package com.mydictionary.ui.presenters.learn

import android.util.Log
import com.mydictionary.R
import com.mydictionary.commons.FAV_WORD_PAGE_SIZE
import com.mydictionary.commons.FAV_WORD_PAGE_THRESHOLD
import com.mydictionary.data.pojo.SortingOption
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.data.repository.WordsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers

/**
 * Created by Viktoria_Chebotar on 3/12/2018.
 */
class LearnWordsPresenterImpl(val repository: WordsRepository) : LearnWordsPresenter {
    val TAG = LearnWordsPresenterImpl::class.java.simpleName
    var wordsView: LearnWordsView? = null
    var favWordsOffset = 0
    val list = mutableListOf<WordDetails>()
    val paginator = PublishProcessor.create<Int>();
    var requestUnderWay = false
    var totalSize: Int = 0
    var sortingType: SortingOption = SortingOption.BY_DATE
    private val compositeDisposable = CompositeDisposable()

    override fun onStart(view: LearnWordsView) {
        wordsView = view
        val disposable = paginator.filter { !requestUnderWay }.onBackpressureDrop().doOnNext {
            wordsView?.showProgress(list.isEmpty())
            requestUnderWay = true
        }.
            //subscribeOn(Schedulers.io()).
            concatMap { repository.getFavoriteWordsInfo(it, FAV_WORD_PAGE_SIZE, sortingType) }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
            wordsView?.showProgress(false)
            requestUnderWay = false
        }.subscribe({ pagedResult ->
            totalSize = pagedResult.totalSize
            if (favWordsOffset == 0) {
                list.clear()
                if (pagedResult.list.isNotEmpty()) showPositionText(
                    view.getSelectedPosition(),
                    totalSize
                )
            }
            list.addAll(pagedResult.list)
            wordsView?.showFavoriteWords(list)
            wordsView?.showProgress(false)
            favWordsOffset += pagedResult.list.size
        }, {
            wordsView?.showProgress(false)
            requestUnderWay = false
            Log.e(TAG, it.message ?: view.getContext().getString(R.string.default_error))
            wordsView?.showError(it.message ?: view.getContext().getString(R.string.default_error))
        })
        compositeDisposable.add(disposable)
        loadFavoriteWords()
    }

    override fun onStop() {
        wordsView = null
        compositeDisposable.clear()
    }

    override fun onItemSelected(position: Int) {
        if (position + FAV_WORD_PAGE_THRESHOLD >= favWordsOffset) {
            loadFavoriteWords()
        }
        showPositionText(position, totalSize)
    }

    private fun showPositionText(position: Int, totalSize: Int) {
        wordsView?.showPositionText(
            wordsView?.getContext()?.getString(
                R.string.fav_word_selected,
                position + 1, totalSize
            ) ?: ""
        )
    }

    override fun onItemDeleteClicked(wordDetails: WordDetails) {
        val favMeanings = emptyList<String>()
        val oldFavMeanings = wordDetails.meanings.filter { it.isFavourite }.map { it.definitionId }
        val disposable =
            repository.setWordFavoriteState(wordDetails, favMeanings)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({ wordDetails ->
                if (wordDetails.meanings.none { it.isFavourite }) {
                    val position = list.indexOf(wordDetails)
                    list.remove(wordDetails)
                    wordsView?.showFavoriteWords(list)
                    wordsView?.showWordDeletedMessage(wordDetails, oldFavMeanings, position)
                } else {
                    wordsView?.showError(
                        wordsView?.getContext()?.getString(R.string.delete_word_error) ?: ""
                    )
                }
            }, { exception ->
                Log.e(TAG, "error: " + exception)
                wordsView?.showError(exception.message ?: "")
            })
        compositeDisposable.add(disposable)
    }

    private fun loadFavoriteWords() {
        if (!compositeDisposable.isDisposed)
            paginator.onNext(favWordsOffset)
    }

    override fun onUndoDeletionClicked(
        oldWordDetails: WordDetails,
        favMeanings: List<String>,
        position: Int
    ) {
        val disposable = repository.setWordFavoriteState(oldWordDetails, favMeanings)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({ t ->
            if (t.meanings.any { it.isFavourite }) {
                list.add(position, t)
                wordsView?.showFavoriteWords(list)
            } else {
                //TODO
            }
        }, {
            //TODO
        })
        compositeDisposable.add(disposable)
    }

    override fun onSortSelected(sortingOption: SortingOption) {
        if (sortingOption == sortingType && sortingOption != SortingOption.RANDOMLY) return
        sortingType = sortingOption
        favWordsOffset = 0
        loadFavoriteWords()
    }

    override fun onSortMenuClicked() {
        wordsView?.showSortingDialog(sortingType)
    }
}