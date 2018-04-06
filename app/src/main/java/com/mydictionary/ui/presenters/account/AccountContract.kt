package com.mydictionary.ui.presenters.account

import android.content.Intent
import com.mydictionary.ui.presenters.BasePresenter
import com.mydictionary.ui.presenters.BaseView

interface AccountPresenter : BasePresenter<AccountView> {
    fun onSingInClicked()
    fun onSignOutClicked()
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}

interface AccountView : BaseView {
    fun startSignInActivity(intent: Intent, requestCode: Int)
    fun showUserLoginState(isLoggedIn: Boolean, userName: String? = null)
}
