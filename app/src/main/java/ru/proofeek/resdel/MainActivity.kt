package ru.proofeek.resdel

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class MainActivity : AppCompatActivity() {
    private var mLocationPermissionGranted = false
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private val COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    private val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()


        if(isServicesOk()) getLocationPermission()
    }
    private fun isServicesOk(): Boolean{
        Log.d(ContentValues.TAG,"isServicesOk: checking google services version" )
        val available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        when {
            available == ConnectionResult.SUCCESS -> {
                Log.d(ContentValues.TAG,"isServicesOk: Google Play Services is working")
                return true
            }
            GoogleApiAvailability.getInstance().isUserResolvableError(available) -> {
                Log.d(ContentValues.TAG, "isServiceOk: an error occured, but we can fix it")
            }
            else -> {
                Log.e(ContentValues.TAG,"isServicesOk: Google Play Services is not working")
            }
        }
        return false
    }

    //проверка разрешений
    private fun getLocationPermission(){
        val permissions = arrayOf(COURSE_LOCATION,FINE_LOCATION)
        if(ContextCompat.checkSelfPermission(this.applicationContext,COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.applicationContext,FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true
                //создание карты, если разрешения есть
                startNextActivity()
            }
        }else{
            //если разрешения не даны, то получить их
            ActivityCompat.requestPermissions(this, permissions,LOCATION_PERMISSION_REQUEST_CODE)
        }
    }
    //получение разрешений
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mLocationPermissionGranted = false
        when(requestCode){
            LOCATION_PERMISSION_REQUEST_CODE ->
                if(grantResults.isNotEmpty()){
                    grantResults.forEach { i ->
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false
                            return
                        }
                    }
                    mLocationPermissionGranted = true
                    //созданеие карты, когда разрешения даны
                    startNextActivity()
                }
        }
    }

    private fun startNextActivity(){
        Handler().postDelayed({
            val intent = Intent(this@MainActivity, MenuActivity::class.java)
            startActivity(intent)
            finish()
        },650)
    }
}