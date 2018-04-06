package com.mydictionary.ui.presenters.home

import com.mydictionary.R
import com.mydictionary.data.repository.WordsRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


/**
 * Created by Viktoria Chebotar on 18.06.17.
 */
const val SIGN_IN_REQUEST_CODE = 1

class HomePresenterImpl(val repository: WordsRepository) : HomePresenter {
    val TAG = HomePresenterImpl::class.java.simpleName
    private var homeView: HomeView? = null
    private val compositeDisposable = CompositeDisposable()
    private var wordList = listOf<WordListItem>()

    override fun onStart(view: HomeView) {
        this.homeView = view
        loadWordLists()
    }

    override fun onMyWordsBtnClicked() {
        homeView?.startMyWordsActivity()
    }

    override fun onStop() {
        homeView = null
        compositeDisposable.clear()
    }

    private fun loadWordLists() {
        if (wordList.isNotEmpty()) {
            homeView?.showWordLists(wordList)
            return
        }
        homeView?.showProgress(true)
        compositeDisposable.add(
            repository.getAllWordLists()
                .toObservable()
                .flatMapIterable { it -> it }
                .groupBy { it.type }
                .flatMap {
                    if (it.key != null) {
                        val observable = it.map { WordListItem.WordList(it.name, it.type, it.list) }
                        Observable.merge(
                            Observable.just(WordListItem.ListCategory(it.key!!)),
                            observable
                        )
                    } else
                        Observable.empty<WordListItem>()
                }.toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnEvent { t1, t2 -> homeView?.showProgress(false) }
                .subscribe({
                    wordList = it
                    homeView?.showWordLists(wordList)
                }, { throwable ->
                    throwable.printStackTrace()
                    homeView?.let {
                        it.showError(
                            throwable.message
                                    ?: it.getContext().getString(R.string.default_error)
                        )
                    }
                })
        )
    }
}
