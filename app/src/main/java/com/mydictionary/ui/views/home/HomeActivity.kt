package com.mydictionary.ui.views.home

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.mydictionary.R
import com.mydictionary.commons.CompoundDrawables.RIGHT
import com.mydictionary.commons.VOICE_SEARCH_EXTRA
import com.mydictionary.ui.Data
import com.mydictionary.ui.DataState
import com.mydictionary.ui.DictionaryApp
import com.mydictionary.ui.presenters.home.HomeViewModel
import com.mydictionary.ui.presenters.home.WordListItem
import com.mydictionary.ui.views.account.AccountActivity
import com.mydictionary.ui.views.learn.LearnActivity
import com.mydictionary.ui.views.mywords.WordsActivity
import com.mydictionary.ui.views.search.SearchActivity
import kotlinx.android.synthetic.main.home_activity.*


class HomeActivity : AppCompatActivity() {
    private val viewModel by lazy {
        ViewModelProviders.of(this,
                DictionaryApp.getInstance(this).viewModelFactory)
                .get(HomeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity);
        setSupportActionBar(toolbar)

        searchField.setOnTouchListener(searchTouchListener)
        fabBtn.setOnClickListener {
            startLearnActivity()
        }
        initList()
        viewModel.wordList.observe(this, Observer { updateList(it) })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_account -> {
                startAccountActivity()
                return true
            }
        }
        return false
    }

    private fun updateList(data: Data<List<WordListItem>>?) {
        data?.let {
            showProgress(it.dataState == DataState.LOADING)
            it.data?.let { (wordList.adapter as WordListAdapter).setData(it) }
            it.message?.let { showError(it) }
        }
    }

    fun showProgress(progress: Boolean) {
        progressBar.visibility = if (progress) View.VISIBLE else View.GONE
    }

    fun showError(message: String) {
        Snackbar.make(wordList, message, Snackbar.LENGTH_LONG).show()
    }

    private fun startLearnActivity() {
        val intent = Intent(this@HomeActivity, LearnActivity::class.java);
        startActivity(intent)
    }

    private fun startAccountActivity() {
        val intent = Intent(this@HomeActivity, AccountActivity::class.java);
        startActivity(intent)
    }

    private fun initList() {
        wordList.layoutManager = LinearLayoutManager(this)
        wordList.adapter = WordListAdapter({ it1, it2 -> onWordListClick(it1, it2) })
        val divider = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        divider.setDrawable(getDrawable(R.drawable.divider))
        wordList.addItemDecoration(divider)
    }

    private fun startSearchActivity(isVoiceSearchClicked: Boolean = false) {
        val intent = Intent(this@HomeActivity, SearchActivity::class.java);
        intent.putExtra(VOICE_SEARCH_EXTRA, isVoiceSearchClicked)
        val p1 = android.support.v4.util.Pair<View, String>(
                searchField,
                getString(R.string.search_field_transition_name)
        )
        val p2 = android.support.v4.util.Pair<View, String>(
                appBarLayout,
                getString(R.string.search_appbar_transition_name)
        )
        val statusBar = findViewById<View>(android.R.id.statusBarBackground)
        val p3 = android.support.v4.util.Pair(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2, p3)
        startActivity(intent, options.toBundle())
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

    private fun onWordListClick(position: Int, wordListName: String) {
        WordsActivity.startActivity(this, wordListName)
    }
}
