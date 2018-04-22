package com.mydictionary.domain.entity

data class PagedResult<T>(val list: List<T>, val totalSize: Int)

enum class SortingOption { BY_DATE, BY_NAME, RANDOMLY }