package com.mydictionary.ui.presenters.word

import android.content.Context
import com.mydictionary.R
import com.mydictionary.commons.Constants.Companion.SELECTED_WORD_NAME_EXTRA
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.data.repository.RepositoryListener
import com.mydictionary.data.repository.WordsRepository

/**
 * Created by Viktoria_Chebotar on 6/30/2017.
 */
class WordInfoPresenterImpl(val repository: WordsRepository, val context: Context) : WordInfoPresenter {
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

    //  override fun onFavoriteClicked() {
    //      wordInfo?.let {
//            repository.setWordFavoriteState(wordInfo!!.word, !wordInfo!!.isFavorite,
//                    object : RepositoryListener<Boolean> {
//                        override fun onSuccess(t: Boolean) {
//                            wordInfo?.isFavorite = t
//                            wordInfoView?.showIsFavorite(t)
//                        }
//
//                        override fun onError(error: String) {
//
//                        }
//                    })
    //   }

//    }


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