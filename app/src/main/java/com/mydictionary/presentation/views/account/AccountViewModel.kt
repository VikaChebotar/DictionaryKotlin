package com.mydictionary.presentation.views.account

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.mydictionary.domain.entity.Result
import com.mydictionary.domain.usecases.ShowUserUseCase
import com.mydictionary.domain.usecases.SignInUseCase
import com.mydictionary.domain.usecases.SignOutUseCase
import com.mydictionary.presentation.views.Data
import com.mydictionary.presentation.views.DataState
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class AccountViewModel @Inject constructor(
    private val showUserUseCase: ShowUserUseCase,
    private val signInUseCase: SignInUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {
    val userName = MutableLiveData<Data<String>>() //holds username if loggedIn, if not - it's null
    private var job: Job? = null

    init {
        checkIfLoggedIn()
    }

    fun signIn(googleIdToken: String) {
        job = launch(UI) {
            userName.value = Data(DataState.LOADING, null, null)
            val result = signInUseCase.execute(googleIdToken)
            when (result) {
                is Result.Success -> userName.value =
                        Data(DataState.SUCCESS, result.data.email, null)
                is Result.Error -> userName.value =
                        Data(DataState.ERROR, null, result.exception.message)
            }
        }
    }

    fun signOut() {
        job = launch(UI) {
            signOutUseCase.execute()
            userName.value = Data(DataState.SUCCESS, null, null)
        }
    }

    private fun checkIfLoggedIn() {
        job = launch(UI) {
            val result = showUserUseCase.execute()
            when (result) {
                is Result.Success -> userName.value =
                        Data(DataState.SUCCESS, result.data.email, null)
                is Result.Error -> userName.value = Data(DataState.SUCCESS, null, null)
            }
        }
    }

    override fun onCleared() {
        job?.cancel()
        super.onCleared()
    }
}