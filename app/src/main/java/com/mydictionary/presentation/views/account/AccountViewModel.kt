package com.mydictionary.presentation.views.account

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.mydictionary.domain.usecases.ShowUserUseCase
import com.mydictionary.domain.usecases.SignInUseCase
import com.mydictionary.domain.usecases.SignOutUseCase
import com.mydictionary.presentation.views.Data
import com.mydictionary.presentation.views.DataState
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class AccountViewModel @Inject constructor(
        private val showUserUseCase: ShowUserUseCase,
        private val signInUseCase: SignInUseCase,
        private val signOutUseCase: SignOutUseCase
) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    val userName = MutableLiveData<Data<String>>() //holds username if loggedIn, if not - it's null

    init {
        checkIfLoggedIn()
    }

    fun signIn(googleIdToken: String?) {
        compositeDisposable.add(
          signInUseCase.execute(googleIdToken)
                .doOnSubscribe {
                    this.userName.postValue(
                        Data(
                            DataState.LOADING,
                            null,
                            null
                        )
                    )
                }
                .subscribe({ user ->
                    this.userName.value = Data(
                        DataState.SUCCESS,
                        user.email,
                        null
                    )
                }, { error ->
                    this.userName.value = Data(
                        DataState.ERROR,
                        null,
                        error.message
                    )
                })
        )
    }

    fun signOut() {
        compositeDisposable.add(
         signOutUseCase.execute()
                .subscribe({
                    this.userName.value = Data(
                        DataState.SUCCESS,
                        null,
                        null
                    )
                })
        )
    }

    private fun checkIfLoggedIn() {
        compositeDisposable.add(
            showUserUseCase.execute()
                .subscribe(
                    { user ->
                        this.userName.value = Data(
                            DataState.SUCCESS,
                            user.email,
                            null
                        )
                    },
                    {
                        this.userName.value = Data(
                            DataState.SUCCESS,
                            null,
                            null
                        )
                    })
        )
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}