package com.mydictionary.data.commons

import android.net.ConnectivityManager
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
