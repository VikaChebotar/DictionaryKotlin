package com.mydictionary.ui.views.home

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import com.mydictionary.R
import com.mydictionary.commons.Constants
import com.mydictionary.commons.Constants.CompoundDrawables.RIGHT
import com.mydictionary.data.entity.WordInfo
import com.mydictionary.ui.DictionaryApp
import com.mydictionary.ui.presenters.home.HomePresenterImpl
import com.mydictionary.ui.presenters.home.HomeView
import com.mydictionary.ui.views.search.SearchActivity
import com.mydictionary.ui.views.word.WordInfoActivity
import kotlinx.android.synthetic.main.home_activity.*

class HomeActivity : AppCompatActivity(), HomeView {
    val presenter by lazy { HomePresenterImpl(DictionaryApp.getInstance(this).repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity);
        presenter.onStart(this)
        searchField.setOnTouchListener(searchTouchListener)
        wordOfTheDayCard.setOnClickListener { presenter.onWordOfTheDayClicked() }
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onStop()
    }

    override fun showProgress(progress: Boolean) {
        progressBar.visibility = if (progress) View.VISIBLE else View.GONE
        homeLayout.visibility = if (progress) View.GONE else View.VISIBLE
    }

    override fun showWordOfTheDay(word: WordInfo) {
        wordOfTheDayCard.bind(word, { presenter.onWordOfTheDayFavoriteBtnClicked() })
    }

    override fun showError(message: String) {
        Snackbar.make(homeScrollContent!!, message, Snackbar.LENGTH_LONG).show()
        homeLayout.visibility = View.GONE
    }

    override fun startWordInfoActivity(word: WordInfo) {
        val intent = Intent(this@HomeActivity, WordInfoActivity::class.java);
        intent.putExtra(Constants.SELCTED_WORD_INFO_EXTRA, word)
        startActivity(intent)
    }

    override fun showWordOfTheDayFavoriteBtnState(isFavorite: Boolean) {
        wordOfTheDayCard.onBindFavoriteBtnState(isFavorite)
    }

    fun startSearchActivity(isVoiceSearchClicked: Boolean = false) {
        val intent = Intent(this@HomeActivity, SearchActivity::class.java);
        intent.putExtra(Constants.VOICE_SEARCH_EXTRA, isVoiceSearchClicked)
        startActivity(intent)
    }

    private val searchTouchListener = View.OnTouchListener { _, event ->
        with(searchField) {
            if (event?.action == MotionEvent.ACTION_UP) {
                val rightBound = right - compoundDrawables[RIGHT.ordinal].bounds.width() -
                        compoundDrawablePadding - paddingRight
                startSearchActivity(event.rawX >= rightBound)
            }
        }
        false
    }
}
