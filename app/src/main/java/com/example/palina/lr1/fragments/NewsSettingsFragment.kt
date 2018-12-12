package com.example.palina.lr1.fragments

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.palina.lr1.R
import com.example.palina.lr1.utils.Constants
import com.example.palina.lr1.databases.ExternalStorageHelper.Companion.saveFile
import com.example.palina.lr1.validation.UrlValidation
import kotlinx.android.synthetic.main.fragment_news_settings.*
import java.io.File

class NewsSettingsFragment : Fragment() {

    val packageName: String = "storage"
    private val fileName: String = Constants.STORAGE_FILENAME

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsUrl.addTextChangedListener(UrlValidation(newsUrl))

        urlButtonOk.setOnClickListener {
            if (UrlValidation.isValid) {
                saveFile(fileName, activity!!, newsUrl.text.toString())
                findNavController().navigate(R.id.listUrlFragment)
            }
        }
    }

    private fun write() {
        val dir = "${Environment.getExternalStorageDirectory()}/$packageName"
        File(dir).mkdirs()
        File("$dir/$fileName").outputStream().use {
            it.write("text".toByteArray())
        }
    }


}
