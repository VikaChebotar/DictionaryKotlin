package com.mydictionary.ui.views.word

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.mydictionary.R
import com.mydictionary.commons.inflate
import kotlinx.android.synthetic.main.example_list_item.view.*

/**
 * Created by Viktoria Chebotar on 01.07.17.
 */

class ExamplesAdapter : RecyclerView.Adapter<ExamplesAdapter.ExampleViewHolder>() {
    var dataset: List<String> = emptyList()

    override fun getItemCount() = dataset.size

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) =
            holder.bind(dataset[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ExampleViewHolder(parent.inflate(R.layout.example_list_item))

    class ExampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: String) {
            itemView.exampleText.text = value
        }
    }
}
