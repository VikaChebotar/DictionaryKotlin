package com.mydictionary.ui.presenters.word

import com.mydictionary.commons.Constants
import com.mydictionary.commons.Constants.Companion.SELECTED_WORD_NAME_EXTRA
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.data.pojo.WordInfo
import com.mydictionary.data.repository.WordsRepository

/**
 * Created by Viktoria_Chebotar on 6/30/2017.
 */
class WordInfoPresenterImpl(val repository: WordsRepository) : WordInfoPresenter {
    var wordInfoView: WordInfoView? = null
    var wordInfo: WordInfo? = null

    override fun onStart(view: WordInfoView) {
        this.wordInfoView = view
        val extras = wordInfoView?.getExtras()
        wordInfo = extras?.getParcelable<WordInfo>(Constants.SELCTED_WORD_INFO_EXTRA)

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

    private fun showWord(wordInfo: WordInfo) {
        wordInfoView?.bindWordInfo(wordInfo)
    }

    private fun loadWordInfo(wordName: String) {
        wordInfoView?.showProgress(true)
        repository.getWordInfo(wordName,
                object : WordsRepository.WordSourceListener<WordDetails> {
                    override fun onSuccess(wordInfo: WordDetails) {

                        wordInfoView?.showProgress(false)

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