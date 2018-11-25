package com.example.palina.lr1.utils

import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity

public class PermissionHelper {
    public val PERMISSIONS_REQUEST_READ_PHONE_STATE: Int = 1

    public fun showPermissionDialog(permission: String, permissionCode: Int,
                                    permissiomText: String, permissionTitle: String,
                                    activity: FragmentActivity?){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission)) {
            val dialog = AlertDialog.Builder(activity!!.applicationContext)
            dialog.setMessage(permissiomText)
            dialog.setTitle(permissionTitle)
            dialog.setCancelable(true)
            dialog.setPositiveButton("OK") { dialogInterface, which ->
                requestPermission(permission, permissionCode, activity)
            }
            dialog.show()
        }
        else {
            requestPermission(permission, permissionCode, activity)
        }
    }

    private fun requestPermission(permission: String, permissionCode: Int, activity: FragmentActivity?){
        activity!!.requestPermissions(arrayOf(permission), permissionCode)
    }

    /*public fun onRequestPermissionsResult(activity: FragmentActivity?, requestCode: Int,
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
    }*/
}