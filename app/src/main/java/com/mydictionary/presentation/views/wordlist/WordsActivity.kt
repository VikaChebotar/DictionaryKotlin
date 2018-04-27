package com.mydictionary.presentation.views.wordlist

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.mydictionary.R
import com.mydictionary.presentation.utils.WORD_LIST_NAME
import com.mydictionary.presentation.utils.getViewModel
import com.mydictionary.presentation.views.DataState
import com.mydictionary.presentation.views.word.WordInfoActivity
import kotlinx.android.synthetic.main.words_activity.*

/**
 * Created by Viktoria Chebotar on 09.07.17.
 */
class WordsActivity : AppCompatActivity() {
    private val wordListName by lazy { intent.getStringExtra(WORD_LIST_NAME) }
    private val viewModel by lazy {
        getViewModel<WordListViewModel>(
            WordListViewModelFactory(
                wordListName
            )
        )
    }

    companion object {
        fun startActivity(context: Context, wordListName: String) {
            val intent = Intent(context, WordsActivity::class.java)
            intent.putExtra(WORD_LIST_NAME, wordListName)
            context.startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.learn_cards_menu, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.words_activity)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            title = wordListName
        }
        wordList.apply {
            adapter = WordListAdapter({ startWordInfoActivity(it) })
            layoutManager = LinearLayoutManager(this@WordsActivity)
            addItemDecoration(
                DividerItemDecoration(
                    this@WordsActivity,
                    LinearLayoutManager.VERTICAL
                )
            )
        }
        viewModel.wordList.observe(this, Observer {
            it?.apply {
                when (dataState) {
                    DataState.ERROR -> showError(message ?: getString(R.string.default_error))
                    DataState.SUCCESS -> showWords(data ?: emptyList())
                    DataState.LOADING -> {
                    }
                }
                showProgress(dataState == DataState.LOADING)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_sort -> {
                viewModel.sortMenu()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showWords(words: List<String>) {
        (wordList.adapter as WordListAdapter).setData(words)
    }

    private fun startWordInfoActivity(word: String) = WordInfoActivity.startActivity(this, word)

    private fun showProgress(progress: Boolean) {
        progressBar.visibility = if (progress) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Snackbar.make(wordList, message, Snackbar.LENGTH_LONG).show()
    }
}