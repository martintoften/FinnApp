package no.martin.app

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import no.martin.app.data.ads
import no.martin.app.data.database.AppDatabase
import no.martin.app.data.database.model.mapToDb
import no.martin.app.data.network.model.AdsNetwork
import no.martin.app.data.repository.FavoriteRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class FavoritesRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var favoriteRepository: FavoriteRepository
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        favoriteRepository = FavoriteRepository(
            favoriteDb = db.favoriteDao(),
            dispatcher = dispatcher
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun toggleFavorite() = scope.runTest {
        db.adDao().insert(AdsNetwork(ads).mapToDb())
        favoriteRepository.toggleFavorite(ads[0].id)
        val favoritesResult1 = db.favoriteDao().getAll()
        assertEquals(1, favoritesResult1.size)
        assertEquals(ads[0].id, favoritesResult1[0].id)
        favoriteRepository.toggleFavorite(ads[0].id)
        val favoritesResult2 = db.favoriteDao().getAll()
        assertEquals(0, favoritesResult2.size)
    }
}
