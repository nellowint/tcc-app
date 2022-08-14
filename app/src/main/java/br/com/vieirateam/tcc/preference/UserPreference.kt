package br.com.vieirateam.tcc.preference

import android.content.SharedPreferences
import br.com.vieirateam.tcc.TCCApplication

object UserPreference {

    private const val KEY_INTRO = "intro"
    private const val KEY_NOTIFY = "notification"
    private const val KEY_SHOPPING = "shopping"
    private const val KEY_PRODUCT = "product"
    private const val PREF_ID = "br.com.vieirateam.tcc"

    private fun getSharedPreferences(): SharedPreferences {
        val context = TCCApplication.getInstance().applicationContext
        return context.getSharedPreferences(PREF_ID, 0)
    }

    private fun setNotificationPreference(value: Boolean) {
        val preferences = getSharedPreferences()
        val editor = preferences.edit()
        editor.putBoolean(KEY_NOTIFY, value)
        editor.apply()
    }

    private fun getNotificationPreference(): Boolean {
        val preferences = getSharedPreferences()
        return preferences.getBoolean(KEY_NOTIFY, true)
    }

    private fun setIntroPreference(value: Boolean) {
        val preferences = getSharedPreferences()
        val editor = preferences.edit()
        editor.putBoolean(KEY_INTRO, value)
        editor.apply()
    }

    private fun getIntroPreference(): Boolean {
        val preferences = getSharedPreferences()
        return preferences.getBoolean(KEY_INTRO, false)
    }

    private fun setShoppingPreference(value: Boolean) {
        val preferences = getSharedPreferences()
        val editor = preferences.edit()
        editor.putBoolean(KEY_SHOPPING, value)
        editor.apply()
    }

    private fun getShoppingPreference(): Boolean {
        val preferences = getSharedPreferences()
        return preferences.getBoolean(KEY_SHOPPING, false)
    }

    private fun setProductPreference(value: Boolean) {
        val preferences = getSharedPreferences()
        val editor = preferences.edit()
        editor.putBoolean(KEY_PRODUCT, value)
        editor.apply()
    }

    private fun getProductPreference(): Boolean {
        val preferences = getSharedPreferences()
        return preferences.getBoolean(KEY_PRODUCT, false)
    }

    var intro
        get() = getIntroPreference()
        set(value) = setIntroPreference(value)

    var shopping
        get() = getShoppingPreference()
        set(value) = setShoppingPreference(value)

    var product
        get() = getProductPreference()
        set(value) = setProductPreference(value)

    var notification
        get() = getNotificationPreference()
        set(value) = setNotificationPreference(value)
}