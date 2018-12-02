package com.example.palina.lr1.utils

import com.example.palina.lr1.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


val DATABASE_NAME = "users"

class DatabaseHelper private constructor(){

    private var currentUser: User? = null
    private var db: DatabaseReference

    private var auth: FirebaseAuth? = null
    var isSignUp: Boolean? = null
    var isSignIn: Boolean? = null

    init {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        db = database.getReference(DATABASE_NAME)
        auth = FirebaseAuth.getInstance()
        setListenerOnDatabase()
    }

    fun isAuthUser(): Boolean {
        return auth?.currentUser != null
    }

    fun getCurrentUser(): User? {
        return currentUser
    }

    fun signUser(email: String, password: String){
        isSignIn = null
        auth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener { task ->
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
        auth?.createUserWithEmailAndPassword(user.email, user.password)?.addOnCompleteListener { task ->
            isSignUp = if (task.isSuccessful) {
                setListenerOnDatabase()
                signUser(user.email, user.password)
                user.password = "????"
                currentUser = user
                db.child(auth?.currentUser!!.uid).setValue(user)
                true
            } else {
                false
            }
        }
    }

    fun signOut() {
        auth?.signOut()
        currentUser = null
    }

    fun changeUser(newUserData: User) {
        if (currentUser?.email != newUserData.email) {
            auth?.currentUser?.updateEmail(newUserData.email)
        }

        if (currentUser != newUserData) {
            db.child(auth?.currentUser!!.uid).setValue(newUserData)
            currentUser = newUserData
        }
    }

    private fun setListenerOnDatabase() {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (auth?.currentUser != null)
                    currentUser = dataSnapshot.child(auth?.currentUser!!.uid).getValue(User::class.java)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private object Holder { val INSTANCE = DatabaseHelper() }
    companion object {
        val dataBase: DatabaseHelper by lazy { Holder.INSTANCE }
    }
}