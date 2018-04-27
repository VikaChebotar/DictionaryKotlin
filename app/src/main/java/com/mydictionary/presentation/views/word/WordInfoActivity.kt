package com.mydictionary.presentation.views.word

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.mydictionary.R
import com.mydictionary.presentation.utils.SELECTED_WORD_NAME_EXTRA
import com.mydictionary.presentation.utils.getViewModel
import com.mydictionary.presentation.views.Data
import com.mydictionary.presentation.views.DataState
import com.mydictionary.presentation.views.SpaceItemDecorator
import kotlinx.android.synthetic.main.word_content_scrolling.*
import kotlinx.android.synthetic.main.word_info_activity.*

/**
 * Created by Viktoria Chebotar on 28.06.17.
 */

class WordInfoActivity : AppCompatActivity(), WordCardsAdapter.ViewClickListener {
    private val wordName by lazy { intent.getStringExtra(SELECTED_WORD_NAME_EXTRA) }
    private lateinit var textToSpeechHelper: TextToSpeechHelper
    private val viewModel by lazy {
        getViewModel<WordInfoViewModel>(
            WordInfoViewModelFactory(
                wordName
            )
        )
    }

    private val wordCardsAdapter = WordCardsAdapter(this, this)

    companion object {
        fun startActivity(context: Context, word: String) {
            val intent = Intent(context, WordInfoActivity::class.java)
            intent.putExtra(SELECTED_WORD_NAME_EXTRA, word)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.word_info_activity)
        initToolbar(wordName)
        initList(scrollContentRecyclerView, wordCardsAdapter)
        textToSpeechHelper = TextToSpeechHelper(this, lifecycle)
        viewModel.wordPresentationDetails.observe(this, Observer { showWordDetails(it) })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.word_info_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.pronounce -> {
                textToSpeechHelper.onPronounceClicked(wordName)
                return true
            }
        }
        return false
    }

    private fun initList(
            recyclerView: RecyclerView,
            adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>
    ) {
        val linearLayoutManager = object : LinearLayoutManager(this) {
            override fun isAutoMeasureEnabled() = true
        }
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        val margin = resources.getDimension(R.dimen.activity_horizontal_margin).toInt()
        recyclerView.addItemDecoration(SpaceItemDecorator(margin))
    }

    private fun initToolbar(word: String) {
        setSupportActionBar(toolbar)
        toolbarLayout.title = word
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun showWordDetails(data: Data<WordPresentationDetails>?) {
        data?.apply {
            progressBar.visibility = if (dataState == DataState.LOADING) View.VISIBLE else View.GONE
            if (dataState == DataState.ERROR) showError(message)
            this.data?.let {
                showPronunciation(it.pronunciation)
                showWordCards(it.contentList)
            }
        }
    }

    private fun showPronunciation(value: String?) {
        pronunciation.text =
                if (value?.isBlank() == true) "" else getString(R.string.prononcuation, value)
    }

    private fun showWordCards(value: List<Any>) {
        if (wordCardsAdapter.dataset != value) {
            wordCardsAdapter.dataset = value
            wordCardsAdapter.notifyDataSetChanged()
        }
    }

    private fun showError(message: String?) {
        Snackbar.make(
                scrollContentRecyclerView,
                message ?: getString(R.string.default_error),
                Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onRelatedWordClicked(item: String) {
        WordInfoActivity.startActivity(this, item)
        finish()
    }

    override fun onFavouriteBtnClicked(item: WordMeaning) {
        viewModel.onFavoriteClicked(item)
    }
}
