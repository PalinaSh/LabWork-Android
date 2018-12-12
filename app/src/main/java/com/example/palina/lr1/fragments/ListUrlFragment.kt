package com.example.palina.lr1.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.navigation.fragment.findNavController
import com.example.palina.lr1.R
import kotlinx.android.synthetic.main.fragment_list_url.*
import android.widget.ArrayAdapter
import com.example.palina.lr1.utils.Constants
import com.example.palina.lr1.databases.ExternalStorageHelper.Companion.openFile

class ListUrlFragment : Fragment() {

    var urls: ArrayList<String> = ArrayList()

    private val fileName: String = Constants.STORAGE_FILENAME

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_url, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        urls.clear()
        urls = openFile(fileName, activity!!)
        //urls.add("https://lenta.ru/rss/news")
        val adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, urls)
        listUrls.adapter = adapter

        add_url_button.setOnClickListener{
            findNavController().navigate(R.id.newsSettingsFragment)
        }

        listUrls.onItemClickListener = AdapterView.OnItemClickListener{ _, _, position, _ ->
            val bundle = Bundle()
            bundle.putString("selectedUrl", adapter.getItem(position))
            findNavController().navigate(R.id.newsFragment, bundle)
        }
    }
}
