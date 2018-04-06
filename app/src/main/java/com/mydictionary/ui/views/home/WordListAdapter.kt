package com.mydictionary.ui.views.home

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mydictionary.R
import com.mydictionary.commons.inflate
import com.mydictionary.ui.presenters.home.WordListItem

class WordListAdapter(val listener: (Int, String) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val dataset = mutableListOf<WordListItem>()

    private enum class ViewTypes {
        CATEGORY_NAME, LIST_NAME
    }

    fun setData(list: List<WordListItem>) {
        if (list != dataset) {
            dataset.clear()
            dataset.addAll(list)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (ViewTypes.values()[viewType]) {
            ViewTypes.CATEGORY_NAME -> CategoryItemViewHolder(parent.inflate(R.layout.home_category_list_item))
            ViewTypes.LIST_NAME -> ListItemViewHolder(parent.inflate(R.layout.home_word_list_item))
        }

    override fun getItemCount() = dataset.size

    override fun getItemViewType(position: Int) =
        when (dataset[position]) {
            is WordListItem.ListCategory -> ViewTypes.CATEGORY_NAME.ordinal
            is WordListItem.WordList -> ViewTypes.LIST_NAME.ordinal
        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (holder) {
            is CategoryItemViewHolder -> holder.bind(dataset[position] as WordListItem.ListCategory)
            is ListItemViewHolder -> holder.bind(dataset[position] as WordListItem.WordList)
        }
    }

    inner class ListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: WordListItem.WordList) {
            (itemView as TextView).text = item.name
            itemView.setOnClickListener {
                val data = dataset[adapterPosition] as WordListItem.WordList
                listener.invoke(adapterPosition, data.name)
            }
        }
    }

    inner class CategoryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: WordListItem.ListCategory) {
            (itemView as TextView).text = item.type
        }
    }
}