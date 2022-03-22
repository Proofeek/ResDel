package ru.proofeek.resdel

import android.content.ContentValues.TAG
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import ru.proofeek.resdel.databinding.ActivityMenuBinding
import java.util.*


class MenuActivity : AppCompatActivity() {

    lateinit var binding: ActivityMenuBinding
    lateinit var  toggle: ActionBarDrawerToggle
    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient


    private lateinit var recyclerView: RecyclerView
    private lateinit var foodMenuAdapter: FoodMenuAdapter
    private var dataList = mutableListOf<DataModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(R.layout.activity_menu)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f;

        getDeviceLocation()


        recyclerView = findViewById(R.id.recyclerFoodMenu)


        recyclerView.layoutManager = GridLayoutManager(applicationContext, 2)
        foodMenuAdapter = FoodMenuAdapter(applicationContext)
        recyclerView.adapter = foodMenuAdapter

        dataList.add(DataModel("Title", R.drawable.i7508))
        dataList.add(DataModel("Title", R.drawable.i7577))
        dataList.add(DataModel("Title", R.drawable.i7589))
        dataList.add(DataModel("Title", R.drawable.i7615))
        dataList.add(DataModel("Title", R.drawable.i7643))
        dataList.add(DataModel("Title", R.drawable.i7666))
        dataList.add(DataModel("Title", R.drawable.i7589))
        dataList.add(DataModel("Title", R.drawable.i7615))
        dataList.add(DataModel("Title", R.drawable.i7508))



        foodMenuAdapter.setDataList(dataList)

        //openFrag(FoodMenu.newInstance(), R.id.foodMenuFragment)


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