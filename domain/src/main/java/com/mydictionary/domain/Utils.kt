package com.mydictionary.domain

import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Created by Viktoria Chebotar on 22.04.18.
 */
const val AUTOCOMPLETE_DELAY = 500L
const val MESSAGE_TEXT_CHANGED = 100
const val SEARCH_LIMIT = 10
const val HISTORY_SEARCH_LIMIT = 3
const val MIN_WORD_LENGTH_TO_SEARCH = 2
const val TOP_DEFINITIONS_LENGTH = 3
const val TOP_EXAMPLES_LENGTH = 3
const val TOP_RELATED_WORDS_LIMIT = 12
const val RESPONSE_CODE_NOT_FOUND = 404
const val VOICE_SEARCH_PAUSE_MILLIS = 500
const val MAX_HISTORY_LIMIT = 5
const val FAV_WORD_PAGE_SIZE = 10
const val FAV_WORD_PAGE_THRESHOLD = 5
const val SHUFFLE_DOWNLOAD_PAGE_SIZE = 30
const val DEFAULT_PAGE_SIZE = 100

class NoConnectivityException : Exception("No connectivity exception")

class AuthorizationException(message: String = "User is not signed in") : Exception(message)

fun <T> ReceiveChannel<T>.debounce(
    settleTime: Long,
    context: CoroutineContext = DefaultDispatcher
): ReceiveChannel<T> = produce(context) {
    var job: Job? = null
    consumeEach {
        job?.cancel()
        job = launch {
            delay(settleTime)
            send(it)
        }
    }
    job?.join() //waiting for the last debouncing to end
}
