package com.mydictionary.ui.views.word

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mydictionary.R
import com.mydictionary.commons.inflate

/**
 * Created by Viktoria Chebotar on 25.02.18.
 */
class MeaningsAdapter(private val dataset: List<String>, val type: ViewType) :
        RecyclerView.Adapter<MeaningsAdapter.MeaningItemViewHolder>() {

    enum class ViewType {
        DEFINITION, EXAMPLE
    }

    val reslayout = if (type == ViewType.DEFINITION) R.layout.definitions_list_item else
        R.layout.example_list_item

    override fun onBindViewHolder(holder: MeaningItemViewHolder, position: Int) =
            holder.bind(dataset[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            MeaningItemViewHolder(parent.inflate(reslayout), type)


    override fun getItemCount() = dataset.size


    class MeaningItemViewHolder(itemView: View, val type: ViewType) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: String) {
            if (type == ViewType.EXAMPLE) {
                (itemView as TextView).text = itemView.context.
                        getString(R.string.example_formatted_string, value)
            } else {
                (itemView as TextView).text = value
            }

        }
    }
}
