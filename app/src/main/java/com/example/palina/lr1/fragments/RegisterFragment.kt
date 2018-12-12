package com.example.palina.lr1.fragments


import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.palina.lr1.MainActivity
import com.example.palina.lr1.R
import com.example.palina.lr1.models.User
import com.example.palina.lr1.utils.AsyncLoader
import com.example.palina.lr1.databases.DatabaseHelper
import com.example.palina.lr1.validation.EmailValidation
import com.example.palina.lr1.validation.PasswordValidation
import com.example.palina.lr1.validation.PhoneValidation
import com.example.palina.lr1.validation.TextFieldValidation
import kotlinx.android.synthetic.main.fragment_register.*


class RegisterFragment : Fragment() {
    private val db = DatabaseHelper.dataBase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerName.addTextChangedListener(TextFieldValidation(registerName))
        registerSurname.addTextChangedListener(TextFieldValidation(registerSurname))
        registerPhone.addTextChangedListener(PhoneValidation(registerPhone))
        registerEmail.addTextChangedListener(EmailValidation(registerEmail))
        registerPassword.addTextChangedListener(PasswordValidation(registerPassword))

        registerButton.setOnClickListener {
            if (TextFieldValidation.isValid && PhoneValidation.isValid &&
                    EmailValidation.isValid && PasswordValidation.isValid) {
                val user = User(
                    registerName.text.toString().trim(), registerSurname.text.toString().trim(),
                    registerEmail.text.toString().trim(), registerPassword.text.toString().trim(),
                    registerPhone.text.toString().trim()
                )

                val progressDialog = ProgressDialog(context)
                progressDialog.setMessage("User registration...")
                progressDialog.setCancelable(false)

                AsyncLoader(object : AsyncLoader.LoadListener
                {
                    override fun onPreExecute() {
                        db.registerUser(user)
                        progressDialog.show()
                    }

                    override fun onPostExecute() {
                        progressDialog.dismiss()
                        if (db.isSignUp == true) {
                            activity!!.finish()
                            startActivity(Intent(context, MainActivity::class.java))
                            return
                        }
                        else
                            Toast.makeText(context, "Invalid data", Toast.LENGTH_SHORT).show()
                    }

                    override fun doInBackground() {
                        while (true) {
                            if (db.isSignUp == false)
                                break
                            if ((db.isSignUp == true) and (db.getCurrentUser() != null))
                                break
                        }
                    }
                }).execute()
            }
        }
    }
}
