package com.mydictionary.ui.views.home

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.mydictionary.R
import com.mydictionary.data.entity.WordInfo
import com.mydictionary.ui.DictionaryApp
import com.mydictionary.ui.presenters.HomePresenterImpl
import com.mydictionary.ui.presenters.HomeView
import com.mydictionary.ui.views.search.SearchActivity
import kotlinx.android.synthetic.main.home_activity.*

class HomeActivity : AppCompatActivity(), HomeView {
    val presenter by lazy { HomePresenterImpl(DictionaryApp.getInstance(this).repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity);
        searchField.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java);
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun showProgress(progress: Boolean) {
        progressBar.visibility = if (progress) View.VISIBLE else View.GONE
        homeLayout.visibility = if (progress) View.GONE else View.VISIBLE
    }

    override fun showWordOfTheDay(word: WordInfo) {
        wordOfTheDayCard.bind(word)
    }

    override fun showError(message: String) {
        Snackbar.make(homeScrollContent!!, message, Snackbar.LENGTH_LONG).show()
        homeLayout.visibility = View.GONE
    }

}
