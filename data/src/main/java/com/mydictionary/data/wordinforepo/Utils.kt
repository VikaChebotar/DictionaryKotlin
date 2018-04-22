package com.mydictionary.data.wordinforepo

import android.net.ConnectivityManager

/**
 * Created by Viktoria Chebotar on 22.04.18.
 */

const val OXFORD_API_ENDPOINT = "https://od-api.oxforddictionaries.com/api/v1/"
const val OXFORD_API_APP_KEY_HEADER = "app_key"
const val OXFORD_API_APP_ID_HEADER = "app_id"
const val MEMORY_CACHE_PERCENT = 0.125

fun ConnectivityManager.isOnline(): Boolean {
    return activeNetworkInfo?.isConnectedOrConnecting ?: false
}


fun getCacheMemorySize(): Int {
    // Get max available VM memory, exceeding this amount will throw an OutOfMemory exception.
    val maxMemory = Runtime.getRuntime().maxMemory()
    return (maxMemory * MEMORY_CACHE_PERCENT).toInt();
}