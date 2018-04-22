package com.mydictionary.presentation.viewmodel

/**
 * Created by Viktoria Chebotar on 15.04.18.
 */
enum class DataState { LOADING, SUCCESS, ERROR }

data class Data<out T> constructor(val dataState: DataState, val data: T? = null, val message: String? = null)
