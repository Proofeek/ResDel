package ru.proofeek.resdel

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        val internetText = findViewById<TextView>(R.id.internetText)
        internetText.text = ""

        if(isOnline(this)){
            if(isServicesOk()){
                if(getLocationPermission()){
                    startNextActivity()
                }
            }
        }else{
            internetText.text = resources.getString(R.string.weNeedInternet)
        }

    }

    //получение разрешений
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
                        startNextActivity()
                    }
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