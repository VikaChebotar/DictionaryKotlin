package com.mydictionary.presentation.views.wordlist

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mydictionary.R
import com.mydictionary.presentation.utils.inflate
import kotlinx.android.synthetic.main.word_list_item.view.*

class WordListAdapter(val listener: (String) -> Unit) :
    RecyclerView.Adapter<WordListAdapter.WordViewHolder>() {
    private val list = mutableListOf<String>()

    fun setData(data: List<String>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        WordViewHolder(parent.inflate(R.layout.word_list_item))

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) =
        holder.bind(list[position])

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: String) {
            (itemView.word as TextView).text = item
            itemView.setOnClickListener { listener.invoke(list[adapterPosition]) }
        }
    }
}