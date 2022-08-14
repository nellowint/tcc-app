package br.com.vieirateam.tcc.dao

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import br.com.vieirateam.tcc.model.Store

@Dao
interface StoreDAO {

    @Insert(onConflict = REPLACE)
    fun insert(store: Store)

    @Query("DELETE FROM store WHERE store_id > 0")
    fun delete()

    @Query("SELECT * FROM store ORDER BY store_id DESC")
    fun select(): List<Store>
}