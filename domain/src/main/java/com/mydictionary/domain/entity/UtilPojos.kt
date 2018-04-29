package com.mydictionary.domain.entity

data class PagedResult<T>(val list: List<T>, val totalSize: Int)

enum class SortingOption { BY_DATE, BY_NAME, RANDOMLY }

sealed class Result<out T : Any?> {
    class Success<out T : Any?>(val data: T?) : Result<T>()

    class Error(val exception: Throwable) : Result<Nothing>()
}