package us.echols.embyplaylistmaker.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun View.visible(visible: Boolean) {
    visibility = if (visible) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

//@JvmOverloads
//fun View.showSnackBar(message: String, duration: Int = Snackbar.LENGTH_LONG) {
//    Snackbar.make(this, message, duration).show()
//}