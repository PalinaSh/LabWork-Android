package com.example.palina.lr1.utils

import android.content.Context
import android.net.ConnectivityManager

class InternetConnectionHelper {
    companion object {
        fun isInternetConnection(context: Context?): Boolean {
            val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }
    }
}