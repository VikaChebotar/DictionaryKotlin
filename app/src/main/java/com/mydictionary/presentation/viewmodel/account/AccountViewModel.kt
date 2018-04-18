package com.mydictionary.presentation.viewmodel.account

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.mydictionary.data.repository.WordsRepository
import com.mydictionary.presentation.Data
import com.mydictionary.presentation.DataState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AccountViewModel @Inject constructor(val repository: WordsRepository) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    val userName = MutableLiveData<Data<String>>() //holds username if loggedIn, if not - it's null

    init {
        checkIfLoggedIn()
    }

    fun signIn(googleIdToken: String) {
        compositeDisposable.add(
            repository.loginFirebaseUser(googleIdToken)
                .doOnSubscribe {
                    this.userName.postValue(Data(DataState.LOADING, null, null))
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ userName ->
                    this.userName.value = Data(DataState.SUCCESS, userName, null)
                }, { error ->
                    this.userName.value = Data(DataState.ERROR, null, error.message)
                })
        )
    }

    fun signOut() {
        compositeDisposable.add(
            repository.signOut()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    this.userName.value = Data(DataState.SUCCESS, null, null)
                })
        )
    }

    private fun checkIfLoggedIn() {
        compositeDisposable.add(
            repository.getUserName()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { userName ->
                        this.userName.value = Data(DataState.SUCCESS, userName, null)
                    },
                    {
                        this.userName.value = Data(DataState.SUCCESS, null, null)
                    })
        )
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}