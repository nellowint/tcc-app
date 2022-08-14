package br.com.vieirateam.tcc.dao

import android.arch.persistence.room.*
import br.com.vieirateam.tcc.model.ShoppingItem

@Dao
interface ShoppingItemDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(shoppingItem: ShoppingItem)

    @Delete
    fun delete(shoppingItem: ShoppingItem)

    @Update
    fun update(shoppingItem: ShoppingItem)

    @Query("SELECT * FROM shopping_item WHERE shopping_id = :shopping_id")
    fun select(shopping_id: Long): List<ShoppingItem>

    @Query("SELECT * FROM shopping_item WHERE shopping_id = :shopping_id AND product_id = :product_id")
    fun select(shopping_id: Long, product_id: Long): ShoppingItem
}