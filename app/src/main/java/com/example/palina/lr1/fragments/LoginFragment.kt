package com.example.palina.lr1.fragments


import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.palina.lr1.R
import androidx.navigation.fragment.findNavController
import com.example.palina.lr1.MainActivity
import com.example.palina.lr1.utils.AsyncLoader
import com.example.palina.lr1.utils.DatabaseHelper
import com.example.palina.lr1.utils.LayoutHelper
import com.example.palina.lr1.validation.EmailValidation
import com.example.palina.lr1.validation.PasswordValidation
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {

    private val db = DatabaseHelper.dataBase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        LayoutHelper.SetLayoutHeight(activity, view, R.id.loginRelativeLayout)

        loginEmail.addTextChangedListener(EmailValidation(loginEmail))
        loginPassword.addTextChangedListener(PasswordValidation(loginPassword))

        registerButtonInLogin?.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }

        loginButton?.setOnClickListener{
            if (EmailValidation.isValid && PasswordValidation.isValid) {
                val progressDialog = ProgressDialog(context)
                progressDialog.setMessage("Sign in...")
                progressDialog.setCancelable(false)

                AsyncLoader(object : AsyncLoader.LoadListener
                {
                    override fun onPreExecute() {
                        db.signUser(loginEmail.text.toString().trim(), loginPassword.text.toString().trim())
                        progressDialog.show()
                    }

                    override fun onPostExecute() {
                        progressDialog.dismiss()
                        if (db.isSignIn == true) {
                            activity!!.finish()
                            startActivity(Intent(context, MainActivity::class.java))
                            return
                        }
                        else
                            Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }

                    override fun doInBackground() {
                        while (true) {
                            if (db.isSignIn == false)
                                break
                            if ((db.isSignIn == true) and (db.getCurrentUser() != null))
                                break
                        }
                    }
                }).execute()
            }
        }
    }


}
