package ru.proofeek.resdel

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import ru.proofeek.resdel.model.Post2


var mLocationPermissionGranted = false
val LOCATION_PERMISSION_REQUEST_CODE = 1
val COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

fun Activity.isServicesOk(): Boolean{
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
fun Activity.getLocationPermission(): Boolean{
    val permissions = arrayOf(COURSE_LOCATION,FINE_LOCATION)
    if(ContextCompat.checkSelfPermission(this.applicationContext,COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
        if(ContextCompat.checkSelfPermission(this.applicationContext,FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mLocationPermissionGranted = true
            return true
        }
    }else{
        ActivityCompat.requestPermissions(this, permissions,LOCATION_PERMISSION_REQUEST_CODE)
    }
    return false
}

fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivityManager != null) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
    }
    return false
}
