package br.com.vieirateam.tcc.dao

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import br.com.vieirateam.tcc.model.Notification

@Dao
interface NotificationDAO {

    @Insert(onConflict = REPLACE)
    fun insert(notification: Notification)

    @Update()
    fun update(notification: Notification)

    @Delete
    fun delete(notification: Notification)

    @Query("SELECT * FROM notification WHERE notification_token ==:user_token ORDER BY notification_id DESC")
    fun select(user_token: String): List<Notification>

    @Query("SELECT COUNT(notification_id) FROM notification WHERE notification_token ==:user_token AND notification_visualized == 0")
    fun selectCount(user_token: String): Int

    @Query("SELECT MAX(notification_id) FROM notification WHERE notification_token ==:user_token")
    fun selectLastId(user_token: String): Long
}