package com.example.palina.lr1

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.telephony.TelephonyManager
import android.widget.TextView


class MainActivity : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_READ_PHONE_STATE: Int = 1
    private var imeiTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val versionTextView: TextView = findViewById(R.id.versionTextView)
        versionTextView.text = BuildConfig.VERSION_NAME

        imeiTextView = findViewById(R.id.imeiTextView)

        getDeviceIMEI()
    }

    private fun getDeviceIMEI(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_PHONE_STATE)) {
                val dialog = AlertDialog.Builder(this)
                dialog.setMessage(resources.getString(R.string.permissionText))
                dialog.setTitle(resources.getString(R.string.permissionTitle))
                dialog.setCancelable(false)
                dialog.setPositiveButton("OK") { dialogInterface, which ->
                    ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.READ_PHONE_STATE),
                            PERMISSIONS_REQUEST_READ_PHONE_STATE)
                }
                dialog.show()
            }
            else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_PHONE_STATE),
                        PERMISSIONS_REQUEST_READ_PHONE_STATE)
            }
        }
        else {
            showIMEI()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_PHONE_STATE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    showIMEI()
                } else {
                    imeiTextView?.text = resources.getString(R.string.noPermission)
                }
                return
            }


        }
    }

    private fun showIMEI(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                !=PackageManager.PERMISSION_GRANTED)
            return
        val tel = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val imei = tel.deviceId
        imeiTextView?.text = imei
    }
}
