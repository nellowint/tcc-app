package br.com.vieirateam.tcc.database

import android.arch.persistence.room.Room
import br.com.vieirateam.tcc.TCCApplication
import br.com.vieirateam.tcc.dao.*

object DatabaseManager {

    private var database: Database = Room.databaseBuilder(
            TCCApplication.getInstance().applicationContext,
            Database::class.java,
            "tcc.db")
            .fallbackToDestructiveMigration()
            .addMigrations(DatabaseMigrate)
            .allowMainThreadQueries()
            .build()

    fun getShoppingDAO(): ShoppingDAO {
        return database.shoppingDAO()
    }

    fun getShoppingItemDAO(): ShoppingItemDAO {
        return database.shoppingItemDAO()
    }

    fun getStoreDAO(): StoreDAO {
        return database.storeDAO()
    }

    fun getEntertainmentDAO(): EntertainmentDAO {
        return database.ententertainmentDAO()
    }

    fun getNotificationDAO(): NotificationDAO {
        return database.notificationDAO()
    }

    fun getCategoryDAO(): CategoryDAO {
        return database.categoryDAO()
    }

    fun getUserDAO(): UserDAO {
        return database.userDAO()
    }

    fun getOfficeHourDAO(): OfficeHourDAO {
        return database.officeHourDAO()
    }
}