package com.hdb.tourfolio.feature.explore.presentation

import androidx.lifecycle.viewModelScope
import com.hdb.tourfolio.core.mvi.MviEffect
import com.hdb.tourfolio.core.mvi.MviIntent
import com.hdb.tourfolio.core.mvi.MviState
import com.hdb.tourfolio.core.mvi.MviViewModel
import com.hdb.tourfolio.domain.common.model.RegionType
import com.hdb.tourfolio.domain.common.model.TagType
import com.hdb.tourfolio.domain.common.model.ThemeType
import com.hdb.tourfolio.domain.explore.usecase.GetAutocompleteSuggestionsUseCase
import com.hdb.tourfolio.domain.explore.usecase.GetExploreHubUseCase
import com.hdb.tourfolio.domain.explore.usecase.PreviewFilterCountUseCase
import com.hdb.tourfolio.domain.explore.usecase.SearchExploreSpotsUseCase
import com.hdb.tourfolio.feature.explore.presentation.components.SearchFilterTab
import com.hdb.tourfolio.feature.explore.presentation.model.ExploreHubTrendingSpotUiModel
import com.hdb.tourfolio.feature.explore.presentation.model.ExploreSearchSpotUiModel
import com.hdb.tourfolio.feature.explore.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val AUTOCOMPLETE_DEBOUNCE_MS = 300L
private const val FILTER_PREVIEW_DEBOUNCE_MS = 250L

enum class SearchPageMode { INITIAL, RESULT }

sealed interface ExploreHubUiState {
    data object Idle : ExploreHubUiState

    data object Loading : ExploreHubUiState

    data class Success(
        val trendingSpots: List<ExploreHubTrendingSpotUiModel>,
    ) : ExploreHubUiState

    data class Error(
        val message: String,
    ) : ExploreHubUiState
}

sealed interface ExploreSearchResultUiState {
    data object Idle : ExploreSearchResultUiState

    data object Loading : ExploreSearchResultUiState

    data class Success(
        val spots: List<ExploreSearchSpotUiModel>,
        val totalCount: Int,
    ) : ExploreSearchResultUiState

    data class Error(
        val message: String,
    ) : ExploreSearchResultUiState
}

sealed interface ExploreAutocompleteUiState {
    data object Idle : ExploreAutocompleteUiState

    data object Loading : ExploreAutocompleteUiState

    data class Success(
        val keywords: List<String>,
    ) : ExploreAutocompleteUiState

    data object Error : ExploreAutocompleteUiState
}

sealed interface ExploreFilterPreviewUiState {
    data object Idle : ExploreFilterPreviewUiState

    data object Loading : ExploreFilterPreviewUiState

    data class Success(
        val totalCount: Int,
    ) : ExploreFilterPreviewUiState

    data object Error : ExploreFilterPreviewUiState
}

sealed interface ExploreSearchIntent : MviIntent {
    data object EnterScreen : ExploreSearchIntent

    data class QueryChanged(
        val query: String,
    ) : ExploreSearchIntent

    data class SearchFocusChanged(
        val focused: Boolean,
    ) : ExploreSearchIntent

    data object Submit : ExploreSearchIntent

    data class SearchByKeyword(
        val keyword: String,
    ) : ExploreSearchIntent

    data class SearchByTag(
        val tag: TagType,
    ) : ExploreSearchIntent

    data object Back : ExploreSearchIntent

    data class OpenFilterTab(
        val tab: SearchFilterTab,
    ) : ExploreSearchIntent

    data object DismissFilter : ExploreSearchIntent

    data class PreviewFilter(
        val tags: Set<TagType>,
        val themes: Set<ThemeType>,
        val regions: Set<RegionType>,
    ) : ExploreSearchIntent

    data class ApplyFilter(
        val tags: Set<TagType>,
        val themes: Set<ThemeType>,
        val regions: Set<RegionType>,
    ) : ExploreSearchIntent
}

data class ExploreSearchState(
    val query: String = "",
    val pageMode: SearchPageMode = SearchPageMode.INITIAL,
    val isSearchFocused: Boolean = false,
    val selectedTags: Set<TagType> = emptySet(),
    val selectedThemes: Set<ThemeType> = emptySet(),
    val selectedRegions: Set<RegionType> = emptySet(),
    val openedFilterTab: SearchFilterTab? = null,
    val hub: ExploreHubUiState = ExploreHubUiState.Idle,
    val search: ExploreSearchResultUiState = ExploreSearchResultUiState.Idle,
    val autocomplete: ExploreAutocompleteUiState = ExploreAutocompleteUiState.Idle,
    val filterPreview: ExploreFilterPreviewUiState = ExploreFilterPreviewUiState.Idle,
) : MviState

sealed interface ExploreSearchEffect : MviEffect {
    data object NavigateBack : ExploreSearchEffect
}

@HiltViewModel
class ExploreSearchViewModel
    @Inject
    constructor(
        private val getExploreHubUseCase: GetExploreHubUseCase,
        private val searchExploreSpotsUseCase: SearchExploreSpotsUseCase,
        private val getAutocompleteSuggestionsUseCase: GetAutocompleteSuggestionsUseCase,
        private val previewFilterCountUseCase: PreviewFilterCountUseCase,
    ) : MviViewModel<ExploreSearchIntent, ExploreSearchState, ExploreSearchEffect>(ExploreSearchState()) {
        private var autocompleteJob: Job? = null
        private var filterPreviewJob: Job? = null

        override suspend fun handleIntent(intent: ExploreSearchIntent) {
            when (intent) {
                ExploreSearchIntent.EnterScreen -> fetchHubIfNeeded()
                is ExploreSearchIntent.QueryChanged -> onQueryChanged(intent.query)
                is ExploreSearchIntent.SearchFocusChanged -> onSearchFocusChanged(intent.focused)
                ExploreSearchIntent.Submit -> submitSearch()
                is ExploreSearchIntent.SearchByKeyword -> searchByKeyword(intent.keyword)
                is ExploreSearchIntent.SearchByTag -> searchByTag(intent.tag)
                ExploreSearchIntent.Back -> handleBack()
                is ExploreSearchIntent.OpenFilterTab -> setState { copy(openedFilterTab = intent.tab) }
                ExploreSearchIntent.DismissFilter -> dismissFilter()
                is ExploreSearchIntent.PreviewFilter -> previewFilter(intent.tags, intent.themes, intent.regions)
                is ExploreSearchIntent.ApplyFilter -> applyFilter(intent.tags, intent.themes, intent.regions)
            }
        }

        private suspend fun fetchHubIfNeeded() {
            if (currentState.hub !is ExploreHubUiState.Idle) return

            setState { copy(hub = ExploreHubUiState.Loading) }

            val result =
                try {
                    ExploreHubUiState.Success(
                        trendingSpots = getExploreHubUseCase().trendingSpots.map { it.toUiModel() },
                    )
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    ExploreHubUiState.Error(message = e.message ?: "추천 관광지를 불러오지 못했습니다.")
                }

            setState { copy(hub = result) }
        }

        private fun onQueryChanged(query: String) {
            setState { copy(query = query) }
            refreshAutocomplete()
        }

        private fun onSearchFocusChanged(focused: Boolean) {
            setState { copy(isSearchFocused = focused) }
            refreshAutocomplete()
        }

        private fun refreshAutocomplete() {
            autocompleteJob?.cancel()

            val state = currentState
            if (!state.isSearchFocused || state.query.isBlank()) {
                setState { copy(autocomplete = ExploreAutocompleteUiState.Idle) }
                return
            }

            autocompleteJob =
                viewModelScope.launch {
                    delay(AUTOCOMPLETE_DEBOUNCE_MS)

                    setState { copy(autocomplete = ExploreAutocompleteUiState.Loading) }

                    val result =
                        try {
                            ExploreAutocompleteUiState.Success(
                                keywords = getAutocompleteSuggestionsUseCase(state.query),
                            )
                        } catch (e: CancellationException) {
                            throw e
                        } catch (_: Exception) {
                            ExploreAutocompleteUiState.Error
                        }

                    setState { copy(autocomplete = result) }
                }
        }

        private suspend fun submitSearch() {
            val normalizedQuery = currentState.query.trim()
            if (normalizedQuery.isBlank()) return
            searchByKeyword(normalizedQuery)
        }

        private suspend fun searchByKeyword(keyword: String) {
            autocompleteJob?.cancel()
            setState {
                copy(
                    query = keyword,
                    selectedTags = emptySet(),
                    selectedThemes = emptySet(),
                    selectedRegions = emptySet(),
                    pageMode = SearchPageMode.RESULT,
                    isSearchFocused = false,
                    autocomplete = ExploreAutocompleteUiState.Idle,
                )
            }
            performSearch(keyword = keyword)
        }

        private suspend fun searchByTag(tag: TagType) {
            autocompleteJob?.cancel()
            setState {
                copy(
                    query = tag.displayName,
                    selectedTags = setOf(tag),
                    selectedThemes = emptySet(),
                    selectedRegions = emptySet(),
                    pageMode = SearchPageMode.RESULT,
                    isSearchFocused = false,
                    autocomplete = ExploreAutocompleteUiState.Idle,
                )
            }
            performSearch(tags = setOf(tag))
        }

        private suspend fun performSearch(
            keyword: String? = null,
            tags: Set<TagType> = emptySet(),
            themes: Set<ThemeType> = emptySet(),
            regions: Set<RegionType> = emptySet(),
        ) {
            setState { copy(search = ExploreSearchResultUiState.Loading) }

            val result =
                try {
                    val response =
                        searchExploreSpotsUseCase(
                            keyword = keyword?.trim()?.removePrefix("#")?.takeIf { it.isNotBlank() },
                            tags = tags,
                            themes = themes,
                            regions = regions,
                        )
                    ExploreSearchResultUiState.Success(
                        spots = response.spots.map { it.toUiModel() },
                        totalCount = response.totalCount,
                    )
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    ExploreSearchResultUiState.Error(message = e.message ?: "검색 결과를 불러오지 못했습니다.")
                }

            setState { copy(search = result) }
        }

        private suspend fun handleBack() {
            val state = currentState

            if (state.isSearchFocused) {
                autocompleteJob?.cancel()
                setState { copy(isSearchFocused = false, autocomplete = ExploreAutocompleteUiState.Idle) }
                return
            }

            if (state.pageMode == SearchPageMode.RESULT) {
                autocompleteJob?.cancel()
                setState {
                    copy(
                        query = "",
                        selectedTags = emptySet(),
                        selectedThemes = emptySet(),
                        selectedRegions = emptySet(),
                        pageMode = SearchPageMode.INITIAL,
                        openedFilterTab = null,
                        search = ExploreSearchResultUiState.Idle,
                        autocomplete = ExploreAutocompleteUiState.Idle,
                    )
                }
                return
            }

            sendEffect(ExploreSearchEffect.NavigateBack)
        }

        private fun dismissFilter() {
            filterPreviewJob?.cancel()
            setState { copy(openedFilterTab = null, filterPreview = ExploreFilterPreviewUiState.Idle) }
        }

        private fun previewFilter(
            tags: Set<TagType>,
            themes: Set<ThemeType>,
            regions: Set<RegionType>,
        ) {
            filterPreviewJob?.cancel()

            if (tags.isEmpty() && themes.isEmpty() && regions.isEmpty()) {
                setState { copy(filterPreview = ExploreFilterPreviewUiState.Idle) }
                return
            }

            filterPreviewJob =
                viewModelScope.launch {
                    delay(FILTER_PREVIEW_DEBOUNCE_MS)

                    setState { copy(filterPreview = ExploreFilterPreviewUiState.Loading) }

                    val result =
                        try {
                            ExploreFilterPreviewUiState.Success(
                                totalCount = previewFilterCountUseCase(tags, themes, regions),
                            )
                        } catch (e: CancellationException) {
                            throw e
                        } catch (_: Exception) {
                            ExploreFilterPreviewUiState.Error
                        }

                    setState { copy(filterPreview = result) }
                }
        }

        private suspend fun applyFilter(
            tags: Set<TagType>,
            themes: Set<ThemeType>,
            regions: Set<RegionType>,
        ) {
            filterPreviewJob?.cancel()
            autocompleteJob?.cancel()
            setState {
                copy(
                    selectedTags = tags,
                    selectedThemes = themes,
                    selectedRegions = regions,
                    query = "",
                    pageMode = SearchPageMode.RESULT,
                    openedFilterTab = null,
                    isSearchFocused = false,
                    autocomplete = ExploreAutocompleteUiState.Idle,
                    filterPreview = ExploreFilterPreviewUiState.Idle,
                )
            }
            performSearch(tags = tags, themes = themes, regions = regions)
        }
    }
