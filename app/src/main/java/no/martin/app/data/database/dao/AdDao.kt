package no.martin.app.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import no.martin.app.data.database.model.AdDb
import no.martin.app.data.database.model.AdWithFavorite

@Dao
interface AdDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ads: List<AdDb>)

    @Query(
        "SELECT addb.id, description, location, priceTotal, priceValue, image, favoritedb.id " +
        "AS favoriteId " +
        "FROM addb " +
        "LEFT JOIN favoritedb ON addb.id = favoritedb.id"
    )
    fun getAllAdsWithFavorites(): Flow<List<AdWithFavorite>>
}
