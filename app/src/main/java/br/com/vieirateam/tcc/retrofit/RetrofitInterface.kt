package br.com.vieirateam.tcc.retrofit

import br.com.vieirateam.tcc.model.*
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {

    @GET("get/entertainment/?format=json")
    fun getEntertainments(): Call<List<Entertainment>>

    @GET("get/entertainment/{id}/?format=json")
    fun getEntertainmentId(@Path("id") id: Long): Call<Entertainment>

    @GET("get/entertainment/category/?format=json")
    fun getEntertainmentCategory(): Call<List<Category>>

    @GET("get/store/?format=json")
    fun getStores(): Call<List<Store>>

    @GET("get/office_hour/store/{id}/?format=json")
    fun getOfficeHour(@Path("id") id: Long): Call<List<OfficeHour>>

    @GET("get/product/?format=json")
    fun getProducts(): Call<List<Product>>

    @GET("get/product/offer/?format=json")
    fun getProductsOffer(): Call<List<Product>>

    @GET("get/product/{id}/?format=json")
    fun getProductsId(@Path("id") id: Long): Call<Product>

    @GET("get/category/?format=json")
    fun getCategories(): Call<List<Category>>

    @GET("get/question/?format=json")
    fun getQuestions(): Call<List<Question>>

    @GET("get/notification/{token}/{lastId}/?format=json")
    fun getNotifications(@Path("token") token: String, @Path("lastId") lastId: Long): Call<List<Notification>>

    @POST("post/feedback/")
    fun postFeedback(@Body feedback: Feedback): Call<Feedback>

    @POST("post/user/")
    fun postUser(@Body user: User): Call<User>

    @POST("post/history/search/")
    fun postSearchHistory(@Body searchHistory: SearchHistory): Call<SearchHistory>
}