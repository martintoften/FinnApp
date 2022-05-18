package no.martin.app.view.model

import no.martin.app.data.database.model.AdWithFavorite
import no.martin.app.data.network.model.AdsNetwork
import no.martin.app.view.helpers.CurrencyHelper

private const val baseImageUrl = "https://images.finncdn.no/dynamic/480x360c/"

data class Ad(
    val id: String,
    val description: String?,
    val location: String?,
    val priceTotal: String?,
    val priceValue: String?,
    val image: String?,
    val isFavored: Boolean
)

fun List<AdWithFavorite>.mapToView(): List<Ad> {
    return map { ad ->
        Ad(
            id = ad.id,
            description = ad.description,
            location = ad.location,
            priceTotal = ad.priceTotal?.let { CurrencyHelper.formatValue(it) },
            priceValue = ad.priceValue?.let { CurrencyHelper.formatValue(it)  },
            image = ad.image?.let { baseImageUrl + it },
            isFavored = ad.favoriteId != null
        )
    }
}

fun AdsNetwork.mapToView(): List<Ad> {
    return items.map { ad ->
        Ad(
            id = ad.id,
            description = ad.description,
            location = ad.location,
            priceTotal = ad.price?.total?.let { CurrencyHelper.formatValue(it) },
            priceValue = ad.price?.value?.let { CurrencyHelper.formatValue(it) },
            image = ad.image?.let { baseImageUrl + it },
            isFavored = false
        )
    }
}
