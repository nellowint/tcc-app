package br.com.vieirateam.tcc.model

import android.arch.persistence.room.*

@Entity(tableName = "shopping_item")
data class ShoppingItem(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "shopping_item_id")
        var id: Long = 0,
        @ColumnInfo(name = "shopping_item_quantity")
        var quantity: Long = 0,
        @ColumnInfo(name = "shopping_item_value")
        var value: Double = 0.0,

        @Embedded
        val product: Product,
        @Embedded
        val shopping: Shopping) {

    fun increaseQuantity() {
        this.quantity++
        this.value = this.product.value * this.quantity
    }

    fun updateQuantity(value: Long) {
        this.quantity += value
        this.value = this.product.value * this.quantity
    }

    fun decreaseQuantity(): Boolean {
        if (this.quantity > 1) {
            this.quantity--
            this.value = this.product.value * this.quantity
            return true
        }
        return false
    }
}

