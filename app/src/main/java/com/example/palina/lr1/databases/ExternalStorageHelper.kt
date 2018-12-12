package com.example.palina.lr1.databases

import android.app.Activity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class ExternalStorageHelper {

    companion object {

        fun saveFile(fileName: String, activity: Activity, appendedString: String) {
            try {
                val outputStream = activity.openFileOutput(fileName, Activity.MODE_APPEND)
                val osw = OutputStreamWriter(outputStream)
                osw.append(appendedString + "\n")
                osw.close()
            } catch (t: Throwable) {
            }
        }

        fun openFile(fileName: String, activity: Activity) : ArrayList<String> {
            val urls = ArrayList<String>()
            try {
                val inputStream = activity.openFileInput(fileName)

                if (inputStream != null) {
                    val stream = InputStreamReader(inputStream)
                    val reader = BufferedReader(stream)
                    while (true) {
                        val line = reader.readLine() ?: break
                        urls.add(line)
                    }
                    inputStream.close()
                }
            } catch (t: Throwable) { }
            return urls
        }
    }
}