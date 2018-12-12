package com.example.palina.lr1.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.palina.lr1.R
import com.example.palina.lr1.models.RssNew

class RecyclerViewAdapter: RecyclerView.Adapter<ViewHolder>() {

    var links: ArrayList<RssNew> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardItem = LayoutInflater.from(parent.context).inflate(R.layout.new_card, parent, false)
        return ViewHolder(cardItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentLink = links[position]
        holder.updateWithPage(currentLink)

    }

    override fun getItemCount(): Int {
        return links.size
    }
}

class ViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
    private val linkView: TextView = view.findViewById(R.id.cardDate)
    private val titleView: TextView = view.findViewById(R.id.cardTitle)
    private val descriptionView: TextView = view.findViewById(R.id.cardContent)

    fun updateWithPage(news: RssNew){
        linkView.text = news.url
        titleView.text = news.title
        descriptionView.text = news.description
    }
}