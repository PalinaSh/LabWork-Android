package com.example.palina.lr1.utils

import android.graphics.Bitmap
import com.example.palina.lr1.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory
import java.io.File

val DATABASE_NAME = "users"
val STORAGE_NAME = "avatars"

class DatabaseHelper private constructor(){

    private var currentUser: User? = null
    private var currentAvatar : Bitmap? = null

    private var db: DatabaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_NAME)
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var storage: StorageReference = FirebaseStorage.getInstance().getReference(STORAGE_NAME)

    var isSignUp: Boolean? = null
    var isSignIn: Boolean? = null
    var isSuccessDownLoadPhoto: Boolean? = null

    init {
        setListenerOnDatabase()
    }

    fun isAuthUser(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUser(): User? {
        return currentUser
    }

    fun getCurrentAvatar() : Bitmap?{
        return currentAvatar
    }

    fun signUser(email: String, password: String){
        isSignIn = null
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            isSignIn = if (task.isSuccessful) {
                setListenerOnDatabase()
                true
            } else {
                false
            }
        }
    }

    fun registerUser(user: User) {
        isSignUp = null
        auth.createUserWithEmailAndPassword(user.email, user.password).addOnCompleteListener { task ->
            isSignUp = if (task.isSuccessful) {
                setListenerOnDatabase()
                signUser(user.email, user.password)
                user.password = "????"
                currentUser = user
                db.child(auth.currentUser!!.uid).setValue(user)
                true
            } else {
                false
            }
        }
    }

    fun signOut() {
        auth.signOut()
        currentUser = null
        currentAvatar = null
    }

    fun changeUser(newUserData: User) {
        if (currentUser?.email != newUserData.email) {
            auth.currentUser?.updateEmail(newUserData.email)
        }

        if (currentUser != newUserData) {
            db.child(auth.currentUser!!.uid).setValue(newUserData)
            currentUser = newUserData
        }
    }

    fun setPhoto(bitmap: Bitmap){
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        storage.child(auth.currentUser!!.uid).putBytes(data)
    }

    fun getPhoto() {
        try {
            val localFile = File.createTempFile("avatar", ".jpg")

            isSuccessDownLoadPhoto = null
            storage.child(auth.currentUser!!.uid).getFile(localFile).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    currentAvatar = BitmapFactory.decodeFile(localFile.absolutePath)
                    isSuccessDownLoadPhoto = true
                }
                else
                    isSuccessDownLoadPhoto = false
            }
        }
        catch (e: Exception) {
            currentAvatar = null
        }
    }

    private fun setListenerOnDatabase() {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (auth.currentUser != null)
                    currentUser = dataSnapshot.child(auth.currentUser!!.uid).getValue(User::class.java)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private object Holder { val INSTANCE = DatabaseHelper() }
    companion object {
        val dataBase: DatabaseHelper by lazy { Holder.INSTANCE }
    }
}