package no.martin.app.view.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import no.martin.app.data.database.database
import no.martin.app.view.model.Ad
import no.martin.app.view.model.Resource
import no.martin.app.data.network.createAdApi
import no.martin.app.data.repository.AdRepository
import no.martin.app.data.repository.FavoriteRepository

class AdViewModel(
    private val adRepository: AdRepository = AdRepository(
        adApi = createAdApi(),
        adDb = database.adDao()
    ),
    private val favoriteRepository: FavoriteRepository = FavoriteRepository(
        favoriteDb = database.favoriteDao()
    )
) : ViewModel() {

    private val _retryError = MutableStateFlow<Throwable?>(null)
    private val _filter = MutableStateFlow(Filter.NONE)
    val filter: StateFlow<Filter> = _filter

    val ads: Flow<Resource<List<Ad>>> = combine(
        adRepository.getAds().distinctUntilChanged(),
        _filter,
        _retryError
    ) { adsResult, filter, retryError ->
        when (adsResult) {
            is Resource.Success -> {
                when (filter) {
                    Filter.FAVORITES_ONLY -> Resource.Success(adsResult.value.filter { it.isFavored })
                    Filter.NONE -> Resource.Success(adsResult.value)
                }
            }
            is Resource.Error -> Resource.Error(retryError ?: adsResult.throwable)
            is Resource.Loading -> adsResult
        }
    }

    fun refreshAds() {
        viewModelScope.launch {
            when (val result = adRepository.refreshAds()) {
                is Resource.Success, Resource.Loading -> {}
                is Resource.Error -> _retryError.value = result.throwable
            }
        }
    }

    fun toggleFavorite(ad: Ad) {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(ad.id)
        }
    }

    fun toggleFavoriteFilter() {
        _filter.value = if (_filter.value == Filter.FAVORITES_ONLY) Filter.NONE else Filter.FAVORITES_ONLY
    }
}

enum class Filter {
    FAVORITES_ONLY,
    NONE
}
