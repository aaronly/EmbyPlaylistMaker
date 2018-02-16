package us.echols.embyplaylistmaker.util

import android.content.Context
import android.net.ConnectivityManager

object NetworkUtil {

    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo.isConnectedOrConnecting
    }

}
