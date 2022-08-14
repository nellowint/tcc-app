package br.com.vieirateam.tcc.dao

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import br.com.vieirateam.tcc.model.OfficeHour

@Dao
interface OfficeHourDAO {

    @Insert(onConflict = REPLACE)
    fun insert(officeHour: OfficeHour)

    @Query("DELETE FROM office_hour WHERE office_hour_id > 0")
    fun delete()

    @Query("SELECT * FROM office_hour WHERE office_hour_store == :store_id")
    fun select(store_id: Long): List<OfficeHour>
}