package br.com.vieirateam.tcc.retrofit

import br.com.vieirateam.tcc.model.*
import retrofit2.Call

class RetrofitWebService {

    fun getStores(success: (store: List<Store>) -> Unit,
                  failure: (throwable: Throwable) -> Unit) {

        val call = RetrofitInitializer().retrofitService().getStores()
        call.enqueue(callback({ response ->
            response?.body()?.let {
                success(it)
            }
        }, { throwable ->
            throwable?.let {
                failure(it)
            }
        }))
    }

    fun getOfficeHour(id: Long,
                      success: (officeHour: List<OfficeHour>) -> Unit,
                      failure: (throwable: Throwable) -> Unit) {

        val call = RetrofitInitializer().retrofitService().getOfficeHour(id)
        call.enqueue(callback({ response ->
            response?.body()?.let {
                success(it)
            }
        }, { throwable ->
            throwable?.let {
                failure(it)
            }
        }))
    }

    fun getProducts(offer: Boolean,
                    success: (product: List<Product>) -> Unit,
                    failure: (throwable: Throwable) -> Unit) {

        val call: Call<List<Product>> = if (offer) {
            RetrofitInitializer().retrofitService().getProductsOffer()
        } else {
            RetrofitInitializer().retrofitService().getProducts()
        }

        call.enqueue(callback({ response ->
            response?.body()?.let {
                success(it)
            }
        }, { throwable ->
            throwable?.let {
                failure(it)
            }
        }))
    }

    fun getProductId(id: Long,
                     success: (product: Product) -> Unit,
                     failure: (throwable: Throwable) -> Unit) {

        val call = RetrofitInitializer().retrofitService().getProductsId(id)
        call.enqueue(callback({ response ->
            response?.body()?.let {
                success(it)
            }
        }, { throwable ->
            throwable?.let {
                failure(it)
            }
        }))
    }

    fun getCategories(success: (category: List<Category>) -> Unit,
                      failure: (throwable: Throwable) -> Unit) {

        val call = RetrofitInitializer().retrofitService().getCategories()
        call.enqueue(callback({ response ->
            response?.body()?.let {
                success(it)
            }
        }, { throwable ->
            throwable?.let {
                failure(it)
            }
        }))
    }

    fun getEntertainments(success: (entertainment: List<Entertainment>) -> Unit,
                          failure: (throwable: Throwable) -> Unit) {

        val call = RetrofitInitializer().retrofitService().getEntertainments()
        call.enqueue(callback({ response ->
            response?.body()?.let {
                success(it)
            }
        }, { throwable ->
            throwable?.let {
                failure(it)
            }
        }))
    }

    fun getEntertainmentId(id: Long,
                           success: (entertainment: Entertainment) -> Unit,
                           failure: (throwable: Throwable) -> Unit) {

        val call = RetrofitInitializer().retrofitService().getEntertainmentId(id)
        call.enqueue(callback({ response ->
            response?.body()?.let {
                success(it)
            }
        }, { throwable ->
            throwable?.let {
                failure(it)
            }
        }))
    }

    fun getEntertainmentCategory(success: (category: List<Category>) -> Unit,
                                 failure: (throwable: Throwable) -> Unit) {

        val call = RetrofitInitializer().retrofitService().getEntertainmentCategory()
        call.enqueue(callback({ response ->
            response?.body()?.let {
                success(it)
            }
        }, { throwable ->
            throwable?.let {
                failure(it)
            }
        }))
    }

    fun getQuestions(success: (question: List<Question>) -> Unit,
                     failure: (throwable: Throwable) -> Unit) {

        val call = RetrofitInitializer().retrofitService().getQuestions()
        call.enqueue(callback({ response ->
            response?.body()?.let {
                success(it)
            }
        }, { throwable ->
            throwable?.let {
                failure(it)
            }
        }))
    }

    fun getNotifications(token: String,
                         lastId: Long,
                         success: (notification: List<Notification>) -> Unit,
                         failure: (throwable: Throwable) -> Unit) {

        val call = RetrofitInitializer().retrofitService().getNotifications(token, lastId)
        call.enqueue(callback({ response ->
            response?.body()?.let {
                success(it)
            }
        }, { throwable ->
            throwable?.let {
                failure(it)
            }
        }))
    }

    fun postUser(user: User,
                 success: (user: User) -> Unit,
                 failure: (throwable: Throwable) -> Unit) {
        val call = RetrofitInitializer().retrofitService().postUser(user)
        call.enqueue(callback({ response ->
            response?.body()?.let {
                success(it)
            }
        }, { throwable ->
            throwable?.let {
                failure(it)
            }
        }))
    }

    fun postFeedback(feedback: Feedback,
                     success: (feedback: Feedback) -> Unit,
                     failure: (throwable: Throwable) -> Unit) {
        val call = RetrofitInitializer().retrofitService().postFeedback(feedback)
        call.enqueue(callback({ response ->
            response?.body()?.let {
                success(it)
            }
        }, { throwable ->
            throwable?.let {
                failure(it)
            }
        }))
    }

    fun postSearch(searchHistory: SearchHistory,
                   success: (searchHistory: SearchHistory) -> Unit,
                   failure: (throwable: Throwable) -> Unit) {
        val call = RetrofitInitializer().retrofitService().postSearchHistory(searchHistory)
        call.enqueue(callback({ response ->
            response?.body()?.let {
                success(it)
            }
        }, { throwable ->
            throwable?.let {
                failure(it)
            }
        }))
    }
}