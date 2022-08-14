package br.com.vieirateam.tcc

import android.app.Application
import android.util.Log

class TCCApplication : Application() {

    private val TAG = "TCCApplication"

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }

    companion object {
        private var appInstance: TCCApplication? = null

        fun getInstance(): TCCApplication {
            if (appInstance == null) {
                throw IllegalArgumentException("Erro de configuração em AndroidManifest.xml")
            }
            return appInstance as TCCApplication
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d(TAG, "TCCApplication.onTerminate()")
    }
}