package com.mydictionary.ui.views.search

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.mydictionary.R
import com.mydictionary.commons.inflate
import kotlinx.android.synthetic.main.search_list_item.view.*
import java.util.*

/**
 * Created by Viktoria_Chebotar on 6/23/2017.
 */

class SearchResultsAdapter (val listener: (String) -> Unit) : RecyclerView.Adapter<SearchResultsAdapter.SearchItemViewHolder>() {
    private var dataset = ArrayList<String>()
    private var isHistoryList = false

    fun setList(dataset: List<String>, isHistoryList: Boolean = false) {
        this.dataset.clear()
        this.dataset.addAll(dataset)
        this.isHistoryList = isHistoryList
    }

    fun clearList() {
        this.dataset.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            SearchItemViewHolder(parent.inflate(R.layout.search_list_item))

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) = holder.bind(dataset[position], listener)

    override fun getItemCount() = dataset.size


    class SearchItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(itemResult: String, listener: (String) -> Unit) {
            itemView.searchResultItem.text = itemResult
            itemView.setOnClickListener {listener.invoke(itemResult)}
        }
    }
}
