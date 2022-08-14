package br.com.vieirateam.tcc.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "user")
data class User(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "user_id")
        var id: Long = 0,
        @ColumnInfo(name = "user_name")
        var name: String = "",
        @ColumnInfo(name = "user_email")
        var email: String = "") : Serializable