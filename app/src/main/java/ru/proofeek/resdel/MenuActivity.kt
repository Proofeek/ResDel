package ru.proofeek.resdel

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.squareup.picasso.Picasso
import ru.proofeek.resdel.databinding.ActivityMenuBinding
import ru.proofeek.resdel.model.NewsItem
import ru.proofeek.resdel.model.ResultNews
import ru.proofeek.resdel.repository.Repository
import java.util.*


class MenuActivity : AppCompatActivity(), NewsAdapter.Listener, BannerAdapter.Listener, FoodMenuAdapter.Listener {

    lateinit var binding: ActivityMenuBinding
    lateinit var  actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var recyclerViewFoodMenu: RecyclerView
    private lateinit var foodMenuAdapter: FoodMenuAdapter
    private var dataListFoodMenu = mutableListOf<DataModel>()

    private lateinit var recyclerViewBanner: RecyclerView
    private lateinit var bannerAdapter: BannerAdapter
    private var dataListBanner = mutableListOf<DataModel>()

    private lateinit var recyclerViewNews: RecyclerView
    private lateinit var newsAdapter: NewsAdapter

    private lateinit var newsJ: ArrayList<NewsItem>
    private lateinit var viewModel: MainViewModel

    private lateinit var myLocation: String
    private var locationTitle: TextView? = null
    private var locationText: TextView? = null

    private val newsDialogHeightPercentage: Int = 90
    private val settingsDialogHeightPercentage: Int = 95

    private var widthBool: Boolean = false

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(R.layout.activity_menu)

        myLocation = resources.getString(R.string.SelectAddress)

        if(isOnline(this)) getDeviceLocation()
        addCustomActionBar()
        addFoodMenuItemsToRecyclerView()
        addBannerItemsToRecyclerView()
        newsObserver()
    }

    /**
     * ?????????????????? ?????????????????? ???????? ?? ?????????????????? layout ?? ActionBar
     */
    private fun addCustomActionBar(){
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        supportActionBar?.elevation = 0f
        this.supportActionBar!!.displayOptions =
            androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setCustomView(R.layout.custom_actionbar)
        val view: View = supportActionBar!!.customView
        locationTitle = view.findViewById(R.id.locationTitle)
        val locationLayout = view.findViewById<ConstraintLayout>(R.id.layoutTextArrow)
        locationLayout.setOnClickListener{
            if(isOpenRecently()) return@setOnClickListener
            showDialogLocation()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * ?????????????? ?? ?????????????????? ???????? ??????????????, ?????????????????? ?? ???????? [layout]
     */
    private fun createDialog(heightPercentage: Int, layout: Int): Dialog{
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(layout)

        val height = (resources.displayMetrics.heightPixels * heightPercentage * 0.01).toInt()

        dialog.show()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.FragmentAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
        return dialog
    }

    /**
     * ?????????????????? [item] ?? ???????? ??????????????
     */
    private fun showDialogBanner(item: DataModel){
        val dialog = createDialog(newsDialogHeightPercentage, R.layout.fragment_news)

        val imageView = dialog.findViewById<ImageView>(R.id.newsImageFrag)
        val titleView = dialog.findViewById<TextView>(R.id.newsTitleText)
        val textView = dialog.findViewById<TextView>(R.id.newsText)

        imageView.setImageResource(item.image)
        titleView.text = item.title
        textView.text = item.title
    }

    /**
     * ?????????????????? [result] ?? ???????? ??????????????
     */
    private  fun showDialogNews(result: ResultNews){
        val dialog = createDialog(newsDialogHeightPercentage, R.layout.fragment_news)

        val imageView = dialog.findViewById<ImageView>(R.id.newsImageFrag)
        val titleView = dialog.findViewById<TextView>(R.id.newsTitleText)
        val textView = dialog.findViewById<TextView>(R.id.newsText)

        titleView.text = result.name
        textView.text = result.description
        if(result.logo.isNotEmpty()) {
            Picasso.get()
                .load(result.logo)
                .into(imageView)
        }
    }

    /**
     * ?????????????????? ???????????????????? ?? ???????? ?????????????? ???????????? ??????????????
     */
    private fun showDialogLocation(){
        val dialog = createDialog(settingsDialogHeightPercentage, R.layout.fragment_location)

        val buttonSave = dialog.findViewById<Button>(R.id.buttonSave)
        locationText = dialog.findViewById(R.id.myLocation)

        if(isOnline(this)) {
            if (isServicesOk()) {
                getLocationPermission()
            }
        }else toastWeNeedInternet()

        locationText?.text = myLocation

        buttonSave.setOnClickListener {
            dialog.hide()
        }
    }

    /**
     * ???????????????????? ???????????? ???? ???????????? ?? ?????????????????? ??????????. ?????????????????? ?????????????? ???? ???????????? ?? RecyclerView
     * @param widthFood ???????????? CardView ?????????????? (?? ????????????????)
     */
    private fun addNewsItemsToRecyclerView(widthFood:Int) {
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        if(isOnline(this)) viewModel.getPost()
        else toastWeNeedInternet()
        viewModel.myResponse.observe(this, androidx.lifecycle.Observer { response ->
            if(response.isSuccessful){

                newsJ = response.body()!!.result.list

                recyclerViewNews = findViewById(R.id.recyclerNews)
                recyclerViewNews.layoutManager = LinearLayoutManager(
                    this@MenuActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )

                newsAdapter = NewsAdapter(applicationContext, newsJ, this, widthFood)
                recyclerViewNews.adapter = newsAdapter
                Log.e("Response", response.body().toString())

            }else{
                Log.e("Response", response.errorBody().toString())
            }

        })
    }

    /**
     * ?????????????????? ?? RecyclerView ??????????????
     */
    private fun addBannerItemsToRecyclerView(){
        recyclerViewBanner = findViewById(R.id.recyclerBanner)
        recyclerViewBanner.layoutManager = LinearLayoutManager(
            this@MenuActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        bannerAdapter = BannerAdapter(applicationContext, this)
        recyclerViewBanner.adapter = bannerAdapter

        dataListBanner.add(DataModel("banner1", R.drawable.i12))
        dataListBanner.add(DataModel("banner2", R.drawable.i12))
        dataListBanner.add(DataModel("banner3", R.drawable.i12))
        dataListBanner.add(DataModel("banner4", R.drawable.i12))

        bannerAdapter.setDataList(dataListBanner)
    }

    /**
     * ?????????????????? ???????????? "????????" ?? RecyclerView
     */
    private fun addFoodMenuItemsToRecyclerView() {
        recyclerViewFoodMenu = findViewById(R.id.recyclerFoodMenu)
        recyclerViewFoodMenu.layoutManager = GridLayoutManager(applicationContext, 3)
        foodMenuAdapter = FoodMenuAdapter(applicationContext, this)
        recyclerViewFoodMenu.adapter = foodMenuAdapter

        recyclerViewFoodMenu.addItemDecoration(
            GridSpacingItemDecoration(
                3,
                resources.getDimension(R.dimen.food_item_spacing).toInt(),
                false
            )
        )

        dataListFoodMenu.add(DataModel("??????????????", R.drawable.i7508))
        dataListFoodMenu.add(DataModel("??????????????", R.drawable.i7577))
        dataListFoodMenu.add(DataModel("????????-??????????", R.drawable.i7589))
        dataListFoodMenu.add(DataModel("????????????????, ??????????????", R.drawable.i7615))
        dataListFoodMenu.add(DataModel("???????? ???? ??????????", R.drawable.i7643))
        dataListFoodMenu.add(DataModel("????????????", R.drawable.i7666))
        dataListFoodMenu.add(DataModel("????????-??????????", R.drawable.i7589))
        dataListFoodMenu.add(DataModel("??????????????", R.drawable.i7508))
        dataListFoodMenu.add(DataModel("??????????????", R.drawable.i7577))

        foodMenuAdapter.setDataList(dataListFoodMenu)
    }

    /**
     * ?????????????????? observer, ?????????????? ?????? ???????????? ?? ?????????????? ?????????????????? [showDialogNews]
     */
    private fun newsObserver(){
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        viewModel.myResponse2.observe(this, androidx.lifecycle.Observer { response ->
            if (response.isSuccessful) {
                showDialogNews(response.body()!!.result)
            } else {
                Log.e("Response", response.errorBody().toString())
            }
        })
        viewModel.myResponse2.value?.body()?.result?.let { showDialogNews(it) }
        Log.e("VALUE: ",viewModel.myResponse2.value.toString())
    }

    /**
     * ???????????????? ?????????????????? ?????????????????? ???????????????????????????? ????????????????????, ???????????????????? ?????? ?? ActionBar
     */
    private fun getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the device current location")
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        try{
            val location: Task<Location> = mFusedLocationProviderClient.lastLocation
            location.addOnCompleteListener() {
                if(location.isSuccessful && location.result != null){
                    Log.d(TAG, "onComplete: found location!")
                    val currentLocation: Location = location.result

                    val geocoder = Geocoder(this@MenuActivity, Locale.ROOT)
                    val addresses: List<Address> = geocoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1)

                    //???????????? ???????????? ?? ActionBar
                    myLocation="${addresses[0].locality}, ${addresses[0].thoroughfare}, ${addresses[0].featureName}"
                    locationTitle?.text = myLocation
                    locationText?.text = myLocation
                }else{
                    Log.e(TAG, "onComplete: current location is null")
                }
            }
        }catch (e: SecurityException){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.message)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            LOCATION_PERMISSION_REQUEST_CODE ->
                if(grantResults.isNotEmpty()){
                    if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                        Log.e("PErmission", "  ???? ??????????????")
                    }else getDeviceLocation()
                }
        }
    }

    /**
     * ???????????????????? ???????????? ??????????????, ?????????? ?????????????????? observer ?? [newsObserver]
     * @param item ??????????????, ???? ?????????????? ?????????? ????????????????????????
     */
    override fun OnClick(item: NewsItem) {
        if(isOpenRecently()) return
        if(isOnline(this)) viewModel.getPost2(item.id)
        else toastWeNeedInternet()
    }

    /**
     * ?????????????????? [showDialogBanner]
     * @param item ????????????, ???? ?????????????? ?????????? ????????????????????????
     */
    override fun OnClick(item: DataModel) {
        if(isOpenRecently()) return
        showDialogBanner(item)
    }

    /**
     * ???????? ?????? ?????????????????? [addNewsItemsToRecyclerView]
     * @param width ???????????? ???????????????? ???????? ???? RecyclerView ????????
     */
    override fun getWidth(width: Int): Int {
        if(!widthBool){
            addNewsItemsToRecyclerView(width)
            widthBool=true
        }
        return super.getWidth(width)
    }


    private fun toastWeNeedInternet(){
        Toast.makeText(this, resources.getString(R.string.weNeedInternet), Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}