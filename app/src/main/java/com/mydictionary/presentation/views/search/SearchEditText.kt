package com.mydictionary.presentation.views.search

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.speech.RecognizerIntent
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.mydictionary.R
import com.mydictionary.presentation.utils.CompoundDrawables
import com.mydictionary.presentation.utils.hideKeyboard
import com.mydictionary.presentation.utils.isIntentAvailable


/**
 * Created by Viktoria_Chebotar on 6/15/2017.
 */

class SearchEditText : AppCompatEditText {
    private val isVoiceRecognitionSupported by lazy {
        context.isIntentAvailable(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH))
    }
    private var mIsSearchActive = false
    private var isMiscActive = true

    var contentChangedListener: ContentChangedListener? = null
    var voiceRecognitionListener: VoiceButtonListener? = null

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(string: Editable) {
            contentChangedListener?.onSearchLetterEntered(string.toString())
            if ((string.isNotEmpty() && isMiscActive) || (string.isEmpty() && !isMiscActive)) {
                isMiscActive = !isMiscActive
                showSearchDrawables()
            }
        }
    }

    private val touchListener = View.OnTouchListener { _, event ->
        if (event?.action == MotionEvent.ACTION_UP) {
            val leftBound = compoundDrawables[CompoundDrawables.LEFT.ordinal].bounds.width() +
                    compoundDrawablePadding + paddingLeft
            val rightBound =
                right - compoundDrawables[CompoundDrawables.RIGHT.ordinal].bounds.width() -
                        compoundDrawablePadding - paddingRight
            if (mIsSearchActive && event.rawX <= leftBound) {
                closeSearch()
                return@OnTouchListener true
            } else if (!isMiscActive && event.rawX >= rightBound) {
                clearSearch()
                return@OnTouchListener true
            } else if (isMiscActive && event.rawX >= rightBound) {
                if (isVoiceRecognitionSupported) voiceRecognitionListener?.startVoiceRecognition()
                return@OnTouchListener true
            }
        }
        false
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    init {
        addTextChangedListener(textWatcher)
        setOnFocusChangeListener { _, hasFocus -> onFocusChanged(hasFocus) }
        setOnTouchListener(touchListener)
        setOnEditorActionListener({ _: TextView?, p1: Int, _: KeyEvent? ->
            if (p1 == EditorInfo.IME_ACTION_SEARCH) {
                contentChangedListener?.onSearchLetterEntered(text.toString())
                true
            } else false
        })
    }

    private fun onFocusChanged(hasFocus: Boolean) {
        mIsSearchActive = hasFocus
        showSearchDrawables()
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(state)
        if (text.isNotEmpty()) {
            requestFocus()
        }
    }

    fun closeSearch() {
        clearSearch()
        clearFocus()
        isSelected = false
        context.hideKeyboard(windowToken)
        contentChangedListener?.onSearchClosed()
    }

    fun clearSearch() {
        text.clear()
        contentChangedListener?.onSearchCleared()
    }

    private fun showSearchDrawables() {
        val drawableLeft = if (mIsSearchActive) R.drawable.ic_arrow_back_black_24dp
        else R.drawable.ic_search_black_24dp
        val drawableRight = if (isMiscActive) R.drawable.ic_mic_black_24dp
        else R.drawable.ic_close_black_24dp
        setCompoundDrawablesWithIntrinsicBounds(drawableLeft, 0, drawableRight, 0)
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            dispatchKeyEvent(event)
            closeSearch()
            return false
        }
        return super.onKeyPreIme(keyCode, event)
    }


    interface ContentChangedListener {
        fun onSearchLetterEntered(msg: String)

        fun onSearchCleared()

        fun onSearchClosed()
    }

    interface VoiceButtonListener {
        fun startVoiceRecognition()
    }

}
