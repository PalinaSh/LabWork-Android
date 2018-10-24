package com.example.palina.lr1

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import android.telephony.TelephonyManager
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val CAMERA_REQUEST_CODE = 0

    private val PERMISSIONS_REQUEST_READ_PHONE_STATE: Int = 1
    private var imeiTextView: TextView? = null
    private var bottomNavigation: BottomNavigationView? = null
    private var db: DataBaseHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DataBaseHandler(this)
        //db?.clearDatabase()
        var result = db?.readData()
        if (result == null){
            db?.insertUser("New_name", "New_surname", "000", "Empty")
            result = db?.readData()
        }
        setData(result?.get("Name"), result?.get("Surname"), result?.get("Phone"), result?.get("Email"))

        val photo = db?.getPhoto()
        if (photo != null){
            avatar.setImageBitmap(photo)
        }

        photo_camera.setOnClickListener{
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (callCameraIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(callCameraIntent, CAMERA_REQUEST_CODE)
            }
        }
        /*bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation?.setOnNavigationItemReselectedListener {
            when (it.itemId){
                R.id.action_empty1 -> {

                }
                R.id.action_empty2 -> {

                }
                R.id.action_profile -> {

                }
            }
        }*/

        //val fab: FloatingActionButton? = findViewById(R.id.photo_camera)
        //val versionTextView: TextView = findViewById(R.id.versionTextView)
        //versionTextView.text = BuildConfig.VERSION_NAME

        //imeiTextView = findViewById(R.id.imeiTextView)

        //getDeviceIMEI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if (data != null){
                    avatar.setImageBitmap(data.extras.get("data") as Bitmap)
                    db?.updatePhoto(data.extras.get("data") as Bitmap)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.about_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.about_button -> {

            }
        }
        return if (item.itemId == R.id.about_button) {
            true
        } else super.onOptionsItemSelected(item)
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

        if (!(validateRequired(updateName) and validateRequired(updateSurname)))
            return

        val name = updateName.text.toString()
        val surname = updateSurname.text.toString()
        val phone = updatePhone.text.toString()
        val email = updateEmail.text.toString()
        setData(name, surname, phone, email)

        db?.updateUser(name, surname, phone, email)

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

    private fun setData(name: String?, surname:String?, phone: String?, email: String?) {
        val nameTextView: TextView = findViewById(R.id.name)
        nameTextView.text = name
        val nameEditText: EditText = findViewById(R.id.nameEdit)
        nameEditText.setText(name)

        val surnameTextView: TextView = findViewById(R.id.surname)
        surnameTextView.text = surname
        val surnameEditText: EditText = findViewById(R.id.surnameEdit)
        surnameEditText.setText(surname)

        val phoneTextView: TextView = findViewById(R.id.phoneNumber)
        phoneTextView.text = phone
        val phoneEditText: EditText = findViewById(R.id.phoneEdit)
        phoneEditText.setText(phone)

        val emailTextView: TextView = findViewById(R.id.email)
        emailTextView.text = email
        val emailEditText: EditText = findViewById(R.id.emailEdit)
        emailEditText.setText(email)
    }
}
