package com.example.palina.lr1.validation

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class UrlValidation (private val url: EditText?) : TextWatcher {

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable?) {

        isValid = false
        val urlString: String  = url?.text.toString()

        if (urlString.isEmpty()){
            url?.error = "Field is required"
            return
        }

        if (!urlString.matches("^(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?".toRegex())) {
            url?.error = "Invalid url"
            return
        }

        isValid = true
    }

    companion object {
        var isValid = false
    }
}