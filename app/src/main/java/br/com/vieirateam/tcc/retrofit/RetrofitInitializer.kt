package br.com.vieirateam.tcc.retrofit

import br.com.vieirateam.tcc.TCCApplication
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import br.com.vieirateam.tcc.R

class RetrofitInitializer {

    private val retrofit = Retrofit.Builder()

            .baseUrl(TCCApplication.getInstance().applicationContext.getString(R.string.app_server))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    fun retrofitService(): RetrofitInterface = retrofit.create(RetrofitInterface::class.java)
}