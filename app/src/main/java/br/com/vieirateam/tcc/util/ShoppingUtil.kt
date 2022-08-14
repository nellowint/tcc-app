package br.com.vieirateam.tcc.util

import android.widget.ArrayAdapter
import br.com.vieirateam.tcc.database.DatabaseService
import br.com.vieirateam.tcc.model.Product
import br.com.vieirateam.tcc.model.Shopping
import br.com.vieirateam.tcc.model.ShoppingItem

object ShoppingUtil {

    fun addToCart(shoppingList: List<Shopping>, productList: List<Product>, stringAdapter: ArrayAdapter<String>, position: Int) {
        var selectedShopping: Shopping? = null
        for (shopping in shoppingList) {
            if (shopping.name == stringAdapter.getItem(position)) {
                selectedShopping = shopping
                break
            }
        }

        if (selectedShopping != null) {

            for (product in productList) {
                val shoppingItems = DatabaseService.selectShoppingItemList(selectedShopping.id)
                val item = DatabaseService.selectShoppingItem(selectedShopping.id, product.id)

                val value = product.quantity * product.value
                if (shoppingItems.contains(item)) {
                    item.updateQuantity(product.quantity.toLong())
                    DatabaseService.updateShoppingItem(item)
                    selectedShopping.updateValue(value)
                } else {
                    val shoppingItem = ShoppingItem(product = product, shopping = selectedShopping, value = value, quantity = product.quantity.toLong())
                    DatabaseService.insertShoppingItem(shoppingItem)
                    selectedShopping.updateValue(shoppingItem.value)
                }
            }
            DatabaseService.updateShopping(selectedShopping)
        }
    }
}