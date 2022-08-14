package br.com.vieirateam.tcc.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

object KeyboardUtil {

    fun showKeyboard(context: Context, editText: EditText) {
        editText.requestFocus()
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        editText.requestFocus()
    }

    fun hideKeyboard(context: Context, view: View) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}