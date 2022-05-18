package no.martin.app.data.repository

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import no.martin.app.data.database.dao.AdDao
import no.martin.app.data.database.model.mapToDb
import no.martin.app.view.model.mapToView
import no.martin.app.data.network.api.AdApi
import no.martin.app.view.model.Resource

class AdRepository(
    private val adApi: AdApi,
    private val adDb: AdDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun refreshAds() = withContext(dispatcher) {
        try {
            val result = adApi.getAds()
            adDb.insert(result.mapToDb())
            Resource.Success(result.mapToView())
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    fun getAds() = flow {
        val cachedAds = adDb.getAllAdsWithFavorites().firstOrNull()
        if (cachedAds != null && cachedAds.isNotEmpty()) {
            emit(Resource.Success(cachedAds.mapToView()))
        }
        try {
            val adResult = adApi.getAds()
            adDb.insert(adResult.mapToDb())
        } catch (e: Exception) {
            if (cachedAds == null || cachedAds.isEmpty()) {
                emit(Resource.Error(e))
            }
        }
        adDb.getAllAdsWithFavorites()
            .map { it.mapToView() }
            .catch { emit(Resource.Error(it)) }
            .collect { if (it.isNotEmpty()) emit(Resource.Success(it)) }
    }.flowOn(dispatcher)
}
