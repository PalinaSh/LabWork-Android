package com.example.palina.lr1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.palina.lr1.utils.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private var nameTextView: TextView? = null
    private var nameEditText: EditText? = null
    private var surnameTextView: TextView? = null
    private var surnameEditText: EditText? = null
    private var phoneTextView: TextView? = null
    private var phoneEditText: EditText? = null
    private var emailTextView: TextView? = null
    private var emailEditText: EditText? = null

    private var updateName: EditText? = null
    private var updateSurname: EditText? = null
    private var updatePhone: EditText? = null
    private var updateEmail: EditText? = null

    private val PERMISSIONS_REQUEST_CAMERA = Constants.PERMISSIONS_REQUEST_CAMERA
    private val CAMERA_REQUEST_CODE = Constants.CAMERA_REQUEST_CODE
    private val GALLERY_REQUEST_CODE = Constants.GALLERY_REQUEST_CODE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameTextView = view.findViewById(R.id.name)
        nameEditText = view.findViewById(R.id.nameEdit)
        surnameTextView = view.findViewById(R.id.surname)
        surnameEditText = view.findViewById(R.id.surnameEdit)
        phoneTextView = view.findViewById(R.id.phoneNumber)
        phoneEditText = view.findViewById(R.id.phoneEdit)
        emailTextView = view.findViewById(R.id.email)
        emailEditText = view.findViewById(R.id.emailEdit)

        updateName = view.findViewById(R.id.nameEdit)
        updateSurname = view.findViewById(R.id.surnameEdit)
        updatePhone = view.findViewById(R.id.phoneEdit)
        updateEmail = view.findViewById(R.id.emailEdit)

        val photoCamera : FloatingActionButton = view.findViewById(R.id.photo_camera)
        photoCamera.setOnClickListener{
            showDialog()
        }

        val viewSwitcher : ViewSwitcher = view.findViewById(R.id.profile_switcher)

        val buttonEdit : Button = view.findViewById(R.id.edit_button)
        buttonEdit.setOnClickListener{
            viewSwitcher.showNext()
        }

        val buttonOk : Button = view.findViewById(R.id.ok_button)
        buttonOk.setOnClickListener{
            val dialog = AlertDialog.Builder(activity!!)
            dialog.setMessage("Save your changes?")
            dialog.setPositiveButton("Yes"){ dialogInterface, which ->
                //db
                viewSwitcher.showNext()
            }
            dialog.setNegativeButton("No"){dialogInterface, which ->
                viewSwitcher.showNext()
            }
            dialog.show()
        }
    }

    fun newProfile(view: View){
        if (!(validateRequired(updateName!!) and validateRequired(updateSurname!!)))
            return

        val name = updateName?.text.toString()
        val surname = updateSurname?.text.toString()
        val phone = updatePhone?.text.toString()
        val email = updateEmail?.text.toString()
        setData(name, surname, phone, email)

        //db?.updateUser(name, surname, phone, email)
    }

    private fun validateRequired(field: EditText): Boolean {
        if (field.text.toString() == "") {
            field.error = "This field is required"
            return false
        }
        return true
    }

    private fun setData(name: String?, surname:String?, phone: String?, email: String?) {
        nameTextView?.text = name
        nameEditText?.setText(name)
        surnameTextView?.text = surname
        surnameEditText?.setText(surname)
        phoneTextView?.text = phone
        phoneEditText?.setText(phone)
        emailTextView?.text = email
        emailEditText?.setText(email)
    }

    private fun showDialog(){
        val photoMods = arrayOf("Create photo", "Select photo from gallery")
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle("Photo")

        builder.setItems(photoMods) { element, which ->
            when (photoMods[which]) {
                "Create photo" -> {
                    if (ContextCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                        showPermissionDialog(
                            Manifest.permission.CAMERA, PERMISSIONS_REQUEST_CAMERA,
                            resources.getString(R.string.permissionTextCamera),
                            resources.getString(R.string.permissionTitle))
                    }
                    else {
                        setPhoto()
                    }
                }
                "Select photo from gallery" -> {
                    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    if (galleryIntent.resolveActivity(activity!!.packageManager) != null)
                        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
                }
            }
        }
        builder.show()
    }

    private fun setPhoto(){
        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (callCameraIntent.resolveActivity(activity!!.packageManager) != null)
            startActivityForResult(callCameraIntent, CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val avatar = view?.findViewById<ImageView>(R.id.avatar)
        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if (data != null){
                    avatar?.setImageBitmap(data.extras!!.get("data") as Bitmap)
                }
            }
            GALLERY_REQUEST_CODE -> {
                if (data != null) {
                    val contentURI = data.data
                    val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, contentURI)
                    avatar?.setImageBitmap(bitmap)
                }
            }
        }
    }

    private fun showPermissionDialog(permission: String, permissionCode: Int,
                                     permissiomText: String, permissionTitle: String){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission)) {
            val dialog = AlertDialog.Builder(activity!!)
            dialog.setMessage(permissiomText)
            dialog.setTitle(permissionTitle)
            dialog.setPositiveButton("OK") { dialogInterface, which ->
                requestPermission(permission, permissionCode)
            }
            dialog.show()
        }
        else {
            requestPermission(permission, permissionCode)
        }
    }

    private fun requestPermission(permission: String, permissionCode: Int){
        requestPermissions(arrayOf(permission), permissionCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CAMERA -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    setPhoto()
                }
            }
        }
    }
}
