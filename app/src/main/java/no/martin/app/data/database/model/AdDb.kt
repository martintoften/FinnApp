package no.martin.app.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import no.martin.app.data.network.model.AdsNetwork

@Entity
data class AdDb(
    @PrimaryKey
    val id: String,
    val description: String?,
    val location: String?,
    val priceTotal: Int?,
    val priceValue: Int?,
    val image: String?
)

data class AdWithFavorite(
    val id: String,
    val description: String?,
    val location: String?,
    val priceTotal: Int?,
    val priceValue: Int?,
    val image: String?,
    val favoriteId: String?
)


fun AdsNetwork.mapToDb(): List<AdDb> {
    return items.map { ad ->
        AdDb(
            id = ad.id,
            description = ad.description,
            location = ad.location,
            image = ad.image?.url,
            priceTotal = ad.price?.total,
            priceValue = ad.price?.value
        )
    }
}
