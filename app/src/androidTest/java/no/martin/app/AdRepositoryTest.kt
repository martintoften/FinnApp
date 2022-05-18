package no.martin.app

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import no.martin.app.data.ads
import no.martin.app.data.database.AppDatabase
import no.martin.app.data.database.model.FavoriteDb
import no.martin.app.data.database.model.mapToDb
import no.martin.app.data.moreAds
import no.martin.app.data.network.api.AdApi
import no.martin.app.data.network.model.AdsNetwork
import no.martin.app.data.repository.AdRepository
import no.martin.app.view.model.Ad
import no.martin.app.view.model.Resource
import no.martin.app.view.model.mapToView
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class AdRepositoryTest {

    private val adApi = mock<AdApi>()
    private lateinit var db: AppDatabase
    private lateinit var adRepository: AdRepository
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        adRepository = AdRepository(
            adApi = adApi,
            adDb = db.adDao(),
            dispatcher = dispatcher
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun refreshAdsWithNoAds() = scope.runTest {
        adApi.stub {
            onBlocking { getAds() } doReturn AdsNetwork(emptyList())
        }
        val result = adRepository.refreshAds()
        assertEquals(Resource.Success(listOf<Ad>()), result)
    }

    @Test
    fun refreshAdsWithTwoAds() = scope.runTest {
        adApi.stub {
            onBlocking { getAds() } doReturn AdsNetwork(ads)
        }
        val result = adRepository.refreshAds()
        assertEquals(Resource.Success(AdsNetwork(ads).mapToView()), result)
    }

    @Test
    fun refreshAdsWithError() = scope.runTest {
        adApi.stub {
            onBlocking { getAds() } doThrow IllegalStateException("Test")
        }
        val result = adRepository.refreshAds()
        assertTrue(result is Resource.Error)
    }

    @Test
    fun listenToAdsFlowWhenNoAds() = scope.runTest {
        adApi.stub {
            onBlocking { getAds() } doReturn AdsNetwork(emptyList())
        }
        try {
            withTimeout(1000) {
                adRepository.getAds().take(1).toList()[0]
            }
            fail("This should never time out")
        } catch (e: TimeoutCancellationException) {
            // Do nothing
        }
    }

    @Test
    fun listenToAdsFlowWhenTwoAds() = scope.runTest {
        adApi.stub {
            onBlocking { getAds() } doReturn AdsNetwork(ads)
        }
        val result = adRepository.getAds().take(1).toList()[0]
        assertEquals(Resource.Success(AdsNetwork(ads).mapToView()), result)
    }

    @Test
    fun listenToAdsFlowWhenTwoAdsAndAddingFour() = scope.runTest {
        adApi.stub {
            onBlocking { getAds() } doReturn AdsNetwork(ads)
        }
        adRepository.getAds().test {
            assertEquals(Resource.Success(AdsNetwork(ads).mapToView()), awaitItem())
            db.adDao().insert(AdsNetwork(moreAds).mapToDb())
            assertEquals(Resource.Success(AdsNetwork(moreAds).mapToView()), awaitItem())
        }
    }

    @Test
    fun listenToAdsFlowWhenError() = scope.runTest {
        adApi.stub {
            onBlocking { getAds() } doThrow IllegalStateException("Test")
        }
        val result = adRepository.getAds().take(1).toList()[0]
        assertTrue(result is Resource.Error)
    }

    @Test
    fun listenToAdsWhenTogglingFavorite() = scope.runTest {
        adApi.stub {
            onBlocking { getAds() } doReturn AdsNetwork(ads)
        }
        adRepository.getAds().test {
            assertEquals(Resource.Success(AdsNetwork(ads).mapToView()), awaitItem())

            db.favoriteDao().add(FavoriteDb(ads[0].id))
            val result2 = awaitItem() as Resource.Success
            assertEquals(true, result2.value[0].isFavored)
            assertEquals(false, result2.value[1].isFavored)

            db.favoriteDao().remove(ads[0].id)
            db.favoriteDao().add(FavoriteDb(ads[1].id))
            val result3 = awaitItem() as Resource.Success
            assertEquals(false, result3.value[0].isFavored)
            assertEquals(true, result3.value[1].isFavored)
        }
    }
}
