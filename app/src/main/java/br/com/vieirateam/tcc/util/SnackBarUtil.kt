package br.com.vieirateam.tcc.util

import android.support.design.widget.Snackbar
import android.view.View
import android.widget.TextView
import br.com.vieirateam.tcc.R

object SnackBarUtil{

    fun show(view : View, message: String, duration: Int) : Snackbar{
        val snackBar = Snackbar.make(view, message, duration)
        snackBar.view.findViewById<TextView>(
                android.support.design.R.id.snackbar_text).textSize = 20F
        snackBar.view.setBackgroundResource(R.color.colorPrimary)
        snackBar.show()
        return snackBar
    }
}