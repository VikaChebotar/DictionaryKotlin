package com.mydictionary.presentation.views.search

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mydictionary.R
import com.mydictionary.commons.inflate
import kotlinx.android.synthetic.main.search_list_item.view.*
import java.util.*

/**
 * Created by Viktoria_Chebotar on 6/23/2017.
 */

class SearchResultsAdapter(val listener: (String) -> Unit) :
    RecyclerView.Adapter<SearchResultsAdapter.SearchItemViewHolder>() {
    private var dataset = ArrayList<String>()
    private var isHistoryList = false

    fun setList(dataset: List<String>, isHistoryList: Boolean = false) {
        this.dataset.clear()
        this.dataset.addAll(dataset)
        this.isHistoryList = isHistoryList
        notifyDataSetChanged()
    }

    fun clearList() {
        this.dataset.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SearchItemViewHolder(parent.inflate(R.layout.search_list_item))

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) =
        holder.bind(dataset[position], listener)

    override fun getItemCount() = dataset.size


    inner class SearchItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(itemResult: String, listener: (String) -> Unit) {
            itemView.searchResultItem.text = itemResult
            itemView.setOnClickListener { listener.invoke(itemResult) }
            val searchIcon =
                if (isHistoryList) R.drawable.ic_access_time_black_24dp else R.drawable.ic_search_black_24dp
            (itemView as TextView).setCompoundDrawablesWithIntrinsicBounds(searchIcon, 0, 0, 0)
        }
    }
}
