package com.mydictionary.presentation.views.word

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.speech.tts.TextToSpeech
import com.mydictionary.presentation.utils.SPEECH_RATE
import java.util.*

class TextToSpeechHelper(context: Context, lifecycle: Lifecycle) : LifecycleObserver {
    private lateinit var textToSpeech: TextToSpeech

    init {
        lifecycle.addObserver(this)
        textToSpeech = TextToSpeech(context, { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech.language = Locale.UK
                textToSpeech.setSpeechRate(SPEECH_RATE)
            }
        })
    }

    fun onPronounceClicked(word: String) {
        textToSpeech.speak(
            word,
            TextToSpeech.QUEUE_FLUSH,
            null,
            UUID.randomUUID().toString()
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        textToSpeech.stop()
        textToSpeech.shutdown()
    }

}