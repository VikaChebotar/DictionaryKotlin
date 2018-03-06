package com.mydictionary.ui.views.word

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mydictionary.R
import com.mydictionary.commons.inflate
import com.mydictionary.data.pojo.Definition
import com.mydictionary.data.pojo.Example

/**
 * Created by Viktoria Chebotar on 25.02.18.
 */
class MeaningsAdapter(private val dataset: List<Any>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class ViewType {
        DEFINITION, EXAMPLE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DefinitionItemViewHolder -> (holder as DefinitionItemViewHolder).bind(dataset[position] as Definition)
            else -> (holder as ExampleItemViewHolder).bind(dataset[position] as Example)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.DEFINITION.ordinal -> DefinitionItemViewHolder(parent.inflate(R.layout.definitions_list_item))
            else -> ExampleItemViewHolder(parent.inflate(R.layout.example_list_item))
        }
    }

    override fun getItemCount() = dataset.size

    override fun getItemViewType(position: Int): Int {
        return when (dataset[position]) {
            is Definition -> ViewType.DEFINITION.ordinal
            else -> ViewType.EXAMPLE.ordinal
        }
    }

    class ExampleItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: Example) {
            (itemView as TextView).text = itemView.context.
                    getString(R.string.example_formatted_string, value.text)
        }
    }

    class DefinitionItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: Definition) {
            (itemView as TextView).text = value.text
        }
    }
}
