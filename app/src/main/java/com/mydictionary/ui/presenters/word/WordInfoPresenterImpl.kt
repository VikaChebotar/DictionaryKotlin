package com.mydictionary.ui.presenters.word

import com.mydictionary.R
import com.mydictionary.commons.Constants
import com.mydictionary.commons.Constants.Companion.SELECTED_WORD_NAME_EXTRA
import com.mydictionary.data.entity.WordInfo
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
        wordInfoView?.showPronunciation(wordInfo.pronunciation ?: "")
        wordInfo.definitions.let {
            val definitionsList = it.subList(0, minOf(it.size, Constants.TOP_DEFINITIONS_LENGTH))
            wordInfoView?.showDefinitions(definitionsList, it.size > Constants.TOP_DEFINITIONS_LENGTH)
        }
        wordInfo.examples.let {
            val examplesList = it.subList(0, minOf(it.size, Constants.TOP_EXAMPLES_LENGTH))
            wordInfoView?.showExamples(examplesList, it.size > Constants.TOP_EXAMPLES_LENGTH)
        }
    }

    private fun loadWordInfo(wordName: String) {
        wordInfoView?.showProgress(true)
        repository.getWordInfo(wordName,
                object : WordsRepository.WordSourceListener<WordInfo> {
                    override fun onSuccess(wordInfo: WordInfo) {
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

    override fun onSeeAllDefinitionsBtnClicked(definitionsCount: Int) {
        if (wordInfo == null || wordInfoView == null) return
        collapseOrExpandList(wordInfo!!.definitions, definitionsCount, Constants.TOP_DEFINITIONS_LENGTH,
                { a, b -> wordInfoView!!.showDefinitions(a, b) },
                { a -> wordInfoView!!.setSeeAllDefinitionsBtnText(a) })
    }

    override fun onSeeAllExamplesBtnClicked(examplesCount: Int) {
        if (wordInfo == null || wordInfoView == null) return
        collapseOrExpandList(wordInfo!!.examples, examplesCount, Constants.TOP_EXAMPLES_LENGTH,
                { a, b -> wordInfoView!!.showExamples(a, b) },
                { a -> wordInfoView!!.setSeeAllExamplesBtnText(a) })
    }

    private fun <T> collapseOrExpandList(list: List<T>, visibleCount: Int, minCount: Int,
                                         showList: (list: List<T>, showBtn: Boolean) -> Unit,
                                         setBtnText: (textRes: Int) -> Unit) {
        if (visibleCount < list.size) {
            showList(list, true)
            setBtnText(R.string.collapse)
        } else {
            showList(list.subList(0, minCount), true)
            setBtnText(R.string.see_all)
        }
    }

    override fun onStop() {
        wordInfoView = null
    }
}