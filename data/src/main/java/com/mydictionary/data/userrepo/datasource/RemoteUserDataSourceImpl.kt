package com.mydictionary.data.userrepo.datasource

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.mydictionary.data.R
import com.mydictionary.domain.AuthorizationException
import io.reactivex.Completable
import io.reactivex.Single

class RemoteUserDataSourceImpl(
    val context: Context,
    val firebaseAuth: FirebaseAuth,
    val mapper: UserMapper
) : UserDataSource {
    private val TAG = RemoteUserDataSourceImpl::class.java.simpleName

    override fun getUser() = Single
        .create<FirebaseUser> { emitter ->
            firebaseAuth.currentUser?.let { emitter.onSuccess(it) }
                    ?: emitter.onError(AuthorizationException())
        }
        .map { mapper.mapFromFirebaseUserToUser(it) }

    override fun signIn(token: String) = Single.create<FirebaseUser> { emitter ->
        val credential = GoogleAuthProvider.getCredential(token, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result.user != null) {
                    emitter.onSuccess(task.result.user!!)
                } else {
                    Log.e(TAG, "firebaseAuthWithGoogle:failure", task.getException())
                    emitter.onError(
                        task.exception
                                ?: Exception(context.getString(R.string.login_error))
                    )
                }
            }
    }
        .map { mapper.mapFromFirebaseUserToUser(it) }

    override fun signOut() = Completable.create { emitter ->
        firebaseAuth.signOut()
        emitter.onComplete()
    }

}