package com.mydictionary.commons

/**
 * Created by Viktoria_Chebotar on 3/5/2018.
 */
class Utils {
    companion object {
        fun getCacheMemorySize(): Int {
            // Get max available VM memory, exceeding this amount will throw an OutOfMemory exception.
            val maxMemory = Runtime.getRuntime().maxMemory()
            return (maxMemory * Constants.MEMORY_CACHE_PERCENT).toInt();
        }
    }
}