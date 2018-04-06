package com.mydictionary.ui.presenters.account

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.mydictionary.R
import com.mydictionary.data.repository.WordsRepository
import com.mydictionary.ui.presenters.home.SIGN_IN_REQUEST_CODE
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AccountPresenterImpl(val repository: WordsRepository) : AccountPresenter {
    val TAG = AccountPresenterImpl::class.java.simpleName
    private var googleSignInOptions: GoogleSignInOptions? = null
    private var googleSignInClient: GoogleSignInClient? = null
    private var accountView: AccountView? = null
    private var isLoggedIn = false
    private val compositeDisposable = CompositeDisposable()

    override fun onStart(view: AccountView) {
        this.accountView = view
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(view.getContext().getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(view.getContext(), googleSignInOptions!!);
        checkIfLoggedIn()
    }

    private fun checkIfLoggedIn() {
        isLoggedIn = false
        accountView?.showUserLoginState(isLoggedIn)

        compositeDisposable.add(
            repository.getUserName().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { userName ->
                    accountView?.showUserLoginState(true, userName)
                    this.isLoggedIn = true
                },
                {
                    this.isLoggedIn = false
                    accountView?.showUserLoginState(false)
                })
        )
    }

    override fun onSingInClicked() {
        val signInIntent = googleSignInClient?.signInIntent
        if (signInIntent != null) {
            accountView?.startSignInActivity(signInIntent, SIGN_IN_REQUEST_CODE)
        }
    }

    override fun onSignOutClicked() {
        googleSignInClient?.signOut()
        googleSignInClient?.revokeAccess()
        repository.signOut()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                accountView?.showUserLoginState(false)
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SIGN_IN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            accountView?.showProgress(true)
            compositeDisposable.add(Single.just(task.getResult(ApiException::class.java)).flatMap {
                repository.loginFirebaseUser(it.idToken)
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({ userName ->
                accountView?.showProgress(false)
                accountView?.showUserLoginState(true, userName)
                isLoggedIn = true
            }, { error ->
                Log.e(TAG, error.message)
                accountView?.showError(
                    accountView?.getContext()?.getString(R.string.login_error) ?: ""
                )
                accountView?.showProgress(false)
                isLoggedIn = false
            }))
        }
    }

    override fun onStop() {
        accountView = null
        compositeDisposable.clear()
    }

}