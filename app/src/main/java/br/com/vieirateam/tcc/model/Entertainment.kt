package br.com.vieirateam.tcc.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "entertainment")
data class Entertainment(
        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = "entertainment_id")
        var id: Long = 0,
        @ColumnInfo(name = "entertainment_name")
        var name: String = "",
        @ColumnInfo(name = "entertainment_desc")
        var desc: String = "",
        @ColumnInfo(name = "entertainment_sub_desc")
        var sub_desc: String = "",
        @ColumnInfo(name = "entertainment_image")
        var image: String = "",
        @ColumnInfo(name = "entertainment_category")
        var category: String = "") : Serializable