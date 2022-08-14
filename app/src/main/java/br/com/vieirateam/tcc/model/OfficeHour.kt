package br.com.vieirateam.tcc.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "office_hour")
data class OfficeHour(
        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = "office_hour_id")
        var id: Long = 0,
        @ColumnInfo(name = "office_hour_weekday")
        var weekday: String = "",
        @ColumnInfo(name = "office_hour_date")
        var date: String? = null,
        @ColumnInfo(name = "office_hour_start")
        var hour_start: String = "",
        @ColumnInfo(name = "office_hour_final")
        var hour_final: String = "",
        @ColumnInfo(name = "office_hour_store")
        var store: Long = 0)