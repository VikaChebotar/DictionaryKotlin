package com.mydictionary.ui.views.word

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mydictionary.R
import com.mydictionary.commons.inflate
import kotlinx.android.synthetic.main.related_word_card.view.*

/**
 * Created by Viktoria Chebotar on 02.07.17.
 */

class RelatedWordsAdapter : RecyclerView.Adapter<RelatedWordsAdapter.RelatedWordViewHolder>() {
    var dataset: List<Pair<String, List<String>>> = emptyList()

    override fun onBindViewHolder(holder: RelatedWordViewHolder, position: Int) =
            holder.bind(dataset[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RelatedWordViewHolder(parent.inflate(R.layout.related_word_card))

    override fun getItemCount() = dataset.size

    class RelatedWordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(value: Pair<String, List<String>>) {
            for (i in 0..value.second.size - 1) {
                val textView = itemView.relatedWordTagLayout.inflate(R.layout.related_word_item)
                (textView as TextView).text = value.second[i]
                itemView.relatedWordTagLayout.addView(textView)
            }
            itemView.relatedWordLbl.text = value.first
        }

    }
}
