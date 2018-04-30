package com.mydictionary.data

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.experimental.channels.Channel
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


suspend fun Query.await(): DataSnapshot =
    suspendCancellableCoroutine { continuation ->
        val listener = object : ValueEventListener {
            override fun onCancelled(e: DatabaseError?) {
                continuation.resumeWithException(e?.toException() ?: Exception("cancelled"))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    continuation.resume(snapshot)
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }
            }
        }
        addListenerForSingleValueEvent(listener)
        continuation.invokeOnCompletion { if (continuation.isCancelled) removeEventListener(listener) }
    }


fun Query.listenAsync() = Channel<DataSnapshot>(Channel.UNLIMITED).apply {
    val listener = object : ValueEventListener {
        override fun onCancelled(e: DatabaseError?) {
            // continuation.resumeWithException(e?.toException() ?: Exception("cancelled"))
            Log.e("TAG", e?.message)
            //todo need to handle exceptions
        }

        override fun onDataChange(snapshot: DataSnapshot) {
            try {
                offer(snapshot)
            } catch (e: Exception) {
                Log.e("TAG", e.message)
            }
        }
    }
    addValueEventListener(listener)
    //todo need to unregister
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

    enqueue(callback)
}

private inline fun <T> Continuation<T>.resumeNormallyOrWithException(getter: () -> T) = try {
    val result = getter()
    resume(result)
} catch (exception: Throwable) {
    resumeWithException(exception)
}

