package com.example.palina.lr1


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

class AboutFragment : Fragment() {

    private var imeiTextView: TextView? = null
    private val PERMISSIONS_REQUEST_READ_PHONE_STATE: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val versionTextView: TextView = view.findViewById(R.id.versionTextView)
        versionTextView.text = BuildConfig.VERSION_NAME

        imeiTextView = view.findViewById(R.id.imeiTextView)

        getDeviceIMEI()
    }

    private fun getDeviceIMEI(){

        if (ActivityCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity!!,
                    Manifest.permission.READ_PHONE_STATE)) {
                val dialog = AlertDialog.Builder(activity!!)
                dialog.setMessage(resources.getString(R.string.permissionText))
                dialog.setTitle(resources.getString(R.string.permissionTitle))
                dialog.setCancelable(false)
                dialog.setPositiveButton("OK") { dialogInterface, which ->
                    this.requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE),
                        PERMISSIONS_REQUEST_READ_PHONE_STATE)
                }
                dialog.show()
            }
            else {
                this.requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE),
                    PERMISSIONS_REQUEST_READ_PHONE_STATE)
            }
        }
        else {
            printIMEI()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_PHONE_STATE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    printIMEI()
                } else {
                    imeiTextView?.text = resources.getString(R.string.noPermission)
                }
                return
            }
        }
    }

    private fun printIMEI(){
        if (ActivityCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED)
            return
        val tel = activity!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val imei = tel.deviceId
        imeiTextView?.text = imei.toString()
    }
}
