package com.mydictionary.data.pojo

/**
 * Created by Viktoria_Chebotar on 3/28/2018.
 */
data class PagedResult<T>(val list: List<T>, val totalSize: Int)

enum class SortingOption { BY_DATE, BY_NAME, RANDOMLY }