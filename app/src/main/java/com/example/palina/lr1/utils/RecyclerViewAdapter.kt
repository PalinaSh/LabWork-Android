package com.example.palina.lr1.utils

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.palina.lr1.R
import com.example.palina.lr1.WebViewActivity
import com.example.palina.lr1.models.RssNew
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class RecyclerViewAdapter: RecyclerView.Adapter<ViewHolder>() {

    var links: ArrayList<RssNew> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardItem = LayoutInflater.from(parent.context).inflate(R.layout.new_card, parent, false)
        return ViewHolder(cardItem, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentLink = links[position]
        holder.updateWithPage(currentLink)
    }

    override fun getItemCount(): Int {
        return links.size
    }
}

class ViewHolder constructor(view: View, var context: Context) : RecyclerView.ViewHolder(view), View.OnClickListener {
    private val title: TextView = view.findViewById(R.id.cardTitle)
    private val description: TextView = view.findViewById(R.id.cardContent)
    private val date: TextView = view.findViewById(R.id.cardDate)
    private val image: ImageView = view.findViewById(R.id.cardImage)
    private var url: String? = null

    override fun onClick(v: View) {
        val intent = Intent(context, WebViewActivity::class.java)
        intent.putExtra("link", this.url)
        startActivity(context, intent,null)
    }

    init {
        view.setOnClickListener(this)
    }

    fun updateWithPage(news: RssNew){
        title.text = news.title
        description.text = news.description
        date.text = getDateFormat(news.date)
        url = news.url

        if (news.description.length < 70)
            description.text = news.description
        else {
            val cutDescription = news.description.substring(0, 70) + ".."
            description.text = cutDescription
        }

        if(news.image != "")
            Picasso.with(itemView.context).load(news.image).into(image)
    }

    private fun getDateFormat(pubDate: String): String{
        var format = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)
        val neededFormat = SimpleDateFormat("dd-MM-yyyy, h:mm a", Locale.ENGLISH)
        return try {
            val date = format.parse(pubDate)
            neededFormat.timeZone = TimeZone.getTimeZone("GMT+3")
            neededFormat.format(date)
        } catch (e: Exception) {
            format = SimpleDateFormat("yyyy-MM-ddTHH:mm:ss-zz:zz", Locale.ENGLISH)
            val date = format.parse(pubDate)
            neededFormat.timeZone = TimeZone.getTimeZone("GMT+3")
            neededFormat.format(date)
            pubDate
        } catch (e: Exception){
            pubDate
        }
    }
}