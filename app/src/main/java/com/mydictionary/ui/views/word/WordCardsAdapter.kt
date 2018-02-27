package com.mydictionary.ui.views.word

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.mydictionary.R
import com.mydictionary.commons.containsWhiteSpace
import com.mydictionary.commons.inflate
import com.mydictionary.data.pojo.Note
import com.mydictionary.data.pojo.WordMeaning
import kotlinx.android.synthetic.main.definition_card.view.*
import kotlinx.android.synthetic.main.related_word_card.view.*
import kotlinx.android.synthetic.main.related_word_item.view.*
import kotlinx.android.synthetic.main.word_header_list_item.view.*
import kotlinx.android.synthetic.main.word_notes_list_item.view.*

/**
 * Created by Viktoria Chebotar on 01.07.17.
 */

class WordCardsAdapter(val listener: ViewClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var dataset: List<Any> = emptyList()

    enum class ViewTypes {
        HEADER, WORD_MEANING, RELATED_WORDS, NOTES
    }

    interface ViewClickListener {
        fun onRelatedWordClicked(item: String)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderItemViewHolder -> holder.bind(dataset[position] as String)
            is WordCardItemViewHolder -> holder.bind(dataset[position] as WordMeaning)
            is NotesItemViewHolder -> holder.bind(dataset[position] as Note)
            is RelatedWordsItemViewHolder -> holder.bind(dataset[position] as List<String>)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return when (viewType) {
            ViewTypes.HEADER.ordinal -> HeaderItemViewHolder(parent.inflate(R.layout.word_header_list_item))
            ViewTypes.WORD_MEANING.ordinal -> WordCardItemViewHolder(parent.inflate(R.layout.definition_card))
            ViewTypes.NOTES.ordinal -> NotesItemViewHolder(parent.inflate(R.layout.word_notes_list_item))
            else -> RelatedWordsItemViewHolder(parent.inflate(R.layout.related_word_card))
        }
    }

    override fun getItemCount() = dataset.size

    override fun getItemViewType(position: Int): Int {
        return when (dataset[position]) {
            is String -> ViewTypes.HEADER.ordinal
            is WordMeaning -> ViewTypes.WORD_MEANING.ordinal
            is Note -> ViewTypes.NOTES.ordinal
            else -> ViewTypes.RELATED_WORDS.ordinal
        }
    }

    inner class WordCardItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

    inner class HeaderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: String) {
            itemView.header.text = value
        }
    }

    inner class RelatedWordsItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: List<String>) {
            itemView.relatedWordTagLayout.removeAllViews()
            for (item in value) {
                val relatedWordCardItem = itemView.relatedWordTagLayout.inflate(R.layout.related_word_item)
                relatedWordCardItem.relatedWord.text = item
                relatedWordCardItem.setOnClickListener({ if (!item.containsWhiteSpace()) listener.onRelatedWordClicked(item) })
                itemView.relatedWordTagLayout.addView(relatedWordCardItem)
            }
        }
    }

    inner class NotesItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: Note) {
            itemView.notes.text = value.text
        }
    }
}
