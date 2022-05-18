package no.martin.app.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.martin.app.data.database.dao.FavoriteDao
import no.martin.app.data.database.model.FavoriteDb

class FavoriteRepository(
    private val favoriteDb: FavoriteDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun toggleFavorite(adId: String) = withContext(dispatcher) {
        val isFavored = favoriteDb.get(adId) != null
        if (isFavored) favoriteDb.remove(adId)
        else favoriteDb.add(FavoriteDb(adId))
    }
}
