package com.example.palina.lr1.utils

import android.app.Activity
import android.net.Uri
import androidx.navigation.NavController
import com.example.palina.lr1.R
import java.util.regex.Matcher
import java.util.regex.Pattern

class DeepLinksHelper {
    companion object {
        fun uriNavigate(navController: NavController, activity: Activity){
            val data : Uri? = activity.intent.data
            val lastPathSegment : String? = data?.lastPathSegment
            val pattern : Pattern = Pattern.compile("^/page/\\d*$")

            var pageNumber = 0
            try {
                val matcher : Matcher = pattern.matcher(data?.path)
                if (matcher.matches())
                    pageNumber = lastPathSegment!!.toInt()
            }
            catch (e : NumberFormatException) {}
            catch (e : NullPointerException) {}

            when (pageNumber){
                0 -> navController.navigate(R.id.loginFragment)
                1 -> navController.navigate(R.id.registerFragment)
                2 -> navController.navigate(R.id.homeFragment)
                3 -> navController.navigate(R.id.emptyFragment)
                4 -> navController.navigate(R.id.empty1Fragment)
                5 -> navController.navigate(R.id.aboutFragment)
            }
        }
    }
}