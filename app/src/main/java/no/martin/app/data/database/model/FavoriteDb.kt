package no.martin.app.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteDb(
    @PrimaryKey
    val id: String
)
