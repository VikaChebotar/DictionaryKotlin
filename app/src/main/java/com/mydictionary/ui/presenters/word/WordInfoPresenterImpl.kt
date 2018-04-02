package com.mydictionary.ui.presenters.word

import android.util.Log
import com.mydictionary.R
import com.mydictionary.commons.SELECTED_WORD_NAME_EXTRA
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.data.pojo.WordMeaning
import com.mydictionary.data.repository.WordsRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by Viktoria_Chebotar on 6/30/2017.
 */
class WordInfoPresenterImpl(val repository: WordsRepository) : WordInfoPresenter {
    val TAG = WordInfoPresenterImpl::class.java.simpleName
    var wordInfoView: WordInfoView? = null
    var wordInfo: WordDetails? = null
    val compositeDisposable = CompositeDisposable()

    override fun onStart(view: WordInfoView) {
        this.wordInfoView = view
        val extras = wordInfoView?.getExtras()
        // wordInfo = extras?.getParcelable<WordDetails>(Constants.SELCTED_WORD_INFO_EXTRA) todo

        if (wordInfo != null) {
            wordInfoView?.initToolbar(wordInfo!!.word)
            showWord(wordInfo!!)
        } else {
            val wordName = extras?.getString(SELECTED_WORD_NAME_EXTRA)
            wordName?.let {
                wordInfoView?.initToolbar(it)
                loadWordInfo(it)
            }
        }
    }

    private fun showWord(wordInfo: WordDetails) {
        wordInfoView?.let {
            wordInfoView?.showPronunciation(wordInfo.pronunciation ?: "")
            val wordCardsList = mutableListOf<Any>()
            wordCardsList.add(it.getContext().getString(R.string.definitions) ?: "")
            wordCardsList.addAll(wordInfo.meanings)
            if (wordInfo.synonyms.isNotEmpty()) {
                wordCardsList.add(it.getContext().getString(R.string.synonyms))
                wordCardsList.add(wordInfo.synonyms)
            }
            if (wordInfo.antonyms.isNotEmpty()) {
                wordCardsList.add(it.getContext().getString(R.string.antonyms))
                wordCardsList.add(wordInfo.antonyms)
            }
            if (wordInfo.notes.isNotEmpty()) {
                wordCardsList.add(it.getContext().getString(R.string.notes))
                wordCardsList.addAll(wordInfo.notes)
            }
            it.showWordCards(wordCardsList)
        }
    }

    override fun onFavoriteClicked(item: WordMeaning) {
        wordInfo?.let {
            val favMeanings = mutableListOf<String>()
            it.meanings.filter { it.isFavourite }.forEach { favMeanings.add(it.definitionId) }
            if (favMeanings.contains(item.definitionId)) {
                favMeanings.remove(item.definitionId)
            } else favMeanings.add(item.definitionId)
            compositeDisposable.add(repository.setWordFavoriteState(it, favMeanings).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe({ t -> wordInfo = t }, { error ->
                        Log.e(TAG, "error: " + error)
                        wordInfoView?.showError(error.message ?: "")
                        showWord(wordInfo as WordDetails)
                    }))
        }
    }


    private fun loadWordInfo(wordName: String) {
        compositeDisposable.add(
                Single.just(wordName).
                        doOnEvent { t1, t2 -> wordInfoView?.showProgress(true) }.
                        subscribeOn(Schedulers.io()).
                        flatMap { repository.getWordInfo(it) }.
                        observeOn(AndroidSchedulers.mainThread()).
                        doOnEvent { t1, t2 -> wordInfoView?.showProgress(false) }.
                        subscribe({ wordInfo ->
                            Log.e(TAG, "onnext:"+Thread.currentThread().toString())
                            this@WordInfoPresenterImpl.wordInfo = wordInfo
                            showWord(wordInfo)
                        }, { e ->
                            Log.e(TAG, "error:"+Thread.currentThread().toString())
                            wordInfoView?.let {
                                it.showError(e.message ?: it.getContext().getString(R.string.default_error))
                            }
                        }))
    }


    override fun onStop() {
        wordInfoView = null
        compositeDisposable.clear()
    }
}