package com.mydictionary.ui.views.word

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.mydictionary.R
import com.mydictionary.commons.inflate
import com.mydictionary.data.entity.Definition
import kotlinx.android.synthetic.main.definitions_list_item.view.*

/**
 * Created by Viktoria Chebotar on 01.07.17.
 */

class DefinitionsAdapter : RecyclerView.Adapter<DefinitionsAdapter.DefinitionItemViewHolder>() {
    var dataset: List<Definition> = emptyList()

    override fun onBindViewHolder(holder: DefinitionItemViewHolder, position: Int) =
            holder.bind(dataset[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            DefinitionItemViewHolder(parent.inflate(R.layout.definitions_list_item))

    override fun getItemCount() = dataset.size

    class DefinitionItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: Definition) {
            itemView.definition.text = value.definition
            itemView.partOfSpeech.text = value.partOfSpeech
        }
    }
}