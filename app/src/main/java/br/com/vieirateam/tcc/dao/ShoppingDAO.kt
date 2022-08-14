package br.com.vieirateam.tcc.dao

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import br.com.vieirateam.tcc.model.Shopping

@Dao
interface ShoppingDAO{

    @Insert(onConflict = REPLACE)
    fun insert(shopping: Shopping)

    @Delete
    fun delete(shopping: Shopping)

    @Update
    fun update(shopping: Shopping)

    @Query("SELECT * FROM shopping WHERE shopping_user ==:user_token AND shopping_id ==:shopping_id")
    fun select(user_token: String, shopping_id: Long) : Shopping

    @Query("SELECT shopping_id FROM shopping ORDER BY shopping_id DESC LIMIT 1")
    fun selectId() : Long

    @Query("SELECT * FROM shopping WHERE shopping_user ==:user_token ORDER BY shopping_id DESC")
    fun select(user_token: String) : List<Shopping>

    @Query("SELECT 1 FROM shopping WHERE shopping_name == :shopping_name")
    fun selectName(shopping_name : String) : Boolean
}