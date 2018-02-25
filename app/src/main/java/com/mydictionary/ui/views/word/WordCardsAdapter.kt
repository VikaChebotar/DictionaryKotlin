package com.mydictionary.ui.views.word

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.mydictionary.R
import com.mydictionary.commons.inflate
import com.mydictionary.data.pojo.WordMeaning
import kotlinx.android.synthetic.main.definition_card.view.*

/**
 * Created by Viktoria Chebotar on 01.07.17.
 */

class WordCardsAdapter : RecyclerView.Adapter<WordCardsAdapter.WordCardItemViewHolder>() {
    var dataset: List<WordMeaning> = emptyList()

    override fun onBindViewHolder(holder: WordCardItemViewHolder, position: Int) =
            holder.bind(dataset[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            WordCardItemViewHolder(parent.inflate(R.layout.definition_card))

    override fun getItemCount() = dataset.size

    class WordCardItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: WordMeaning) {
            itemView.partOfSpeech.text = value.partOfSpeech?.toLowerCase()
            //todo optimize
            itemView.definitionsRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            itemView.definitionsRecyclerView.adapter = MeaningsAdapter(value.definitions,
                    MeaningsAdapter.ViewType.DEFINITION)
            itemView.examplesRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            itemView.examplesRecyclerView.adapter = MeaningsAdapter(value.examples,
                    MeaningsAdapter.ViewType.EXAMPLE)
        }
    }
}
