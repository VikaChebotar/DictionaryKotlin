package com.mydictionary.commons

/**
 * Created by Viktoria_Chebotar on 3/5/2018.
 */

fun getCacheMemorySize(): Int {
    // Get max available VM memory, exceeding this amount will throw an OutOfMemory exception.
    val maxMemory = Runtime.getRuntime().maxMemory()
    return (maxMemory * MEMORY_CACHE_PERCENT).toInt();
}

class NoConnectivityException:Exception("No connectivity exception")
