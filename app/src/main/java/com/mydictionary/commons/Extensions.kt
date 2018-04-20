package com.mydictionary.commons

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.IBinder
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import java.util.*

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

fun ConnectivityManager.isOnline(): Boolean {
    return activeNetworkInfo?.isConnectedOrConnecting ?: false
}

fun Date.isSameDay(otherDate: Date): Boolean {
    val thisCalendar = Calendar.getInstance()
    thisCalendar.time = this
    val otherCalendar = Calendar.getInstance()
    otherCalendar.time = otherDate
    return (thisCalendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR) &&
            thisCalendar.get(Calendar.MONTH) == otherCalendar.get(Calendar.MONTH) &&
            thisCalendar.get(Calendar.DAY_OF_MONTH) == otherCalendar.get(Calendar.DAY_OF_MONTH))
}

fun Context.isIntentAvailable(intent: Intent): Boolean {
    val list = packageManager?.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    return (list?.size ?: 0) > 0
}

fun Context.hideKeyboard(windowToken: IBinder) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun ViewGroup.inflate(layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun String.containsWhiteSpace(): Boolean {
    val strLen = this.length
    return (0 until strLen).any { Character.isWhitespace(this.toCharArray()[it]) }
}


fun EditText.showKeyboard() {
    requestFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);
}

inline fun <reified T : ViewModel> FragmentActivity.getViewModel(viewModelFactory: ViewModelProvider.Factory): T {
    return ViewModelProviders.of(this, viewModelFactory)[T::class.java]
}

