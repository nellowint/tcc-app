package br.com.vieirateam.tcc.util

import android.content.Context
import android.net.ConnectivityManager

object InternetUtil {

    fun checkInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        if (netInfo != null && netInfo.isConnected) {
            return false
        }
        return true
    }
}