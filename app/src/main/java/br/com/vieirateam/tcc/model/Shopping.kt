package br.com.vieirateam.tcc.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "shopping")
data class Shopping(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "shopping_id")
        var id: Long = 0,
        @ColumnInfo(name = "shopping_name")
        var name: String = "",
        @ColumnInfo(name = "shopping_value")
        var value: Double = 0.0,
        @ColumnInfo(name = "shopping_date")
        var date: String = "",
        @ColumnInfo(name = "shopping_user")
        val token: String) : Serializable {

    fun updateValue(value: Double) {
        this.value += value
    }
}