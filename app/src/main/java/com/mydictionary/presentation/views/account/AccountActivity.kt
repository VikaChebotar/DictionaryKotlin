package com.mydictionary.presentation.views.account

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.mydictionary.R
import com.mydictionary.presentation.DictionaryApp
import com.mydictionary.presentation.utils.getViewModel
import com.mydictionary.presentation.views.Data
import com.mydictionary.presentation.views.DataState
import kotlinx.android.synthetic.main.account_activity.*
import javax.inject.Inject

const val SIGN_IN_REQUEST_CODE = 1

class AccountActivity : AppCompatActivity() {
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { getViewModel<AccountViewModel>(viewModelFactory) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_activity)
        DictionaryApp.component.inject(this)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
        initGoogleSignInClient()
        loginBtn.setOnClickListener { startSignInActivity() }
        logoutBtn.setOnClickListener { showSignOutConfirmDialog() }
        viewModel.userName.observe(this, Observer {
            showLoginState(it)
        })
    }

    private fun showLoginState(result: Data<String>?) {
        result?.apply {
            when (dataState) {
                DataState.ERROR -> showError(message ?: getString(R.string.login_error))
                DataState.SUCCESS -> showUserLoginState(data)
                DataState.LOADING -> {
                }
            }
            progressBar.visibility = if (dataState == DataState.LOADING) View.VISIBLE else View.GONE
        }
    }

    private fun initGoogleSignInClient() {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }

    private fun showSignOutConfirmDialog() {
        AlertDialog.Builder(this).setTitle(getString(R.string.account))
                .setMessage(getString(R.string.sign_out_confirm_question))
                .setPositiveButton(getString(R.string.yes), { _, _ -> onSignoutClicked() })
                .setNegativeButton(getString(R.string.cancel), { _, _ -> }).show()
    }

    private fun onSignoutClicked() {
        googleSignInClient.signOut()
        googleSignInClient.revokeAccess()
        viewModel.signOut()
    }

    private fun showUserLoginState(userName: String?) {
        loginLayout.visibility = if (userName.isNullOrEmpty()) View.VISIBLE else View.GONE
        logoutLayout.visibility = if (userName.isNullOrEmpty()) View.GONE else View.VISIBLE
        if (!userName.isNullOrEmpty()) {
            userInfoText.text = getString(R.string.user_account_info, userName)
        }
    }

    private fun startSignInActivity() {
        startActivityForResult(googleSignInClient.signInIntent, SIGN_IN_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val googleAccount = task.getResult(ApiException::class.java)
                viewModel.signIn(googleAccount.idToken!!)
            } catch (e: Exception) {
                showError(getString(R.string.login_error))
            }
        }
    }

    private fun showError(message: String) {
        Snackbar.make(loginLayout, message, Snackbar.LENGTH_SHORT).show()
    }
}