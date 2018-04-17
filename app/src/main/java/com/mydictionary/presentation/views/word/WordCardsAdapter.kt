package com.mydictionary.presentation.views.word

import android.content.Context
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

class WordCardsAdapter(val listener: ViewClickListener, val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var dataset: List<Any> = emptyList()
    val meaningItemDecoration by lazy { MeaningSpaceItemDecorator(context.resources.getDimension(R.dimen.meanings_space_decorator).toInt()) }

    enum class ViewTypes {
        HEADER, WORD_MEANING, RELATED_WORDS, NOTES
    }

    interface ViewClickListener {
        fun onRelatedWordClicked(item: String)
        fun onFavouriteBtnClicked(item: WordMeaning)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderItemViewHolder -> holder.bind(dataset[position] as String)
            is WordCardItemViewHolder -> holder.bind(dataset[position] as WordMeaning)
            is NotesItemViewHolder -> holder.bind(dataset[position] as Note)
            is RelatedWordsItemViewHolder -> holder.bind(dataset[position] as List<String>)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
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
            itemView.meaningsRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            itemView.meaningsRecyclerView.adapter = MeaningsAdapter(value.definitions.union(value.examples).toList())
            itemView.meaningsRecyclerView.removeItemDecoration(meaningItemDecoration)
            itemView.meaningsRecyclerView.addItemDecoration(meaningItemDecoration)
            itemView.favWord.isChecked = value.isFavourite
            itemView.favWord.isEnabled = true
            itemView.favWord.setOnClickListener {
                listener.onFavouriteBtnClicked(dataset[adapterPosition] as WordMeaning)
            }
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
