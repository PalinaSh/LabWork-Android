package com.example.palina.lr1.fragments

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.palina.lr1.R
import com.example.palina.lr1.models.User
import com.example.palina.lr1.utils.AsyncLoader
import com.example.palina.lr1.utils.Constants
import com.example.palina.lr1.utils.DatabaseHelper
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    val db = DatabaseHelper.dataBase

    private val PERMISSIONS_REQUEST_CAMERA = Constants.PERMISSIONS_REQUEST_CAMERA
    private val CAMERA_REQUEST_CODE = Constants.CAMERA_REQUEST_CODE
    private val GALLERY_REQUEST_CODE = Constants.GALLERY_REQUEST_CODE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!db.isAuthUser()) {
            findNavController().navigate(R.id.loginFragment)
            return null
        }
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photo_camera.setOnClickListener{
            showDialog()
        }

        edit_button.setOnClickListener{
            profile_switcher.showNext()
        }

        ok_button.setOnClickListener{
            changeData()
        }

        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Get data...")
        progressDialog.setCancelable(false)
        AsyncLoader(object : AsyncLoader.LoadListener
        {
            override fun onPreExecute() {
                db.getCurrentUser()
                db.getPhoto()
                progressDialog.show()
            }

            override fun onPostExecute() {
                progressDialog.dismiss()
                if (db.getCurrentUser() == null) {
                    Toast.makeText(context, "Fail loading", Toast.LENGTH_SHORT).show()
                    return
                }
                getData()
                downloadPhoto()
                setData()
            }

            override fun doInBackground() {
                while (true) {
                    if ((db.getCurrentUser() != null) and (db.getCurrentAvatar() != null))
                        break
                }
            }
        }).execute()
    }

    private fun getData() {
        val user = db.getCurrentUser()
        name.text = user?.name
        surname.text = user?.surname
        phoneNumber.text = user?.phone
        email.text = user?.email
    }

    private fun downloadPhoto(){
        val downloadPhoto = db.getCurrentAvatar()
        if (downloadPhoto == null)
            avatar.setImageResource(R.drawable.avatar)
        else
            avatar.setImageBitmap(downloadPhoto)
    }

    private fun setData(){
        val user = db.getCurrentUser()
        nameEdit.text = SpannableStringBuilder(user?.name)
        surnameEdit.text = SpannableStringBuilder(user?.surname)
        emailEdit.text = SpannableStringBuilder(user?.email)
        phoneEdit.text = SpannableStringBuilder(user?.phone)
    }

    private fun changeData(destroy : Boolean = false) {
        if (db.getCurrentUser() == null)
            return
        if (!compareUserData(changeUserData(), db.getCurrentUser())) {
            val dialog = AlertDialog.Builder(activity!!)
            dialog.setMessage("Save your changes?")
            dialog.setPositiveButton("Yes") { _, _ ->
                db.changeUser(changeUserData())
                if (!destroy) {
                    profile_switcher.showNext()
                    getData()
                    setData()
                }
            }
            dialog.setNegativeButton("No") { _, _ ->
                if (!destroy)
                    profile_switcher?.showNext()
            }
            dialog.show()
        }
        else
            if (!destroy)
                profile_switcher?.showNext()
    }

    private fun changeUserData() : User {
        return User(nameEdit.text.toString().trim(),
                    surnameEdit.text.toString().trim(),
                    emailEdit.text.toString().trim(),
                    "????",
                    phoneEdit.text.toString().trim())
    }

    private fun compareUserData(newUser: User, currentUser: User?) : Boolean {
        if ((newUser.name == currentUser?.name) and (newUser.surname == currentUser?.surname) and
            (newUser.phone == currentUser?.phone) and (newUser.email == currentUser?.email))
            return true
        return false
    }

    private fun showDialog(){
        val photoMods = arrayOf("Create photo", "Select photo from gallery")
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle("Photo")

        builder.setItems(photoMods) { _, which ->
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
                    db.setPhoto(data.extras!!.get("data") as Bitmap)
                }
            }
            GALLERY_REQUEST_CODE -> {
                if (data != null) {
                    val contentURI = data.data
                    val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, contentURI)
                    avatar?.setImageBitmap(bitmap)
                    db.setPhoto(bitmap)
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
            dialog.setPositiveButton("OK") { _, _ ->
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

    override fun onDestroyView() {
        changeData(true)
        super.onDestroyView()
    }
}
