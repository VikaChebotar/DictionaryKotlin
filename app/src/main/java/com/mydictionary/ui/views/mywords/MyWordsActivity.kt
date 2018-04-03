package com.mydictionary.ui.views.mywords

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.mydictionary.R
import com.mydictionary.data.firebasestorage.dto.UserWord
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.ui.presenters.mywords.MyWordsView
import kotlinx.android.synthetic.main.my_words_activity.*

/**
 * Created by Viktoria Chebotar on 09.07.17.
 */
class MyWordsActivity : AppCompatActivity(), MyWordsView{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_words_activity)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }

    override fun showProgress(progress: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showWords(words: List<UserWord>, isFavorite: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startWordInfoActivity(word: WordDetails) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContext() = this
}