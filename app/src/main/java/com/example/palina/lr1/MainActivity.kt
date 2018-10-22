package com.example.palina.lr1

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import android.telephony.TelephonyManager
import android.text.Layout
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.util.function.BinaryOperator


class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_READ_PHONE_STATE: Int = 1
    private var imeiTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab: FloatingActionButton? = findViewById(R.id.photo_camera)
        //val versionTextView: TextView = findViewById(R.id.versionTextView)
        //versionTextView.text = BuildConfig.VERSION_NAME

        //imeiTextView = findViewById(R.id.imeiTextView)

        //getDeviceIMEI()
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
                    requestPermission()
                }
                dialog.show()
            }
            else {
                requestPermission()
            }
        }
        else {
            printIMEI()
        }
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                PERMISSIONS_REQUEST_READ_PHONE_STATE)
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED)
            return
        val tel = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val imei = tel.deviceId
        imeiTextView?.text = imei.toString()
    }

    fun editProfile(view: View){
        val editLayout: View = findViewById(R.id.includeLayoutEdit)
        editLayout.visibility = View.VISIBLE
        val profileLayout: View = findViewById(R.id.includeLayoutProfile)
        profileLayout.visibility = View.GONE
    }

    fun newProfile(view: View){
        val updateName: EditText = findViewById(R.id.nameEdit)
        val updateSurname: EditText = findViewById(R.id.surnameEdit)
        val updatePhone: EditText = findViewById(R.id.phoneEdit)
        val updateEmail: EditText = findViewById(R.id.emailEdit)

        if (validateRequired(updateName) and validateRequired(updateSurname))
            return

        val nameTextView: TextView = findViewById(R.id.name)
        nameTextView.text = updateName.text
        val surnameTextView: TextView = findViewById(R.id.surname)
        surnameTextView.text = updateSurname.text
        val phoneTextView: TextView = findViewById(R.id.phoneNumber)
        phoneTextView.text = updatePhone.text
        val emailTextView: TextView = findViewById(R.id.email)
        emailTextView.text = updateEmail.text

        val editLayout: View = findViewById(R.id.includeLayoutEdit)
        editLayout.visibility = View.GONE
        val profileLayout: View = findViewById(R.id.includeLayoutProfile)
        profileLayout.visibility = View.VISIBLE
    }

    private fun validateRequired(field: EditText): Boolean {
        if (field.text.toString() == "") {
            field.error = "This field is required"
            return false
        }
        return true
    }
}
