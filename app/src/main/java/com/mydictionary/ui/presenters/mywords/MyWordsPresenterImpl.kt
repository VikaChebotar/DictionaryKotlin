package com.mydictionary.ui.presenters.mywords

/**
 * Created by Viktoria Chebotar on 09.07.17.
 */
class MyWordsPresenterImpl : MyWordsPresenter {
    var myWordsView: MyWordsView? = null

    override fun onStart(view: MyWordsView) {
        myWordsView = view
    }

    override fun onStop() {
        myWordsView = null
    }


    override fun onFavoriteBtnClicked(position: Int, isFavorite: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRemoveWord(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onWordClicked(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}