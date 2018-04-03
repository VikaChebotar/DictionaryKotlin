package com.mydictionary.ui.presenters.home

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.mydictionary.R
import com.mydictionary.data.repository.WordsRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


/**
 * Created by Viktoria Chebotar on 18.06.17.
 */
const val SIGN_IN_REQUEST_CODE = 1

class HomePresenterImpl(val repository: WordsRepository) : HomePresenter {
    val TAG = HomePresenterImpl::class.java.simpleName
    private var googleSignInOptions: GoogleSignInOptions? = null
    private var googleSignInClient: GoogleSignInClient? = null
    private var homeView: HomeView? = null
    private var isLoggedIn = false
    private val compositeDisposable = CompositeDisposable()

    override fun onStart(view: HomeView) {
        this.homeView = view
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(view.getContext().getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(view.getContext(), googleSignInOptions!!);
        checkIfLoggedIn()
    }

    override fun onMyWordsBtnClicked() {
        homeView?.startMyWordsActivity()
    }

    override fun onSingInClicked() {
        val signInIntent = googleSignInClient?.signInIntent
        if (signInIntent != null) {
            homeView?.startSignInActivity(signInIntent, SIGN_IN_REQUEST_CODE)
        }
    }

    override fun onResume() {
        loadWordLists()
    }

    override fun onStop() {
        homeView = null
        compositeDisposable.clear()
    }

    override fun onSignOutClicked() {
        googleSignInClient?.signOut()
        googleSignInClient?.revokeAccess()
        repository.signOut()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    homeView?.showUserLoginState(false)
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SIGN_IN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            homeView?.showProgress(true)
            compositeDisposable.add(Single.just(task.getResult(ApiException::class.java)).flatMap {
                repository.loginFirebaseUser(it.idToken)
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({ userName ->
                homeView?.onLoginSuccess(userName)
                homeView?.showProgress(false)
                homeView?.showUserLoginState(true)
                isLoggedIn = true
                loadWordLists()
            }, { error ->
                Log.e(TAG, error.message)
                homeView?.onLoginError(homeView?.getContext()?.getString(R.string.login_error)
                        ?: "")
                homeView?.showProgress(false)
                isLoggedIn = false
            }))
        }
    }

    private fun checkIfLoggedIn() {
        isLoggedIn = false
        homeView?.showUserLoginState(isLoggedIn)

        compositeDisposable.add(
                repository.isSignedIn().
                        subscribeOn(Schedulers.io()).
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe { isLoggedIn ->
                    homeView?.showUserLoginState(isLoggedIn)
                    this.isLoggedIn = isLoggedIn
                })
    }

    private fun loadWordLists() {
        compositeDisposable.add(
                Single.just("").
                        map { homeView?.showProgress(true) }.
                        flatMap { repository.getFavoriteWords() }.
                        subscribeOn(Schedulers.io()).
                        observeOn(AndroidSchedulers.mainThread()).
                        doOnEvent { t1, t2 -> homeView?.showProgress(false) }.
                        subscribe(
                        { homeView?.showMyWordsList(it) },
                        { throwable ->
                            homeView?.let {
                                it.showError(throwable.message
                                        ?: it.getContext().getString(R.string.default_error))
                            }
                        }))

    }
}
