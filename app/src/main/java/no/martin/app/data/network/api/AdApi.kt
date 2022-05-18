package no.martin.app.data.network.api

import no.martin.app.data.network.model.AdsNetwork
import retrofit2.http.GET

interface AdApi {
    @GET("baldermork/6a1bcc8f429dcdb8f9196e917e5138bd/raw/discover.json")
    suspend fun getAds(): AdsNetwork
}
