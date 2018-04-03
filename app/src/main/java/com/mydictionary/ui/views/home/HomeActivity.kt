package com.mydictionary.ui.views.home

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import com.mydictionary.R
import com.mydictionary.commons.CompoundDrawables.RIGHT
import com.mydictionary.commons.VOICE_SEARCH_EXTRA
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.ui.DictionaryApp
import com.mydictionary.ui.presenters.home.HomePresenterImpl
import com.mydictionary.ui.presenters.home.HomeView
import com.mydictionary.ui.views.learn.LearnActivity
import com.mydictionary.ui.views.mywords.MyWordsActivity
import com.mydictionary.ui.views.search.SearchActivity
import com.mydictionary.ui.views.word.WordInfoActivity
import kotlinx.android.synthetic.main.home_activity.*


class HomeActivity : AppCompatActivity(), HomeView {
    val presenter by lazy { HomePresenterImpl(DictionaryApp.getInstance(this).repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity);
        setSupportActionBar(toolbar)
        presenter.onStart(this)
        searchField.setOnTouchListener(searchTouchListener)
        loginBtn.setOnClickListener { presenter.onSingInClicked() }
        fabBtn.setOnClickListener {
            startLearnActivity()
        }
        initList()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        menu?.findItem(R.id.action_signout)?.isVisible = loginLayout.visibility == View.GONE
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_signout -> {
                showSignOutConfirmDialog()
                return true
            }
        }
        return false
    }

    private fun showSignOutConfirmDialog() {
        AlertDialog.Builder(this).setTitle(getString(R.string.sign_out))
            .setMessage(getString(R.string.sign_out_confirm_question))
            .setPositiveButton(getString(R.string.yes), { _, _ -> presenter.onSignOutClicked() })
            .setNegativeButton(getString(R.string.cancel), { _, _ -> }).show()
    }

    override fun startMyWordsActivity() {
        val intent = Intent(this@HomeActivity, MyWordsActivity::class.java);
        startActivity(intent)
    }

    override fun showProgress(progress: Boolean) {
        progressBar.visibility = if (progress) View.VISIBLE else View.GONE
        wordList.visibility = if (progress) View.GONE else View.VISIBLE
    }

    override fun showUserLoginState(isLoggedIn: Boolean) {
        loginLayout.visibility = if (isLoggedIn) View.GONE else View.VISIBLE
        wordList.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
        invalidateOptionsMenu()
    }

    override fun showError(message: String) {
        Snackbar.make(wordList!!, message, Snackbar.LENGTH_LONG).show()
        wordList.visibility = View.GONE
    }

    override fun startWordInfoActivity(word: WordDetails) {
        WordInfoActivity.startActivity(this, word.word)
    }

    private fun startLearnActivity() {
        val intent = Intent(this@HomeActivity, LearnActivity::class.java);
        startActivity(intent)
    }

    private fun initList() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.isAutoMeasureEnabled = true
        wordList.layoutManager = linearLayoutManager
        wordList.adapter = WordListAdapter()
        //wordLists.adapter = WordListAdapter(this, object : FavoriteWordsAdapter.OnClickListener {
//            override fun onItemClicked(wordDetails: WordDetails) {
//                startWordInfoActivity(wordDetails)
//            }
//        })
//        val margin = resources.getDimension(R.dimen.activity_horizontal_margin).toInt()
        wordList.addItemDecoration(DividerItemDecoration(this, linearLayoutManager.orientation))
    }

    private fun startSearchActivity(isVoiceSearchClicked: Boolean = false) {
        val intent = Intent(this@HomeActivity, SearchActivity::class.java);
        intent.putExtra(VOICE_SEARCH_EXTRA, isVoiceSearchClicked)
        startActivity(intent)
    }

    override fun startSignInActivity(intent: Intent, requestCode: Int) {
        startActivityForResult(intent, requestCode);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onActivityResult(requestCode, resultCode, data)
    }

    override fun onLoginError(message: String) {
        Snackbar.make(loginLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onLoginSuccess(userName: String) {
        Snackbar.make(
            loginLayout, getString(R.string.login_success, userName),
            Snackbar.LENGTH_SHORT
        ).show()
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

    override fun showMyWordsList(list: List<String>) {
        (wordList.adapter as WordListAdapter).setData(list)
    }

    override fun getContext() = this
}
