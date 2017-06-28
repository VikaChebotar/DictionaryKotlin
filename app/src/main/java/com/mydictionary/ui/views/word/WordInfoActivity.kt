package com.mydictionary.ui.views.word

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.mydictionary.R
import com.mydictionary.commons.Constants
import com.mydictionary.data.entity.WordInfo
import kotlinx.android.synthetic.main.word_info_activity.*

/**
 * Created by Viktoria Chebotar on 28.06.17.
 */

class WordInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.word_info_activity);
        val wordInfo = intent.extras.getParcelable<WordInfo>(Constants.SELCTED_WORD_INFO_EXTRA)
        Toast.makeText(this, wordInfo.word, Toast.LENGTH_SHORT).show()
    }

    private fun initToolbar(word: String) {
        setSupportActionBar(toolbar)
        toolbarLayout.title = word
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
