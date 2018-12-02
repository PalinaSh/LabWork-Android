package com.example.palina.lr1.validation

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class EmailValidation (val email: EditText?) : TextWatcher {

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable?) {

        isValid = false
        val emailString: String  = email?.text.toString()

        if (emailString.isEmpty()){
            email?.error = "Email is required"
            return
        }

        if (!emailString.matches("^([a-z0-9_-]+\\.)*[a-z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}\$".toRegex())) {
            email?.error = "Invalid email"
            return
        }

        isValid = true
    }

    companion object {
        var isValid = false
    }
}
