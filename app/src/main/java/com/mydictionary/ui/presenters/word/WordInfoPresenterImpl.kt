package com.mydictionary.ui.presenters.word

import android.content.Context
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
//            wordInfoView?.showIsFavorite(wordInfo.isFavorite)
//            definitions.let {
//                val definitionsList = it.subList(0, minOf(it.size, Constants.TOP_DEFINITIONS_LENGTH))
//                wordInfoView?.showMeanings(definitionsList, it.size > Constants.TOP_DEFINITIONS_LENGTH)
//            }
//            examples.let {
//                val examplesList = it.subList(0, minOf(it.size, Constants.TOP_EXAMPLES_LENGTH))
//                wordInfoView?.showExamples(examplesList, it.size > Constants.TOP_EXAMPLES_LENGTH)
//            }
//            val relatedWords: MutableList<Pair<String, List<String>>> = mutableListOf()
//            addPairIfNotEmpty(R.string.synonyms, synonyms, relatedWords)
//            addPairIfNotEmpty(R.string.antonyms, antonyms, relatedWords)
//            addPairIfNotEmpty(R.string.phrases, also, relatedWords)
//            addPairIfNotEmpty(R.string.derivations, derivation, relatedWords)
//            addPairIfNotEmpty(R.string.typeOf, typeOf, relatedWords)
//            addPairIfNotEmpty(R.string.hasTypes, hasTypes, relatedWords)
//            addPairIfNotEmpty(R.string.partOf, partOf, relatedWords)
//            addPairIfNotEmpty(R.string.hasParts, hasParts, relatedWords)
//            addPairIfNotEmpty(R.string.substanceOf, substanceOf, relatedWords)
//            wordInfoView?.showRelatedWords(relatedWords)
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