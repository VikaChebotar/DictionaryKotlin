package com.mydictionary.ui.presenters.home

import android.app.Activity
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
    var isLoggedIn = false
    var favWordsOffset = 0

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
        favWordsOffset = 0
        loadFavoriteWords()
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
        if (requestCode == SIGN_IN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            homeView?.showProgress(true)
            try {
                val account = task.getResult(ApiException::class.java)
                repository.loginFirebaseUser(account.idToken, object : RepositoryListener<String> {
                    override fun onSuccess(userName: String) {
                        homeView?.onLoginSuccess(userName)
                        homeView?.showProgress(false)
                        homeView?.showUserLoginState(true)
                        isLoggedIn = true
                        loadFavoriteWords()
                    }

                    override fun onError(error: String) {
                        Log.e(TAG, error)
                        homeView?.onLoginError(context.getString(R.string.login_error))
                        homeView?.showProgress(false)
                        isLoggedIn = false
                    }
                })
            } catch (e: ApiException) {
                Log.e(TAG, e.message)
                homeView?.onLoginError(context.getString(R.string.login_error))
                homeView?.showProgress(false)
                isLoggedIn = false
            }
        }
    }

    override fun onFavListScrolled(page: Int, totalItemsCount: Int) {
        loadFavoriteWords()
    }

    private fun checkIfLoggedIn() {
        val firebaseUser = repository.getCurrentUser()
        if (firebaseUser != null) {
            homeView?.showUserLoginState(true)
            isLoggedIn = true
        } else {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            isLoggedIn = account != null
            homeView?.showUserLoginState(isLoggedIn)
            if (account != null) {
                repository.loginFirebaseUser(account.idToken, object : RepositoryListener<String> {
                    override fun onError(error: String) {
                        homeView?.showUserLoginState(false)
                        homeView?.onLoginError(context.getString(R.string.login_error))
                        isLoggedIn = false
                    }
                })
            }
        }
    }

    private fun loadFavoriteWords() {
//        if (isLoggedIn) {
//            val time = System.currentTimeMillis()
//            homeView?.showProgress(favWordsOffset == 0)
//            repository.getFavoriteWords(favWordsOffset, Constants.FAV_WORD_PAGE_SIZE, object : RepositoryListener<List<WordDetails>> {
//                override fun onSuccess(t: List<WordDetails>) {
//                    super.onSuccess(t)
//                    Log.d(TAG, "time" + (System.currentTimeMillis() - time))
//                    homeView?.showFavoriteWords(t, favWordsOffset == 0)
//                    homeView?.showProgress(false)
//                    favWordsOffset += t.size
//                }
//
//                override fun onError(error: String) {
//                    super.onError(error)
//                    Log.d(TAG, "error time" + (System.currentTimeMillis() - time))
//                    homeView?.showProgress(false)
//                    Log.e(TAG, error)
//                }
//            })
//        }
    }
}
