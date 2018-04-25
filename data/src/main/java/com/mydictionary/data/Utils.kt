package com.mydictionary.data

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.experimental.Continuation

/**
 * @throws IllegalStateException
 */
suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
    val callback = OnCompleteListener<T> { task ->
        if (task.isSuccessful) {
            continuation.resume(task.result)
        } else {
            continuation.resumeWithException(task.exception ?: Exception())
        }
    }
    addOnCompleteListener(callback)
}

suspend fun <T> Call<T>.await(): T = suspendCancellableCoroutine { continuation ->
    continuation.invokeOnCompletion { if (continuation.isCancelled) cancel() }
    val callback = object : Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) = continuation.resumeWithException(t)
        override fun onResponse(call: Call<T>, response: Response<T>) =
            continuation.resumeNormallyOrWithException {
                response.isSuccessful || throw IllegalStateException("Http error ${response.code()}")
                response.body() ?: throw IllegalStateException("Response body is null")
            }
    }

    enqueue(callback) // TODO: cancellation (invoke Call.cancel() when coroutine is cancelled)
}

private inline fun <T> Continuation<T>.resumeNormallyOrWithException(getter: () -> T) = try {
    val result = getter()
    resume(result)
} catch (exception: Throwable) {
    resumeWithException(exception)
}

