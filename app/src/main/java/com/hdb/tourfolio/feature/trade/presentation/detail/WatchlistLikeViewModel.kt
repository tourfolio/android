package com.hdb.tourfolio.feature.trade.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdb.tourfolio.core.network.WatchlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistLikeViewModel
    @Inject
    constructor(
        private val watchlistRepository: WatchlistRepository,
    ) : ViewModel() {
        private val _isLiked = MutableStateFlow(false)
        val isLiked: StateFlow<Boolean> = _isLiked.asStateFlow()

        fun toggleLike(spotId: Int) {
            val nextLiked = !_isLiked.value
            _isLiked.value = nextLiked

            viewModelScope.launch {
                try {
                    if (nextLiked) {
                        watchlistRepository.registerWatchlist(spotId = spotId)
                    } else {
                        watchlistRepository.deleteWatchlist(spotId = spotId)
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _isLiked.value = !nextLiked
                }
            }
        }
    }
