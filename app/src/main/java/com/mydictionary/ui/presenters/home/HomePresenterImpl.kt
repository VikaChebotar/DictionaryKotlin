package com.mydictionary.ui.presenters.home

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.mydictionary.R
import com.mydictionary.data.repository.RepositoryListener
import com.mydictionary.data.repository.WordsRepository


/**
 * Created by Viktoria Chebotar on 18.06.17.
 */
const val SIGN_IN_REQUEST_CODE = 1

class HomePresenterImpl(val repository: WordsRepository, val context: Context) : HomePresenter {
    val TAG = HomePresenterImpl::class.java.simpleName
    val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail().build()
    val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions);
    var homeView: HomeView? = null

    override fun onStart(view: HomeView) {
        this.homeView = view
        checkIfLoggedIn()
    }

    override fun onMyWordsBtnClicked() {
        homeView?.startMyWordsActivity()
    }

    override fun onSingInClicked() {
        val signInIntent = googleSignInClient.getSignInIntent()
        homeView?.startSignInActivity(signInIntent, SIGN_IN_REQUEST_CODE)
    }

    override fun onResume() {
//        todayWord?.let {
//            it.isFavorite = repository.getWordFavoriteState(it.word)
//            homeView?.showWordOfTheDayFavoriteBtnState(it.isFavorite)
//        }
    }

    override fun onStop() {
        homeView = null
    }

    override fun onSignOutClicked() {
        googleSignInClient.signOut()
        googleSignInClient.revokeAccess()
        repository.logoutFirebaseUser()
        homeView?.showUserLoginState(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            homeView?.showProgress(true)
            try {
                val account = task.getResult(ApiException::class.java)
                repository.loginFirebaseUser(account.idToken, object : RepositoryListener<String> {
                    override fun onSuccess(userName: String) {
                        homeView?.onLoginSuccess(userName)
                        homeView?.showProgress(false)
                        homeView?.showUserLoginState(true)
                    }

                    override fun onError(error: String) {
                        Log.e(TAG, error)
                        homeView?.onLoginError(context.getString(R.string.login_error))
                        homeView?.showProgress(false)
                    }
                })
            } catch (e: ApiException) {
                Log.e(TAG, e.message)
                homeView?.onLoginError(context.getString(R.string.login_error))
                homeView?.showProgress(false)
            }
        }
    }

    private fun checkIfLoggedIn() {
        val firebaseUser = repository.getCurrentUser()
        if (firebaseUser != null && !firebaseUser.isAnonymous) {
            homeView?.showUserLoginState(true)
        } else {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            homeView?.showUserLoginState(account != null)
            if (account != null) {
                repository.loginFirebaseUser(account.idToken, object : RepositoryListener<String> {
                    override fun onError(error: String) {
                        homeView?.showUserLoginState(false)
                        homeView?.onLoginError(context.getString(R.string.login_error))
                    }
                })
            }
        }
    }
}
