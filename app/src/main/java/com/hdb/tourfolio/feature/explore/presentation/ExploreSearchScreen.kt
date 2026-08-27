@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdb.tourfolio.R
import com.hdb.tourfolio.domain.common.model.RegionType
import com.hdb.tourfolio.domain.common.model.TagType
import com.hdb.tourfolio.domain.common.model.ThemeType
import com.hdb.tourfolio.feature.explore.presentation.components.SearchAutocompleteContent
import com.hdb.tourfolio.feature.explore.presentation.components.SearchBar
import com.hdb.tourfolio.feature.explore.presentation.components.SearchBarStyle
import com.hdb.tourfolio.feature.explore.presentation.components.SearchFilterBar
import com.hdb.tourfolio.feature.explore.presentation.components.SearchFilterBottomSheet
import com.hdb.tourfolio.feature.explore.presentation.components.SearchFilterTab
import com.hdb.tourfolio.feature.explore.presentation.components.SearchHomeSections
import com.hdb.tourfolio.feature.explore.presentation.components.SearchResultCard
import com.hdb.tourfolio.feature.explore.presentation.model.ExploreSearchSpotUiModel
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural70
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.TourfolioTheme

private val RECOMMENDED_SEARCH_TAGS =
    listOf(
        TagType.HISTORY,
        TagType.PALACE,
        TagType.NATURE,
        TagType.SEA,
        TagType.NIGHT_VIEW,
        TagType.MOUNTAIN,
        TagType.HEALING,
        TagType.HANOK,
    )

private val POPULAR_SEARCH_KEYWORDS =
    listOf(
        "경복궁",
        "광안리",
        "첨성대",
        "한라산",
        "해동 용궁사",
        "성산일출봉",
        "남산타워",
        "해운대",
        "흰여울길",
        "감천 문화마을",
    )

@Composable
fun ExploreSearchScreen(
    onBackClick: () -> Unit,
    onTourSpotClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExploreSearchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.processIntent(ExploreSearchIntent.EnterScreen)
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ExploreSearchEffect.NavigateBack -> onBackClick()
            }
        }
    }

    LaunchedEffect(state.isSearchFocused) {
        if (!state.isSearchFocused) {
            focusManager.clearFocus()
        }
    }

    val recommendedSpots =
        when (val hub = state.hub) {
            is ExploreHubUiState.Success -> hub.trendingSpots
            else -> emptyList()
        }

    val autocompleteKeywords =
        when (val autocomplete = state.autocomplete) {
            is ExploreAutocompleteUiState.Success -> autocomplete.keywords
            else -> emptyList()
        }

    val showAutocompleteArea = state.isSearchFocused && state.query.isNotBlank()

    val filterResultCount =
        when (val filterPreview = state.filterPreview) {
            is ExploreFilterPreviewUiState.Success -> filterPreview.totalCount
            else -> 0
        }

    val isFilterResultCountLoading = state.filterPreview is ExploreFilterPreviewUiState.Loading

    BackHandler {
        viewModel.processIntent(ExploreSearchIntent.Back)
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Natural100),
    ) {
        SearchHeader(
            query = state.query,
            onQueryChange = { newQuery -> viewModel.processIntent(ExploreSearchIntent.QueryChanged(newQuery)) },
            isResultMode = state.pageMode == SearchPageMode.RESULT,
            onSearchFocusChanged = { focused ->
                viewModel.processIntent(ExploreSearchIntent.SearchFocusChanged(focused))
            },
            onBackClick = { viewModel.processIntent(ExploreSearchIntent.Back) },
            onSearch = { viewModel.processIntent(ExploreSearchIntent.Submit) },
        )

        if (showAutocompleteArea) {
            when (val autocomplete = state.autocomplete) {
                ExploreAutocompleteUiState.Idle -> {
                }

                ExploreAutocompleteUiState.Loading -> {
                    SearchAutocompleteLoading()
                }

                is ExploreAutocompleteUiState.Success -> {
                    if (autocompleteKeywords.isEmpty()) {
                        SearchAutocompleteEmpty()
                    } else {
                        SearchAutocompleteContent(
                            query = state.query,
                            keywords = autocompleteKeywords,
                            onKeywordClick = { keyword ->
                                viewModel.processIntent(ExploreSearchIntent.SearchByKeyword(keyword))
                            },
                        )
                    }
                }

                ExploreAutocompleteUiState.Error -> {
                    SearchAutocompleteEmpty()
                }
            }
        } else {
            when (state.pageMode) {
                SearchPageMode.INITIAL -> {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(start = 22.dp, top = 16.dp, end = 22.dp, bottom = 32.dp),
                    ) {
                        SearchHomeSections(
                            recommendedTags = RECOMMENDED_SEARCH_TAGS,
                            popularKeywords = POPULAR_SEARCH_KEYWORDS,
                            recommendedSpots = recommendedSpots,
                            onTagClick = { tag -> viewModel.processIntent(ExploreSearchIntent.SearchByTag(tag)) },
                            onKeywordClick = { keyword ->
                                viewModel.processIntent(ExploreSearchIntent.SearchByKeyword(keyword))
                            },
                            onSpotClick = onTourSpotClick,
                        )
                    }
                }

                SearchPageMode.RESULT -> {
                    val search = state.search
                    SearchResultContent(
                        results =
                            (search as? ExploreSearchResultUiState.Success)?.spots ?: emptyList(),
                        selectedTags = state.selectedTags,
                        selectedThemes = state.selectedThemes,
                        selectedRegions = state.selectedRegions,
                        isLoading = search is ExploreSearchResultUiState.Loading,
                        errorMessage = (search as? ExploreSearchResultUiState.Error)?.message,
                        onTagFilterClick = {
                            viewModel.processIntent(ExploreSearchIntent.OpenFilterTab(SearchFilterTab.TAG))
                        },
                        onThemeFilterClick = {
                            viewModel.processIntent(ExploreSearchIntent.OpenFilterTab(SearchFilterTab.THEME))
                        },
                        onRegionFilterClick = {
                            viewModel.processIntent(ExploreSearchIntent.OpenFilterTab(SearchFilterTab.REGION))
                        },
                        onTourSpotClick = onTourSpotClick,
                    )
                }
            }
        }
    }

    state.openedFilterTab?.let { initialTab ->
        SearchFilterBottomSheet(
            initialTab = initialTab,
            appliedTags = state.selectedTags,
            appliedThemes = state.selectedThemes,
            appliedRegions = state.selectedRegions,
            resultCount = filterResultCount,
            isResultCountLoading = isFilterResultCountLoading,
            onSelectionChanged = { tags, themes, regions ->
                viewModel.processIntent(ExploreSearchIntent.PreviewFilter(tags, themes, regions))
            },
            onDismissRequest = {
                viewModel.processIntent(ExploreSearchIntent.DismissFilter)
            },
            onApply = { tags, themes, regions ->
                viewModel.processIntent(ExploreSearchIntent.ApplyFilter(tags, themes, regions))
            },
        )
    }
}

@Composable
private fun SearchHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    isResultMode: Boolean,
    onSearchFocusChanged: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    onSearch: () -> Unit,
) {
    val backIconRes = if (isResultMode) R.drawable.ic_arrow_left_primary else R.drawable.ic_arrow_down_gray
    val backIconRotation = if (isResultMode) 0f else 90f

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, top = 18.dp, bottom = 18.dp, end = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = backIconRes),
            contentDescription = "뒤로 가기",
            modifier =
                Modifier
                    .size(26.dp)
                    .rotate(backIconRotation)
                    .clickable(onClick = onBackClick),
        )

        Spacer(modifier = Modifier.size(16.dp))

        SearchBar(
            value = query,
            onValueChange = onQueryChange,
            style = if (isResultMode) SearchBarStyle.ACTIVE else SearchBarStyle.DEFAULT,
            onSearch = onSearch,
            onFocusChanged = onSearchFocusChanged,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SearchAutocompleteLoading() {
    Box(
        modifier = Modifier.fillMaxSize().padding(top = 80.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        CircularProgressIndicator(color = Primary)
    }
}

@Composable
private fun SearchAutocompleteEmpty() {
    Box(
        modifier = Modifier.fillMaxSize().padding(top = 120.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Text(
            text = "일치하는 검색어가 없습니다.",
            style = LocalAppTypography.current.bodyLarge.medium.copy(color = Natural70),
        )
    }
}

@Composable
private fun SearchResultContent(
    results: List<ExploreSearchSpotUiModel>,
    selectedTags: Set<TagType>,
    selectedThemes: Set<ThemeType>,
    selectedRegions: Set<RegionType>,
    isLoading: Boolean,
    errorMessage: String?,
    onTagFilterClick: () -> Unit,
    onThemeFilterClick: () -> Unit,
    onRegionFilterClick: () -> Unit,
    onTourSpotClick: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 22.dp, top = 0.dp, end = 22.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            SearchFilterBar(
                selectedTags = selectedTags,
                selectedThemes = selectedThemes,
                selectedRegions = selectedRegions,
                onTagClick = onTagFilterClick,
                onThemeClick = onThemeFilterClick,
                onRegionClick = onRegionFilterClick,
            )
        }

        when {
            isLoading -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 160.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
            }

            errorMessage != null -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 160.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = errorMessage,
                            style = LocalAppTypography.current.bodyLarge.medium.copy(color = Natural70),
                        )
                    }
                }
            }

            results.isEmpty() -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 200.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "검색 결과가 없습니다.",
                            style = LocalAppTypography.current.bodyLarge.medium.copy(color = Natural70),
                        )
                    }
                }
            }

            else -> {
                items(items = results, key = { item -> item.id }) { item ->
                    SearchResultCard(item = item, onClick = onTourSpotClick)
                }
            }
        }
    }
}

@Preview(
    name = "Explore Search",
    showBackground = true,
    widthDp = 412,
    heightDp = 915,
)
@Composable
private fun ExploreSearchScreenPreview() {
    TourfolioTheme {
        Box(
            modifier = Modifier.fillMaxSize().background(Natural100),
        )
    }
}
