package com.example.palina.lr1.validation

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class PasswordValidation (private val password: EditText?) : TextWatcher {

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable?) {

        isValid = false
        val passwordString: String  = password?.text.toString()

        if (passwordString.isEmpty()){
            password?.error = "Password is required"
            return
        }

        if (passwordString.length < 6) {
            password?.error = "Invalid password"
            return
        }

        isValid = true
    }

    companion object {
        var isValid = false
    }
}