package com.example.palina.lr1.fragments

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.palina.lr1.R
import com.example.palina.lr1.databases.SqLiteHelper
import com.example.palina.lr1.models.RssNew
import com.example.palina.lr1.utils.AsyncLoader
import com.example.palina.lr1.utils.RecyclerViewAdapter
import com.example.palina.lr1.utils.XmlToRssFeedParser
import kotlinx.android.synthetic.main.fragment_news.*
import java.net.URL

class NewsFragment : Fragment() {

    private var db : SqLiteHelper? = null
    private var urlLink : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        urlLink =  arguments?.getString("selectedUrl")
        var urls: ArrayList<RssNew>? = null
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Wait")
        progressDialog.setCancelable(false)

        AsyncLoader(object : AsyncLoader.LoadListener
        {
            override fun onPreExecute() {
                progressDialog.show()
            }

            override fun onPostExecute() {
                val adapter = RecyclerViewAdapter()
                adapter.links = urls!!
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(context)

                progressDialog.dismiss()

                if (urls?.count()!! >= 10) {
                    val cash = ArrayList<RssNew>()
                    var i = 0
                    while (cash.count() < 10) {
                        cash.add(urls!![i])
                        ++i
                    }
                    db = SqLiteHelper(context!!, urlLink!!)
                    db?.clearDatabase()
                    db?.writeRssNews(cash)
                }
            }

            override fun doInBackground() {
                try {
                    val url = URL(urlLink)
                    val inputStream = url.openConnection().getInputStream()
                    urls = XmlToRssFeedParser.parse(inputStream)
                }
                catch (e:Exception) {
                    db = SqLiteHelper(context!!, urlLink!!)
                    urls = db?.readRssNews()
                }
            }
        }).execute()
    }
}
