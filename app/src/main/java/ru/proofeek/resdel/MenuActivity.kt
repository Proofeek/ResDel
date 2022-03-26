package ru.proofeek.resdel

import android.app.Dialog
import android.content.ContentValues.TAG
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
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
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


class MenuActivity : AppCompatActivity(), NewsAdapter.Listener, BannerAdapter.Listener {

    lateinit var binding: ActivityMenuBinding
    lateinit var  toggle: ActionBarDrawerToggle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(R.layout.activity_menu)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f;

        //getDeviceLocation()
        addFoodMenuItems()
        addBannerItems()
        addNewsItems()
        showDialogLocation()


        //openFrag(NewsFragment.newInstance(), R.id.fra)
    }

    private fun showDialogBanner(item: DataModel){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_news)

        dialog.show()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 1700)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.FragmentAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)

        val imageView = dialog.findViewById<ImageView>(R.id.newsImageFrag)
        val titleView = dialog.findViewById<TextView>(R.id.newsTitleText)
        val textView = dialog.findViewById<TextView>(R.id.newsText)

        imageView.setImageResource(item.image)
        titleView.text = item.title
        textView.text = item.title
    }

    private fun showDialogLocation(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_location)

        dialog.show()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 1800)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.FragmentAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)

        val buttonSave = dialog.findViewById<Button>(R.id.buttonSave)
        val locationText = dialog.findViewById<TextView>(R.id.myLocation)

        buttonSave.setOnClickListener {
            dialog.hide()
        }
    }

    private  fun showDialogNews(result: ResultNews){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_news)

        dialog.show()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 1700)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.FragmentAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)

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


    private fun addNewsItems() {
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        viewModel.getPost()
        viewModel.myResponse.observe(this, androidx.lifecycle.Observer { response ->
            if(response.isSuccessful){

                newsJ = response.body()!!.result.list

                recyclerViewNews = findViewById(R.id.recyclerNews)
                recyclerViewNews.layoutManager = LinearLayoutManager(
                    this@MenuActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )

                newsAdapter = NewsAdapter(applicationContext, newsJ, this)
                recyclerViewNews.adapter = newsAdapter
                Log.e("Response", response.body().toString())

            }else{
                Log.e("Response", response.errorBody().toString())
            }

        })
    }

    private fun addBannerItems(){
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

    private fun addFoodMenuItems() {
        recyclerViewFoodMenu = findViewById(R.id.recyclerFoodMenu)
        recyclerViewFoodMenu.layoutManager = GridLayoutManager(applicationContext, 3)
        foodMenuAdapter = FoodMenuAdapter(applicationContext)
        recyclerViewFoodMenu.adapter = foodMenuAdapter

        dataListFoodMenu.add(DataModel("Закуски", R.drawable.i7508))
        dataListFoodMenu.add(DataModel("Шашлыки", R.drawable.i7577))
        dataListFoodMenu.add(DataModel("Люля-кебаб", R.drawable.i7589))
        dataListFoodMenu.add(DataModel("Хачапури, Ламаджо", R.drawable.i7615))
        dataListFoodMenu.add(DataModel("Рыба на углях", R.drawable.i7643))
        dataListFoodMenu.add(DataModel("Шаурма", R.drawable.i7666))
        dataListFoodMenu.add(DataModel("Люля-кебаб", R.drawable.i7589))
        dataListFoodMenu.add(DataModel("Закуски", R.drawable.i7508))
        dataListFoodMenu.add(DataModel("Шашлыки", R.drawable.i7577))

        foodMenuAdapter.setDataList(dataListFoodMenu)
    }

    private fun openFrag(f: Fragment, idHolder: Int){
        supportFragmentManager
            .beginTransaction()
            .replace(idHolder,f)
            .commit()
        Log.e("FDF","FDF")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the device current location")
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        try{
            val location: Task<Location> = mFusedLocationProviderClient.lastLocation
            location.addOnCompleteListener() {
                if(location.isSuccessful){
                    Log.d(TAG, "onComplete: found location!")
                    val currentLocation: Location = location.result


                    val geocoder = Geocoder(this@MenuActivity, Locale.ROOT)
                    val addresses: List<Address> = geocoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1)

                    //запись адреса в ActionBar
                    supportActionBar?.title="${addresses[0].locality}, ${addresses[0].thoroughfare}, ${addresses[0].featureName}"

                }else{
                    Log.e(TAG, "onComplete: current location is null")
                    //Укажите адрес, если местоположение не удалось найти
                    supportActionBar?.title = resources.getString(R.string.SelectAddress)
                }
            }
        }catch (e: SecurityException){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.message)
        }
    }

    override fun OnClick(item: NewsItem) {
        //Toast.makeText(this, item.id.toString(), Toast.LENGTH_LONG).show()

        //viewModel.myResponse2.value = null
        Log.e("НАЖАОЛ","FGRGRG")
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        viewModel.getPost2(item.id)

        viewModel.myResponse2.observe(this, androidx.lifecycle.Observer { response ->
            if(response.isSuccessful){
                Log.e("Response2: ", response.body().toString())
                showDialogNews(response.body()!!.result)
            }else{
                Log.e("Response", response.errorBody().toString())
            }
        })

        viewModel.myResponse2.value?.body()?.result?.let { showDialogNews(it) }
        Log.e("VALUE: ",viewModel.myResponse2.value.toString())
    }

    override fun OnClick(item: DataModel) {
        showDialogBanner(item)
    }
}