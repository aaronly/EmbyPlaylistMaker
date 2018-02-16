package us.echols.embyplaylistmaker.util

import android.app.Activity
import android.app.Fragment
import android.support.annotation.IdRes

@JvmOverloads
fun <F : Fragment> Activity.addFragment(
        @IdRes container: Int, fragment: F,
        addToBackStack: Boolean = false
) {

    val transaction = fragmentManager.beginTransaction().add(container, fragment)

    if (addToBackStack) {
        transaction.addToBackStack(null)
    }

    transaction.commit()
}

@JvmOverloads
fun <F : Fragment> Activity.replaceFragment(
        @IdRes container: Int, fragment: F,
        addToBackStack: Boolean = false
) {

    val transaction = fragmentManager.beginTransaction().replace(container, fragment)

    if (addToBackStack) {
        transaction.addToBackStack(null)
    }

    transaction.commit()
}