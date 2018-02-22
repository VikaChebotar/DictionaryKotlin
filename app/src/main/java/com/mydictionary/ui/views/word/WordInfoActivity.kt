package com.mydictionary.ui.views.word

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.mydictionary.R
import com.mydictionary.data.pojo.WordInfo
import com.mydictionary.ui.DictionaryApp
import com.mydictionary.ui.presenters.word.WordInfoPresenterImpl
import com.mydictionary.ui.presenters.word.WordInfoView
import kotlinx.android.synthetic.main.word_info_activity.*

/**
 * Created by Viktoria Chebotar on 28.06.17.
 */

class WordInfoActivity : AppCompatActivity(), WordInfoView {
    val presenter by lazy { WordInfoPresenterImpl(DictionaryApp.getInstance(this).repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.word_info_activity);
        presenter.onStart(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onStop()
    }

    override fun initToolbar(word: String) {
        setSupportActionBar(toolbar)
        toolbarLayout.title = word
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun bindWordInfo(wordInfo: WordInfo) {
        pronunciation.text = getString(R.string.prononcuation, wordInfo.pronunciation)
    }

    override fun showProgress(progress: Boolean) {

    }

    override fun showError(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }

    override fun getExtras(): Bundle {
        return intent.extras
    }
}
