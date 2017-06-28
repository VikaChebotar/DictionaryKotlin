package com.mydictionary.ui.views.search

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.mydictionary.R
import com.mydictionary.ui.DictionaryApp
import com.mydictionary.ui.presenters.search.SearchPresenterImpl
import com.mydictionary.ui.presenters.search.SearchView
import kotlinx.android.synthetic.main.search_activity.*


/**
 * Created by Viktoria_Chebotar on 6/20/2017.
 */
class SearchActivity : AppCompatActivity(), SearchView {

    val presenter by lazy { SearchPresenterImpl(DictionaryApp.getInstance(this).repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_activity)
        searchField.contentChangedListener = presenter
        searchField.requestFocus()
        with(searchRecyclerView) {
            layoutManager = LinearLayoutManager(context) as RecyclerView.LayoutManager?
            val dividerDecoration = DividerItemDecoration(context, ContextCompat.getDrawable(context,
                    R.drawable.divider_with_padding))
            addItemDecoration(dividerDecoration)
            adapter = SearchResultsAdapter({ value -> onItemClick(value) })
            isNestedScrollingEnabled = false
        }
        presenter.onStart(this)
    }

    fun onItemClick(searchWord: String) {
        Toast.makeText(this, searchWord, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onStop()
        searchField.contentChangedListener = null
    }

    override fun showProgress(progress: Boolean) {

    }

    override fun showError(message: String) {
        Snackbar.make(searchScrollContent!!, message, Snackbar.LENGTH_LONG).show()
    }

    override fun showHistoryWords(list: List<String>) {
        (searchRecyclerView.adapter as SearchResultsAdapter).setList(list, true)
        moreHistoryBtn.visibility = View.VISIBLE
    }

    override fun showSearchResult(list: List<String>) {
        (searchRecyclerView.adapter as SearchResultsAdapter).setList(list, false)
        moreHistoryBtn.visibility = View.GONE
    }

    override fun finishView() {
        onBackPressed()
    }
}