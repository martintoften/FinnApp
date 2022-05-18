package no.martin.app.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import no.martin.app.App
import no.martin.app.data.database.dao.AdDao
import no.martin.app.data.database.dao.FavoriteDao
import no.martin.app.data.database.model.AdDb
import no.martin.app.data.database.model.FavoriteDb

@Database(entities = [AdDb::class, FavoriteDb::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun adDao(): AdDao
    abstract fun favoriteDao(): FavoriteDao
}

val database by lazy {
    Room
        .databaseBuilder(
            App.applicationContext(),
            AppDatabase::class.java, "app.db"
        )
        .build()
}
