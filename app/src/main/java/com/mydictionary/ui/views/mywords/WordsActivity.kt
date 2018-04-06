package com.mydictionary.ui.views.mywords

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
import com.mydictionary.commons.WORD_LIST_NAME
import com.mydictionary.ui.DictionaryApp
import com.mydictionary.ui.presenters.mywords.WordsPresenterImpl
import com.mydictionary.ui.presenters.mywords.WordsView
import com.mydictionary.ui.views.word.WordInfoActivity
import kotlinx.android.synthetic.main.words_activity.*

/**
 * Created by Viktoria Chebotar on 09.07.17.
 */
class WordsActivity : AppCompatActivity(), WordsView {
    val presenter by lazy { WordsPresenterImpl(DictionaryApp.getInstance(this).repository) }

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

    override fun getWordListName() = intent.getStringExtra(WORD_LIST_NAME)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.words_activity)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getWordListName()
        wordList.adapter = WordListAdapter({ startWordInfoActivity(it) })
        wordList.layoutManager = LinearLayoutManager(this)
        wordList.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        presenter.onStart(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_sort -> {
                presenter.onSortMenuClicked()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showWords(words: List<String>) {
        (wordList.adapter as WordListAdapter).setData(words)
    }

    override fun startWordInfoActivity(word: String) = WordInfoActivity.startActivity(this, word)

    override fun showProgress(progress: Boolean) {
        progressBar.visibility = if (progress) View.VISIBLE else View.GONE
    }

    override fun showError(message: String) {
        Snackbar.make(wordList, message, Snackbar.LENGTH_LONG).show()
    }

    override fun getContext() = this

    override fun onDestroy() {
        super.onDestroy()
        presenter.onStop()
    }
}