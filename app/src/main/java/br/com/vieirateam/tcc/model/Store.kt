package br.com.vieirateam.tcc.model

import android.arch.persistence.room.*
import java.io.Serializable

@Entity(tableName = "store")
data class Store(
        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = "store_id")
        var id: Long = 0,
        @ColumnInfo(name = "store_name")
        var name: String = "",
        @ColumnInfo(name = "store_address")
        var address: String = "",
        @ColumnInfo(name = "store_neighborhood")
        var neighborhood: String = "",
        @ColumnInfo(name = "store_city")
        var city: String = "",
        @ColumnInfo(name = "store_phone")
        var phone: String = "",
        @ColumnInfo(name = "store_email")
        var email: String? = null,
        @ColumnInfo(name = "store_latitude")
        var latitude: String = "",
        @ColumnInfo(name = "store_longitude")
        var longitude: String = "",
        @ColumnInfo(name = "store_image")
        var image: String = "") : Serializable