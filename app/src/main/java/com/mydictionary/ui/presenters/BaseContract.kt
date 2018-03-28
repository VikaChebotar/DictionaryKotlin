package com.mydictionary.ui.presenters

import android.content.Context

/**
 * Created by Viktoria Chebotar on 25.06.17.
 */
interface BasePresenter<T : BaseView> {
    fun onStart(view: T)
    fun onStop()
}

interface BaseView {
    fun showProgress(progress: Boolean)
    fun showError(message: String)
    fun getContext(): Context
}