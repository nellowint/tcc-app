package br.com.vieirateam.tcc.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "category")
data class Category(
        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = "category_id")
        var id: Long = 0,
        @ColumnInfo(name = "category_name")
        var name: String = "")