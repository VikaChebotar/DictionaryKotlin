package com.mydictionary.ui.views.home

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mydictionary.R
import com.mydictionary.commons.inflate

class WordListAdapter : RecyclerView.Adapter<WordListAdapter.WordViewHolder>() {
    private val dataset = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        WordViewHolder(parent.inflate(R.layout.word_list_item))

    override fun getItemCount() = dataset.size

    override fun onBindViewHolder(holder: WordViewHolder?, position: Int) {
        holder?.bind(dataset[position])
    }

    fun setData(list: List<String>) {
        dataset.clear()
        dataset.addAll(list)
        notifyDataSetChanged()
    }

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(word: String) {
            (itemView as TextView).text = word
        }
    }
}