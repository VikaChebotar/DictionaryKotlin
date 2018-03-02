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
}

interface HomeView : BaseView {
    fun startWordInfoActivity(word: WordDetails)
    fun startMyWordsActivity()
    fun startSignInActivity(intent: Intent)
    fun showUserLoginState(isLoggedIn: Boolean)
}

