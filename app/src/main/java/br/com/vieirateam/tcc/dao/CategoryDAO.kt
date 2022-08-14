package br.com.vieirateam.tcc.dao

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import br.com.vieirateam.tcc.model.Category

@Dao
interface CategoryDAO {

    @Insert(onConflict = REPLACE)
    fun insert(category: Category)

    @Query("DELETE FROM category WHERE category_id > 0")
    fun delete()

    @Query("SELECT * FROM category ORDER BY category_id ASC")
    fun select(): List<Category>
}