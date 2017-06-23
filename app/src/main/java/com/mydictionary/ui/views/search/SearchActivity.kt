package com.mydictionary.ui.views.search

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mydictionary.R
import kotlinx.android.synthetic.main.search_activity.*


/**
 * Created by Viktoria_Chebotar on 6/20/2017.
 */
class SearchActivity : AppCompatActivity(), SearchEditText.ContentChangedListener, SearchEditText.OpenCloseListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_activity)
        searchField.contentChangedListener = this
        searchField.openCloseListener = this
        with(searchRecyclerView){

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        searchField.contentChangedListener = null
        searchField.openCloseListener = null
    }
    override fun onSearchLetterEntered(msg: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSearchCleared() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSearchOpened() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSearchClosed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}