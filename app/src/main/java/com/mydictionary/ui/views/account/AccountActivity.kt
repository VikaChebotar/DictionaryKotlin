package com.mydictionary.ui.views.account

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.mydictionary.R
import com.mydictionary.ui.DictionaryApp
import com.mydictionary.ui.presenters.account.AccountPresenterImpl
import com.mydictionary.ui.presenters.account.AccountView
import kotlinx.android.synthetic.main.account_activity.*

class AccountActivity : AppCompatActivity(), AccountView {
    val presenter by lazy { AccountPresenterImpl(DictionaryApp.getInstance(this).repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_activity)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        presenter.onStart(this)
        loginBtn.setOnClickListener { presenter.onSingInClicked() }
        logoutBtn.setOnClickListener { showSignOutConfirmDialog() }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onStop()
    }

    private fun showSignOutConfirmDialog() {
        AlertDialog.Builder(this).setTitle(getString(R.string.account))
            .setMessage(getString(R.string.sign_out_confirm_question))
            .setPositiveButton(getString(R.string.yes), { _, _ -> presenter.onSignOutClicked() })
            .setNegativeButton(getString(R.string.cancel), { _, _ -> }).show()
    }

    override fun showUserLoginState(isLoggedIn: Boolean, userName: String?) {
        loginLayout.visibility = if (isLoggedIn) View.GONE else View.VISIBLE
        logoutLayout.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
        if (isLoggedIn && userName != null) {
            userInfoText.text = getString(R.string.user_account_info, userName)
        }
    }

    override fun startSignInActivity(intent: Intent, requestCode: Int) {
        startActivityForResult(intent, requestCode);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onActivityResult(requestCode, resultCode, data)
    }

    override fun showProgress(progress: Boolean) {
        progressBar.visibility = if (progress) View.VISIBLE else View.GONE
    }

    override fun showError(message: String) {
        Snackbar.make(loginLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun getContext() = this
}