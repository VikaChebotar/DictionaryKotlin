package com.mydictionary.ui.presenters.word

import android.content.Context
import com.mydictionary.R
import com.mydictionary.commons.Constants
import com.mydictionary.commons.Constants.Companion.SELECTED_WORD_NAME_EXTRA
import com.mydictionary.data.entity.WordInfo
import com.mydictionary.data.repository.WordsRepository

/**
 * Created by Viktoria_Chebotar on 6/30/2017.
 */
class WordInfoPresenterImpl(val repository: WordsRepository, val context: Context) : WordInfoPresenter {
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
        wordInfo.apply {
            definitions.let {
                val definitionsList = it.subList(0, minOf(it.size, Constants.TOP_DEFINITIONS_LENGTH))
                wordInfoView?.showDefinitions(definitionsList, it.size > Constants.TOP_DEFINITIONS_LENGTH)
            }
            examples.let {
                val examplesList = it.subList(0, minOf(it.size, Constants.TOP_EXAMPLES_LENGTH))
                wordInfoView?.showExamples(examplesList, it.size > Constants.TOP_EXAMPLES_LENGTH)
            }
            val relatedWords: MutableList<Pair<String, List<String>>> = mutableListOf()
            addPairIfNotEmpty(R.string.synonyms, synonyms, relatedWords)
            addPairIfNotEmpty(R.string.antonyms, antonyms, relatedWords)
            addPairIfNotEmpty(R.string.phrases, also, relatedWords)
            addPairIfNotEmpty(R.string.derivations, derivation, relatedWords)
            addPairIfNotEmpty(R.string.typeOf, typeOf, relatedWords)
            addPairIfNotEmpty(R.string.hasTypes, hasTypes, relatedWords)
            addPairIfNotEmpty(R.string.partOf, partOf, relatedWords)
            addPairIfNotEmpty(R.string.hasParts, hasParts, relatedWords)
            addPairIfNotEmpty(R.string.substanceOf, substanceOf, relatedWords)
            wordInfoView?.showRelatedWords(relatedWords)
        }
    }

    private fun addPairIfNotEmpty(titleRes: Int, list: List<String>,
                                  allList: MutableList<Pair<String, List<String>>>) {
        if (list.isNotEmpty()) {
            allList.add(Pair(context.getString(titleRes), list.distinct()))
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

    override fun onWordClicked(word: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun <T> collapseOrExpandList(list: List<T>, visibleCount: Int, minCount: Int,
                                         showList: (list: List<T>, showBtn: Boolean) -> Unit,
                                         setBtnText: (textRes: Int) -> Unit) {
        val listToShow = if (visibleCount < list.size) list else list.subList(0, minCount)
        val textToShow = if (visibleCount < list.size) R.string.collapse else R.string.see_all
        showList(listToShow, true)
        setBtnText(textToShow)
    }

    override fun onStop() {
        wordInfoView = null
    }
}