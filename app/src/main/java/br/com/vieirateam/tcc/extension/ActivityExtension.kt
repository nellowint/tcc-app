package br.com.vieirateam.tcc.extension

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import br.com.vieirateam.tcc.R
import android.support.v7.app.AppCompatActivity
import android.util.Log
import java.lang.IllegalStateException

fun AppCompatActivity.addFragment(@IdRes layoutId: Int, fragment: Fragment): Boolean {
    val fragmentTransaction = supportFragmentManager.beginTransaction()
    val attachedFragment = supportFragmentManager.findFragmentById(layoutId)
    if (attachedFragment != null) {
        fragmentTransaction.remove(attachedFragment)
    }
    fragmentTransaction.add(layoutId, fragment).setCustomAnimations(R.anim.abc_popup_enter, R.anim.abc_popup_exit)
    try {
        fragmentTransaction.commit()
        return true
    } catch (exception: IllegalStateException) {
        Log.e("tcc", exception.message)
    }
    return false
}