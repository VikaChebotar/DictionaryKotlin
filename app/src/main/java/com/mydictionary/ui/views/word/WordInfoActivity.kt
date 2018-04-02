package com.mydictionary.ui.views.word

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
import com.mydictionary.commons.SELECTED_WORD_NAME_EXTRA
import com.mydictionary.data.pojo.WordMeaning
import com.mydictionary.ui.DictionaryApp
import com.mydictionary.ui.presenters.word.WordInfoPresenterImpl
import com.mydictionary.ui.presenters.word.WordInfoView
import com.mydictionary.ui.views.SpaceItemDecorator
import kotlinx.android.synthetic.main.word_content_scrolling.*
import kotlinx.android.synthetic.main.word_info_activity.*

/**
 * Created by Viktoria Chebotar on 28.06.17.
 */

class WordInfoActivity : AppCompatActivity(), WordInfoView, WordCardsAdapter.ViewClickListener {
    val presenter by lazy { WordInfoPresenterImpl(DictionaryApp.getInstance(this).repository) }
    val wordCardsAdapter = WordCardsAdapter(this, this)

    companion object {
        fun startActivity(context: Context, word: String) {
            val intent = Intent(context, WordInfoActivity::class.java)
            intent.putExtra(SELECTED_WORD_NAME_EXTRA, word)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.word_info_activity);
        initList(scrollContent, wordCardsAdapter)

        presenter.onStart(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.word_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.pronounce -> {
                return true
            }
        }
        return false
    }

    private fun initList(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>) {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.isAutoMeasureEnabled = true
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        val margin = resources.getDimension(R.dimen.activity_horizontal_margin).toInt()
        recyclerView.addItemDecoration(SpaceItemDecorator(margin))
    }
//
//    override fun showIsFavorite(isFavorite: Boolean) {
//        val imageRes = if (isFavorite) R.drawable.ic_favorite_white_24dp else
//            R.drawable.ic_favorite_border_white_24dp
//        favoriteFab.setImageResource(imageRes)
//    }

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

    override fun showPronunciation(value: String) {
        pronunciation.text = if (value.isBlank()) "" else getString(R.string.prononcuation, value)
    }

    //
//    override fun showWordCards(value: List<Definition>, showSeeAllBtn: Boolean) {
//        wordCardsAdapter.dataset = value
//        wordCardsAdapter.notifyDataSetChanged()
//        if (value.isEmpty()) definitionCard.visibility = View.GONE
//        seeAllDefinitionsBtn.visibility = if (showSeeAllBtn) View.VISIBLE else View.GONE
//    }
    override fun showWordCards(value: List<Any>) {
        wordCardsAdapter.dataset = value
        wordCardsAdapter.notifyDataSetChanged()
    }

    override fun showProgress(progress: Boolean) {
        progressBar.visibility = if (progress) View.VISIBLE else View.GONE
    }

    override fun showError(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }

    override fun getExtras(): Bundle {
        return intent.extras
    }

    override fun onRelatedWordClicked(item: String) {
        WordInfoActivity.startActivity(this, item)
    }

    override fun onFavouriteBtnClicked(item: WordMeaning) {
        presenter.onFavoriteClicked(item)
    }

    override fun getContext() = this
}
