package br.com.vieirateam.tcc.dao

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import br.com.vieirateam.tcc.model.Entertainment

@Dao
interface EntertainmentDAO {

    @Insert(onConflict = REPLACE)
    fun insert(entertainment: Entertainment)

    @Query("DELETE FROM entertainment WHERE entertainment_id > 0")
    fun delete()

    @Query("SELECT * FROM entertainment ORDER BY entertainment_id DESC")
    fun select(): List<Entertainment>
}