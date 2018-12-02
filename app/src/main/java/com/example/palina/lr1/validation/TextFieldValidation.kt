package com.example.palina.lr1.validation

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class TextFieldValidation (private val textField: EditText?) : TextWatcher {
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable?) {

        isValid = false

        if ((textField?.text.toString()).isEmpty()){
            textField?.error = "This field is required"
            return
        }

        isValid = true
    }

    companion object {
        var isValid = false
    }
}