package br.com.vieirateam.tcc.dao

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import br.com.vieirateam.tcc.model.User

@Dao
interface UserDAO {

    @Insert(onConflict = REPLACE)
    fun insert(user: User)

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)

    @Query("SELECT * FROM user WHERE user_email == :user_token")
    fun select(user_token: String): User

    @Query("SELECT 1 FROM user WHERE user_email == :user_token")
    fun selectToken(user_token: String): Boolean
}