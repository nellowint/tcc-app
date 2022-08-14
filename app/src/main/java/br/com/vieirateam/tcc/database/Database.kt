package br.com.vieirateam.tcc.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import br.com.vieirateam.tcc.dao.*
import br.com.vieirateam.tcc.model.*

@Database(entities = [
    (Shopping::class),
    (ShoppingItem::class),
    (Store::class),
    (Entertainment::class),
    (Notification::class),
    (Category::class),
    (User::class),
    (OfficeHour::class)], version = 1, exportSchema = false)

abstract class Database : RoomDatabase() {
    abstract fun shoppingDAO(): ShoppingDAO
    abstract fun shoppingItemDAO(): ShoppingItemDAO
    abstract fun storeDAO(): StoreDAO
    abstract fun ententertainmentDAO(): EntertainmentDAO
    abstract fun notificationDAO(): NotificationDAO
    abstract fun categoryDAO(): CategoryDAO
    abstract fun userDAO(): UserDAO
    abstract fun officeHourDAO(): OfficeHourDAO
}