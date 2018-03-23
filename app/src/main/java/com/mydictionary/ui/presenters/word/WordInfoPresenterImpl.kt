package com.mydictionary.ui.presenters.word

import android.content.Context
import android.util.Log
import com.mydictionary.R
import com.mydictionary.commons.Constants.Companion.SELECTED_WORD_NAME_EXTRA
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.data.pojo.WordMeaning
import com.mydictionary.data.repository.RepositoryListener
import com.mydictionary.data.repository.WordsRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by Viktoria_Chebotar on 6/30/2017.
 */
class WordInfoPresenterImpl(val repository: WordsRepository, val context: Context) : WordInfoPresenter {
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
        wordInfo.apply {
            wordInfoView?.showPronunciation(pronunciation ?: "")
            val wordCardsList = mutableListOf<Any>()
            wordCardsList.add(context.getString(R.string.definitions))
            wordCardsList.addAll(wordInfo.meanings)
            if (wordInfo.synonyms.isNotEmpty()) {
                wordCardsList.add(context.getString(R.string.synonyms))
                wordCardsList.add(wordInfo.synonyms)
            }
            if (wordInfo.antonyms.isNotEmpty()) {
                wordCardsList.add(context.getString(R.string.antonyms))
                wordCardsList.add(wordInfo.antonyms)
            }
            if (wordInfo.notes.isNotEmpty()) {
                wordCardsList.add(context.getString(R.string.notes))
                wordCardsList.addAll(wordInfo.notes)
            }
            wordInfoView?.showWordCards(wordCardsList)
        }
    }

    override fun onFavoriteClicked(item: WordMeaning) {

        wordInfo?.let {
            val favMeanings = mutableListOf<String>()
            it.meanings.filter { it.isFavourite }.forEach { favMeanings.add(it.definitionId) }
            if (favMeanings.contains(item.definitionId)) {
                favMeanings.remove(item.definitionId)
            } else favMeanings.add(item.definitionId)
            repository.setWordFavoriteState(it, favMeanings, object : RepositoryListener<WordDetails> {
                override fun onSuccess(t: WordDetails) {
                    wordInfo = t
                }

                override fun onError(error: String) {
                    Log.e(TAG, "error: " + error)
                    wordInfoView?.showError(error)
                    showWord(wordInfo as WordDetails)
                }

            })
        }

    }


    private fun loadWordInfo(wordName: String) {
        compositeDisposable.add(
                Single.just(wordName).
                        doOnEvent { t1, t2 ->   wordInfoView?.showProgress(true)}.
                        observeOn(Schedulers.io()).
                        flatMap { repository.getWordInfo(it) }.
                        subscribeOn(AndroidSchedulers.mainThread()).
                        doOnEvent { t1, t2 -> wordInfoView?.showProgress(false) }.
                        subscribe({ wordInfo ->
                            this@WordInfoPresenterImpl.wordInfo = wordInfo
                            showWord(wordInfo)
                        }, {
                            wordInfoView?.showError(it.message ?: context.getString(R.string.default_error))
                        }))
    }


    override fun onStop() {
        wordInfoView = null
        compositeDisposable.clear()
    }
}