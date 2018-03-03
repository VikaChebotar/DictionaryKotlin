package com.mydictionary.ui.views.home

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import com.mydictionary.R
import com.mydictionary.commons.Constants
import com.mydictionary.commons.Constants.CompoundDrawables.RIGHT
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.ui.DictionaryApp
import com.mydictionary.ui.presenters.home.HomePresenterImpl
import com.mydictionary.ui.presenters.home.HomeView
import com.mydictionary.ui.views.mywords.MyWordsActivity
import com.mydictionary.ui.views.search.SearchActivity
import kotlinx.android.synthetic.main.home_activity.*


class HomeActivity : AppCompatActivity(), HomeView {
    val presenter by lazy { HomePresenterImpl(DictionaryApp.getInstance(this).repository, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity);
        setSupportActionBar(toolbar)
        presenter.onStart(this)
        searchField.setOnTouchListener(searchTouchListener)
        loginBtn.setOnClickListener { presenter.onSingInClicked() }
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
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_my_words -> {
                presenter.onMyWordsBtnClicked()
                return true
            }
            R.id.action_settings -> {
                return true
            }
        }
        return false
    }

    override fun startMyWordsActivity() {
        val intent = Intent(this@HomeActivity, MyWordsActivity::class.java);
        startActivity(intent)
    }

    override fun showProgress(progress: Boolean) {
        progressBar.visibility = if (progress) View.VISIBLE else View.GONE
        homeLayout.visibility = if (progress) View.GONE else View.VISIBLE
    }

    override fun showUserLoginState(isLoggedIn: Boolean) {
        loginLayout.visibility = if (isLoggedIn) View.GONE else View.VISIBLE
        //TODO add logout button
    }

    override fun showError(message: String) {
        Snackbar.make(homeScrollContent!!, message, Snackbar.LENGTH_LONG).show()
        homeLayout.visibility = View.GONE
    }

    override fun startWordInfoActivity(word: WordDetails) {
//        val intent = Intent(this@HomeActivity, WordInfoActivity::class.java);
//        intent.putExtra(Constants.SELCTED_WORD_INFO_EXTRA, word)
//        startActivity(intent)
    }

    private fun startSearchActivity(isVoiceSearchClicked: Boolean = false) {
        val intent = Intent(this@HomeActivity, SearchActivity::class.java);
        intent.putExtra(Constants.VOICE_SEARCH_EXTRA, isVoiceSearchClicked)
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
        Snackbar.make(loginLayout, getString(R.string.login_success, userName),
                Snackbar.LENGTH_SHORT).show()
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
