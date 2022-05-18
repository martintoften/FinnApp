package no.martin.app.data.database.dao

import androidx.room.*
import no.martin.app.data.database.model.FavoriteDb

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(ads: FavoriteDb)

    @Query("SELECT * FROM favoritedb WHERE id = :id")
    suspend fun get(id: String): FavoriteDb?

    @Query("SELECT * FROM favoritedb")
    suspend fun getAll(): List<FavoriteDb>

    @Query("DELETE FROM favoritedb WHERE id = :id")
    suspend fun remove(id: String)
}
