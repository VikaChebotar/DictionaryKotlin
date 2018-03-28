package com.mydictionary.ui.presenters.word

import android.util.Log
import com.mydictionary.R
import com.mydictionary.commons.Constants.Companion.SELECTED_WORD_NAME_EXTRA
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.data.pojo.WordMeaning
import com.mydictionary.data.repository.RepositoryListener
import com.mydictionary.data.repository.WordsRepository

/**
 * Created by Viktoria_Chebotar on 6/30/2017.
 */
class WordInfoPresenterImpl(val repository: WordsRepository) : WordInfoPresenter {
    val TAG = WordInfoPresenterImpl::class.java.simpleName
    var wordInfoView: WordInfoView? = null
    var wordInfo: WordDetails? = null

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
            wordCardsList.add(it.getContext().getString(R.string.definitions)?:"")
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
        wordInfoView?.showProgress(true)
        repository.getWordInfo(wordName,
                object : RepositoryListener<WordDetails> {
                    override fun onSuccess(wordInfo: WordDetails) {
                        this@WordInfoPresenterImpl.wordInfo = wordInfo
                        wordInfoView?.showProgress(false)
                        showWord(wordInfo)
                    }

                    override fun onError(error: String) {
                        wordInfoView?.showProgress(false)
                        wordInfoView?.showError(error)
                    }
                })
    }


    override fun onStop() {
        wordInfoView = null
    }
}