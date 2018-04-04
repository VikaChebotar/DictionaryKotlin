package com.mydictionary.ui.presenters.home

import android.content.Intent
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.ui.presenters.BasePresenter
import com.mydictionary.ui.presenters.BaseView

/**
 * Created by Viktoria Chebotar on 18.06.17.
 */

interface HomePresenter : BasePresenter<HomeView> {
    fun onResume()
    fun onMyWordsBtnClicked()
    fun onSingInClicked()
    fun onSignOutClicked()
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}

interface HomeView : BaseView {
    fun startWordInfoActivity(word: WordDetails)
    fun startMyWordsActivity()
    fun startSignInActivity(intent: Intent, requestCode: Int)
    fun showUserLoginState(isLoggedIn: Boolean)
    fun onLoginError(message: String)
    fun onLoginSuccess(userName: String)
    fun showWordLists(list: List<WordListItem>)
}

