package com.mydictionary.ui.presenters.mywords

import com.mydictionary.R
import com.mydictionary.data.repository.WordsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Viktoria Chebotar on 09.07.17.
 */
class WordsPresenterImpl(val repository: WordsRepository) : WordsPresenter {
    var wordsView: WordsView? = null

    override fun onStart(view: WordsView) {
        wordsView = view
        loadWordList(view.getWordListName())
    }

    override fun onStop() {
        wordsView = null
    }

    private fun loadWordList(wordListName: String) {
        wordsView?.showProgress(true)
        repository.getWordList(wordListName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnEvent { t1, t2 -> wordsView?.showProgress(false) }
            .subscribe({ wordsView?.showWords(it) }, { throwable ->
                throwable.printStackTrace()
                wordsView?.let {
                    it.showError(
                        throwable.message
                                ?: it.getContext().getString(R.string.default_error)
                    )
                }
            })
    }
}