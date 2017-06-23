package com.mydictionary.ui.views.search

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.os.Parcelable
import android.speech.RecognizerIntent
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.mydictionary.R
import com.mydictionary.commons.Constants.Companion.AUTOCOMPLETE_DELAY
import com.mydictionary.commons.Constants.Companion.MESSAGE_TEXT_CHANGED
import com.mydictionary.commons.hideKeyboard
import com.mydictionary.commons.isIntentAvailable
import java.lang.ref.WeakReference


/**
 * Created by Viktoria_Chebotar on 6/15/2017.
 */

class SearchEditText : AppCompatEditText {
    private val isVoiceRecognitionSupported: Boolean
    private val handler = MyHandler(this)
    private var mIsSearchActive = false
    private var isMiscActive = true

    var openCloseListener: OpenCloseListener? = null
    var contentChangedListener: ContentChangedListener? = null

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(string: Editable) {
            handler.removeMessages(MESSAGE_TEXT_CHANGED)
            handler.sendMessageDelayed(handler.obtainMessage(MESSAGE_TEXT_CHANGED, string.toString()), AUTOCOMPLETE_DELAY)
            if ((string.isNotEmpty() && isMiscActive) || (string.isEmpty() && !isMiscActive)) {
                isMiscActive = !isMiscActive
                showSearchDrawables()
            }
        }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        isVoiceRecognitionSupported = context.isIntentAvailable(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH))
        addTextChangedListener(textWatcher)
        setOnFocusChangeListener { v, hasFocus ->
            mIsSearchActive = hasFocus
            showSearchDrawables()
            if (hasFocus) openCloseListener?.onSearchOpened();
        }
        setOnTouchListener(View.OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_RIGHT = 2
            if (event?.action == MotionEvent.ACTION_UP) {
                val leftBound = compoundDrawables[DRAWABLE_LEFT].bounds.width()+ compoundDrawablePadding + paddingLeft
                val rightBound = right - compoundDrawables[DRAWABLE_RIGHT].bounds.width() - compoundDrawablePadding - paddingRight
                if (mIsSearchActive && event.rawX <= leftBound) {
                    closeSearch()
                    return@OnTouchListener true
                } else if (!isMiscActive && event.rawX >= rightBound) {
                    clearSearch()
                    return@OnTouchListener true
                } else if (isMiscActive && event.rawX >= rightBound) {
                    startVoiceRecognition()
                }
            }
            false
        })
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(state)
        if (text.isNotEmpty()) {
            requestFocus()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacksAndMessages(null);
    }

    fun closeSearch() {
        clearSearch()
        clearFocus()
        isSelected = false
        context.hideKeyboard(windowToken)
        openCloseListener?.onSearchClosed()
    }

    fun clearSearch() {
        text.clear()
        handler.removeCallbacksAndMessages(null);
        contentChangedListener?.onSearchCleared()
    }

    private fun showSearchDrawables() {
        val drawableLeft = if (mIsSearchActive) R.drawable.ic_arrow_back_black_24dp else R.drawable.ic_search_black_24dp
        val drawableRight = if (isMiscActive) R.drawable.ic_mic_black_24dp else R.drawable.ic_close_black_24dp
        setCompoundDrawablesWithIntrinsicBounds(drawableLeft, 0, drawableRight, 0)
    }

    //TODO refactor
    fun startVoiceRecognition() {
//        if (isVoiceRecognitionSupported && fragment != null) {
//            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
//            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
//                    context.getString(R.string.speak_now))
//            (fragment as Fragment).startActivityForResult(intent, VOICE_RECOGNITION_CODE)
//        }
    }


    internal class MyHandler(editText: SearchEditText) : Handler() {
        private val editTextWeakRef = WeakReference<SearchEditText>(editText)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            editTextWeakRef.get()?.contentChangedListener?.onSearchLetterEntered(msg.obj as String);
        }
    }

    interface OpenCloseListener {
        fun onSearchOpened()

        fun onSearchClosed()
    }

    interface ContentChangedListener {
        fun onSearchLetterEntered(msg: String)

        fun onSearchCleared()
    }
}
