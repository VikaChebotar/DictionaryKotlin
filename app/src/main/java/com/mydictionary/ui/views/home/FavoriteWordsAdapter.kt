package com.mydictionary.ui.views.home

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.mydictionary.R
import com.mydictionary.commons.inflate
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.ui.views.word.MeaningSpaceItemDecorator
import com.mydictionary.ui.views.word.MeaningsAdapter
import kotlinx.android.synthetic.main.favorite_word_item.view.*

/**
 * Created by Viktoria_Chebotar on 3/7/2018.
 */
class FavoriteWordsAdapter(context: Context, val listener: OnClickListener) : RecyclerView.Adapter<FavoriteWordsAdapter.ItemViewHolder>() {
    var dataset = mutableListOf<WordDetails>()
    val meaningItemDecoration by lazy { MeaningSpaceItemDecorator(context.resources.getDimension(R.dimen.meanings_space_decorator).toInt()) }

    interface OnClickListener {
        fun onItemClicked(wordDetails: WordDetails)
    }

    override fun onBindViewHolder(holder: ItemViewHolder?, position: Int) {
        holder?.bind(dataset[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ItemViewHolder(parent.inflate(R.layout.favorite_word_item))

    override fun getItemCount() = dataset.size


    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: WordDetails) {
            itemView.favWordName.text = value.word
            //todo optimize
            val data = value.meanings.filter { it.isFavourite }.map { it.definitions.union(it.examples) }.
                    reduce { acc, set -> acc.union(set) }.toList()
            itemView.favMeaningsRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            itemView.favMeaningsRecyclerView.adapter = MeaningsAdapter(data)
            itemView.favMeaningsRecyclerView.removeItemDecoration(meaningItemDecoration)
            itemView.favMeaningsRecyclerView.addItemDecoration(meaningItemDecoration)
            itemView.setOnClickListener { listener.onItemClicked(dataset[adapterPosition]) }
        }
    }
}