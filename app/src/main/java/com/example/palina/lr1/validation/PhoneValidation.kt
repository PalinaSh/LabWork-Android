package com.example.palina.lr1.validation

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class PhoneValidation (val phone: EditText?) : TextWatcher {

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable?) {

        isValid = false
        val phoneString : String = phone?.text.toString()

        if (!phoneString.matches("^\\++[0-9]{12}\$".toRegex())){
            phone?.error = "Invalid phone"
        }

        if (phoneString.isEmpty()){
            phone?.error = "Phone is required"
            return
        }

        isValid = true
    }

    companion object {
        var isValid = false
    }
}