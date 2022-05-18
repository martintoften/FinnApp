package no.martin.app.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdsNetwork(
    val items: List<AdNetwork>
)

@Serializable
data class AdNetwork(
    @SerialName("ad-type")
    val adType: String,
    val id: String,
    val content: ContentNetwork?,
    val description: String?,
    val favourite: FavouriteNetwork?,
    val image: ImageNetwork?,
    val location: String?,
    val overlay: OverlayNetwork?,
    val price: PriceNetwork?,
    val score: Double?,
    val type: String?,
    val url: String?,
    val version: String?
)

@Serializable
data class FavouriteNetwork(
    val itemId: String,
    val itemType: String
)

@Serializable
data class PriceNetwork(
    val total: Int?,
    val value: Int
)

@Serializable
data class OverlayNetwork(
    val cms: CmsNetwork,
    val minColumns: Int
)

@Serializable
data class ImageNetwork(
    val height: Int?,
    val scalable: Boolean,
    val type: String,
    val url: String,
    val width: Int?
)

@Serializable
data class ContentNetwork(
    @SerialName("company_name")
    val companyName: String,
    val duration: String,
    val heading: String,
    @SerialName("job_title")
    val jobTitle: String,
    val published: String,
    @SerialName("published_relative")
    val publishedRelative: String
)

@Serializable
data class CmsNetwork(
    val label: String
)
