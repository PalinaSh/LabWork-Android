package com.example.palina.lr1

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.palina.lr1.databases.SqLiteHelper
import com.example.palina.lr1.models.RssNew
import com.example.palina.lr1.utils.AsyncLoader
import com.example.palina.lr1.utils.InternetConnectionHelper.Companion.isInternetConnection
import com.example.palina.lr1.utils.RecyclerViewAdapter
import com.example.palina.lr1.utils.XmlToRssFeedParser
import kotlinx.android.synthetic.main.activity_rss_reader.*
import java.net.URL

class RssReaderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rss_reader)

        val urlLink = intent.getStringExtra("selectedUrl")
        val db = SqLiteHelper(this.applicationContext, urlLink)
        val urls = db.readRssNews()

        setDataOnRecyclerView(urls)

        loadNewsFromNetwork(urlLink)
    }

    override fun onBackPressed() {
        startActivity(Intent(this.applicationContext, MainActivity::class.java))
        finish()
        super.onBackPressed()
    }

    private fun loadNewsFromNetwork(urlLink: String){
        if (!isInternetConnection(this.applicationContext)){
            Toast.makeText(this.applicationContext, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Wait")
        progressDialog.setCancelable(false)

        var urls: ArrayList<RssNew>? = null

        AsyncLoader(object : AsyncLoader.LoadListener {
            override fun onPreExecute() {
                progressDialog.show()
            }

            override fun onPostExecute() {
                if (urls != null) {
                    val adapter = RecyclerViewAdapter()
                    adapter.links = urls!!
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(applicationContext)

                    progressDialog.dismiss()

                    if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

                    if (urls?.count()!! >= 10) {
                        val cash = ArrayList<RssNew>()
                        var i = 0
                        while (cash.count() < 10) {
                            cash.add(urls!![i])
                            ++i
                        }
                        val db = SqLiteHelper(applicationContext, urlLink)
                        db.clearDatabase()
                        db.writeRssNews(cash)
                    }
                }
                else
                    Toast.makeText(applicationContext, "RSS url is incorrect", Toast.LENGTH_SHORT).show()

                progressDialog.dismiss()
            }

            override fun doInBackground() {
                try {
                    val url = URL(urlLink)
                    val inputStream = url.openConnection().getInputStream()
                    urls = XmlToRssFeedParser.parse(inputStream)
                }
                catch (e:Exception) {
                    val db = SqLiteHelper(applicationContext, urlLink!!)
                    urls = db.readRssNews()
                }
            }
        }).execute()
    }

    private fun setDataOnRecyclerView(urls: ArrayList<RssNew>?)
    {
        val adapter = RecyclerViewAdapter()
        adapter.links = urls!!
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }
}