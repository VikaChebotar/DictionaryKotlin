package com.mydictionary.ui.presenters.home

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.mydictionary.data.repository.WordsRepository




/**
 * Created by Viktoria Chebotar on 18.06.17.
 */

class HomePresenterImpl(val repository: WordsRepository, val context: Context) : HomePresenter {
    val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
    val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions);
    var homeView: HomeView? = null

    override fun onStart(view: HomeView) {
        this.homeView = view
        val account = GoogleSignIn.getLastSignedInAccount(context)
        homeView?.showUserLoginState(account == null)
    }

    override fun onMyWordsBtnClicked() {
        homeView?.startMyWordsActivity()
    }

    override fun onSingInClicked() {
        val signInIntent = googleSignInClient.getSignInIntent()

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
}
