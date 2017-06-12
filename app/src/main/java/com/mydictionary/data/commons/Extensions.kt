package com.mydictionary.data.commons

import android.net.ConnectivityManager

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

fun ConnectivityManager.isOnline():Boolean{
  return activeNetworkInfo?.isConnectedOrConnecting ?: false
}
