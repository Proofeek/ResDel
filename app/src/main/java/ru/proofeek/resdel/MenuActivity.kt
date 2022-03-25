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
import android.view.Gravity
import android.view.MenuItem
import android.view.ViewGroup
import android.view.Window
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
import com.google.gson.Gson
import ru.proofeek.resdel.databinding.ActivityMenuBinding
import ru.proofeek.resdel.model.NewsItem
import ru.proofeek.resdel.model.Post
import ru.proofeek.resdel.repository.Repository
import java.util.*


class MenuActivity : AppCompatActivity() {

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
        showDiaolg()

        //openFrag(NewsFragment.newInstance(), R.id.fra)
    }

    private  fun showDiaolg(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_news)

        dialog.show()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 1700)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.FragmentAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
    }


    private fun addNewsItems() {
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
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

                newsAdapter = NewsAdapter(applicationContext, newsJ)
                recyclerViewNews.adapter = newsAdapter

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
        bannerAdapter = BannerAdapter(applicationContext)
        recyclerViewBanner.adapter = bannerAdapter

        dataListBanner.add(DataModel("banner", R.drawable.i12))
        dataListBanner.add(DataModel("banner", R.drawable.i12))
        dataListBanner.add(DataModel("banner", R.drawable.i12))
        dataListBanner.add(DataModel("banner", R.drawable.i12))

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
}