package br.com.vieirateam.tcc.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Ignore
import java.io.Serializable

data class Product(
        @ColumnInfo(name = "product_id")
        var id: Long = 0,
        @ColumnInfo(name = "product_name")
        var name: String = "",
        @ColumnInfo(name = "product_value")
        var value: Double = 0.0,
        @ColumnInfo(name = "product_category")
        var category_name: String = "",
        @ColumnInfo(name = "product_measure")
        var measure_name: String = "",
        @ColumnInfo(name = "product_image")
        var image: String = "",

        @Ignore
        var selected: Boolean = false,
        @Ignore
        var quantity: Int = 1,
        @Ignore
        var offer: Boolean = false,
        @Ignore
        var validate: String? = null,
        @Ignore
        var store_name: List<String> = listOf()) : Serializable {

    fun increaseQuantity() {
        quantity++
    }

    fun decreaseQuantity(): Boolean {
        if (quantity > 1) {
            quantity--
            return true
        }
        return false
    }
}
