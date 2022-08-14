package br.com.vieirateam.tcc.model

import android.arch.persistence.room.*
import java.io.Serializable

@Entity(tableName = "notification")
data class Notification(
        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = "notification_id")
        var id: Long = 0,
        @ColumnInfo(name = "notification_title")
        var title: String = "",
        @ColumnInfo(name = "notification_body")
        var body: String = "",
        @ColumnInfo(name = "notification_type")
        var type: String = "",
        @ColumnInfo(name = "notification_id_object")
        var id_object: Long = 0,
        @ColumnInfo(name = "notification_image")
        var image: String = "",
        @ColumnInfo(name = "notification_visualized")
        var visualized: Boolean = false,
        @ColumnInfo(name = "notification_token")
        var token: String = "") : Serializable