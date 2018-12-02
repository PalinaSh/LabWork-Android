package com.example.palina.lr1.utils

import android.app.Activity
import android.util.DisplayMetrics
import android.view.View
import android.widget.RelativeLayout

class LayoutHelper {
    companion object {
        fun SetLayoutHeight(activity: Activity?, view: View, id: Int){

            val displayMetrics = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val height : Int = displayMetrics.heightPixels - 470

            val relativeLayout : RelativeLayout = view.findViewById(id)
            relativeLayout.layoutParams.height = height
        }

    }
}