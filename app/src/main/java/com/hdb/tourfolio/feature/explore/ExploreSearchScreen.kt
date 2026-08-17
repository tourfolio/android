@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.hdb.tourfolio.feature.explore.components.SearchAutocompleteContent
import com.hdb.tourfolio.feature.explore.components.SearchBar
import com.hdb.tourfolio.feature.explore.components.SearchBarStyle
import com.hdb.tourfolio.feature.explore.components.SearchFilterBar
import com.hdb.tourfolio.feature.explore.components.SearchFilterBottomSheet
import com.hdb.tourfolio.feature.explore.components.SearchFilterTab
import com.hdb.tourfolio.feature.explore.components.SearchHomeSections
import com.hdb.tourfolio.feature.explore.components.SearchResultCard
import com.hdb.tourfolio.feature.explore.model.ExploreAutocompleteUiState
import com.hdb.tourfolio.feature.explore.model.ExploreFilterPreviewUiState
import com.hdb.tourfolio.feature.explore.model.ExploreHubUiState
import com.hdb.tourfolio.feature.explore.model.ExploreSearchSpotUiModel
import com.hdb.tourfolio.feature.explore.model.ExploreSearchUiState
import com.hdb.tourfolio.feature.explore.model.RegionType
import com.hdb.tourfolio.feature.explore.model.TagType
import com.hdb.tourfolio.feature.explore.model.ThemeType
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural70
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.TourfolioTheme

private enum class SearchPageMode {
    INITIAL,
    RESULT,
}

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

@Composable
fun ExploreSearchScreen(
    onBackClick: () -> Unit,
    onTourSpotClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExploreViewModel = hiltViewModel(),
) {
    val hubUiState by
        viewModel.hubUiState
            .collectAsStateWithLifecycle()

    val searchUiState by
        viewModel.searchUiState
            .collectAsStateWithLifecycle()

    val autocompleteUiState by
        viewModel.autocompleteUiState
            .collectAsStateWithLifecycle()

    val filterPreviewUiState by
        viewModel.filterPreviewUiState
            .collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (hubUiState is ExploreHubUiState.Idle) {
            viewModel.fetchHub()
        }
    }

    var query by
        rememberSaveable {
            mutableStateOf("")
        }

    var pageMode by
        rememberSaveable {
            mutableStateOf(
                SearchPageMode.INITIAL,
            )
        }

    var isSearchFocused by
        remember {
            mutableStateOf(false)
        }

    var selectedTagNames by
        rememberSaveable {
            mutableStateOf(
                emptyList<String>(),
            )
        }

    var selectedThemeNames by
        rememberSaveable {
            mutableStateOf(
                emptyList<String>(),
            )
        }

    var selectedRegionNames by
        rememberSaveable {
            mutableStateOf(
                emptyList<String>(),
            )
        }

    var openedFilterTab by
        remember {
            mutableStateOf<SearchFilterTab?>(
                null,
            )
        }

    val focusManager =
        LocalFocusManager.current

    val selectedTags =
        remember(
            selectedTagNames,
        ) {
            selectedTagNames
                .mapNotNull { name ->
                    TagType.entries
                        .firstOrNull { type ->
                            type.name == name
                        }
                }
                .toSet()
        }

    val selectedThemes =
        remember(
            selectedThemeNames,
        ) {
            selectedThemeNames
                .mapNotNull { name ->
                    ThemeType.entries
                        .firstOrNull { type ->
                            type.name == name
                        }
                }
                .toSet()
        }

    val selectedRegions =
        remember(
            selectedRegionNames,
        ) {
            selectedRegionNames
                .mapNotNull { name ->
                    RegionType.entries
                        .firstOrNull { type ->
                            type.name == name
                        }
                }
                .toSet()
        }

    val recommendedTags =
        remember {
            RECOMMENDED_SEARCH_TAGS
        }

    val popularKeywords =
        remember {
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
        }

    val recommendedSpots =
        when (
            val state =
                hubUiState
        ) {
            is ExploreHubUiState.Success -> {
                state.trendingSpots
            }

            else -> {
                emptyList()
            }
        }

    LaunchedEffect(
        query,
        isSearchFocused,
    ) {
        if (
            isSearchFocused &&
            query.isNotBlank()
        ) {
            viewModel.requestAutocomplete(
                query = query,
            )
        } else {
            viewModel.clearAutocomplete()
        }
    }

    val autocompleteKeywords =
        when (
            val state =
                autocompleteUiState
        ) {
            is ExploreAutocompleteUiState.Success -> {
                state.keywords
            }

            else -> {
                emptyList()
            }
        }

    val showAutocompleteArea =
        isSearchFocused &&
            query.isNotBlank()

    val searchResults =
        when (
            val state =
                searchUiState
        ) {
            is ExploreSearchUiState.Success -> {
                state.spots
            }

            else -> {
                emptyList()
            }
        }

    fun clearAppliedFilters() {
        selectedTagNames =
            emptyList()

        selectedThemeNames =
            emptyList()

        selectedRegionNames =
            emptyList()
    }

    fun closeSearchInput() {
        isSearchFocused =
            false

        focusManager.clearFocus()

        viewModel.clearAutocomplete()
    }

    fun submitSearch() {
        val normalizedQuery =
            query.trim()

        if (
            normalizedQuery.isBlank()
        ) {
            return
        }

        query =
            normalizedQuery

        clearAppliedFilters()

        pageMode =
            SearchPageMode.RESULT

        closeSearchInput()

        viewModel.searchSpots(
            keyword =
            normalizedQuery,
        )
    }

    fun selectAutocompleteKeyword(keyword: String) {
        query =
            keyword

        clearAppliedFilters()

        pageMode =
            SearchPageMode.RESULT

        closeSearchInput()

        viewModel.searchSpots(
            keyword =
            keyword,
        )
    }

    fun handleBack() {
        if (isSearchFocused) {
            closeSearchInput()
            return
        }

        if (
            pageMode ==
            SearchPageMode.RESULT
        ) {
            query =
                ""

            clearAppliedFilters()

            pageMode =
                SearchPageMode.INITIAL

            openedFilterTab =
                null

            viewModel.clearSearch()
            viewModel.clearAutocomplete()
        } else {
            onBackClick()
        }
    }

    BackHandler {
        handleBack()
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    Natural100,
                ),
    ) {
        SearchHeader(
            query =
            query,
            onQueryChange = { newQuery ->
                query =
                    newQuery
            },
            isResultMode =
                pageMode ==
                    SearchPageMode.RESULT,
            onSearchFocusChanged = { focused ->
                isSearchFocused =
                    focused
            },
            onBackClick = {
                handleBack()
            },
            onSearch = {
                submitSearch()
            },
        )

        if (showAutocompleteArea) {
            when (
                val state =
                    autocompleteUiState
            ) {
                ExploreAutocompleteUiState.Idle -> {
                }

                ExploreAutocompleteUiState.Loading -> {
                    SearchAutocompleteLoading()
                }

                is ExploreAutocompleteUiState.Success -> {
                    if (
                        autocompleteKeywords.isEmpty()
                    ) {
                        SearchAutocompleteEmpty()
                    } else {
                        SearchAutocompleteContent(
                            query =
                            query,
                            keywords =
                            autocompleteKeywords,
                            onKeywordClick = { keyword ->
                                selectAutocompleteKeyword(
                                    keyword,
                                )
                            },
                        )
                    }
                }

                ExploreAutocompleteUiState.Error -> {
                    SearchAutocompleteEmpty()
                }
            }
        } else {
            when (pageMode) {
                SearchPageMode.INITIAL -> {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .verticalScroll(
                                    rememberScrollState(),
                                )
                                .padding(
                                    start = 22.dp,
                                    top = 16.dp,
                                    end = 22.dp,
                                    bottom = 32.dp,
                                ),
                    ) {
                        SearchHomeSections(
                            recommendedTags =
                            recommendedTags,
                            popularKeywords =
                            popularKeywords,
                            recommendedSpots =
                            recommendedSpots,
                            onTagClick = { tag ->
                                query =
                                    tag.displayName

                                selectedTagNames =
                                    listOf(
                                        tag.name,
                                    )

                                selectedThemeNames =
                                    emptyList()

                                selectedRegionNames =
                                    emptyList()

                                pageMode =
                                    SearchPageMode.RESULT

                                closeSearchInput()

                                viewModel.searchSpots(
                                    tags =
                                        setOf(
                                            tag,
                                        ),
                                )
                            },
                            onKeywordClick = { keyword ->
                                query =
                                    keyword

                                clearAppliedFilters()

                                pageMode =
                                    SearchPageMode.RESULT

                                closeSearchInput()

                                viewModel.searchSpots(
                                    keyword =
                                    keyword,
                                )
                            },
                            onSpotClick =
                            onTourSpotClick,
                        )
                    }
                }

                SearchPageMode.RESULT -> {
                    when (
                        val state =
                            searchUiState
                    ) {
                        ExploreSearchUiState.Idle -> {
                            SearchResultContent(
                                results =
                                    emptyList(),
                                selectedTags =
                                selectedTags,
                                selectedThemes =
                                selectedThemes,
                                selectedRegions =
                                selectedRegions,
                                isLoading =
                                false,
                                errorMessage =
                                null,
                                onTagFilterClick = {
                                    openedFilterTab =
                                        SearchFilterTab.TAG
                                },
                                onThemeFilterClick = {
                                    openedFilterTab =
                                        SearchFilterTab.THEME
                                },
                                onRegionFilterClick = {
                                    openedFilterTab =
                                        SearchFilterTab.REGION
                                },
                                onTourSpotClick =
                                onTourSpotClick,
                            )
                        }

                        ExploreSearchUiState.Loading -> {
                            SearchResultContent(
                                results =
                                    emptyList(),
                                selectedTags =
                                selectedTags,
                                selectedThemes =
                                selectedThemes,
                                selectedRegions =
                                selectedRegions,
                                isLoading =
                                true,
                                errorMessage =
                                null,
                                onTagFilterClick = {
                                    openedFilterTab =
                                        SearchFilterTab.TAG
                                },
                                onThemeFilterClick = {
                                    openedFilterTab =
                                        SearchFilterTab.THEME
                                },
                                onRegionFilterClick = {
                                    openedFilterTab =
                                        SearchFilterTab.REGION
                                },
                                onTourSpotClick =
                                onTourSpotClick,
                            )
                        }

                        is ExploreSearchUiState.Success -> {
                            SearchResultContent(
                                results =
                                    state.spots,
                                selectedTags =
                                selectedTags,
                                selectedThemes =
                                selectedThemes,
                                selectedRegions =
                                selectedRegions,
                                isLoading =
                                false,
                                errorMessage =
                                null,
                                onTagFilterClick = {
                                    openedFilterTab =
                                        SearchFilterTab.TAG
                                },
                                onThemeFilterClick = {
                                    openedFilterTab =
                                        SearchFilterTab.THEME
                                },
                                onRegionFilterClick = {
                                    openedFilterTab =
                                        SearchFilterTab.REGION
                                },
                                onTourSpotClick =
                                onTourSpotClick,
                            )
                        }

                        is ExploreSearchUiState.Error -> {
                            SearchResultContent(
                                results =
                                    emptyList(),
                                selectedTags =
                                selectedTags,
                                selectedThemes =
                                selectedThemes,
                                selectedRegions =
                                selectedRegions,
                                isLoading =
                                false,
                                errorMessage =
                                    state.message,
                                onTagFilterClick = {
                                    openedFilterTab =
                                        SearchFilterTab.TAG
                                },
                                onThemeFilterClick = {
                                    openedFilterTab =
                                        SearchFilterTab.THEME
                                },
                                onRegionFilterClick = {
                                    openedFilterTab =
                                        SearchFilterTab.REGION
                                },
                                onTourSpotClick =
                                onTourSpotClick,
                            )
                        }
                    }
                }
            }
        }
    }

    val filterResultCount =
        when (
            val state =
                filterPreviewUiState
        ) {
            is ExploreFilterPreviewUiState.Success -> {
                state.totalCount
            }

            else -> {
                0
            }
        }

    val isFilterResultCountLoading =
        filterPreviewUiState is
            ExploreFilterPreviewUiState.Loading

    openedFilterTab?.let { initialTab ->
        SearchFilterBottomSheet(
            initialTab =
            initialTab,
            appliedTags =
            selectedTags,
            appliedThemes =
            selectedThemes,
            appliedRegions =
            selectedRegions,
            resultCount =
            filterResultCount,
            isResultCountLoading =
            isFilterResultCountLoading,
            onSelectionChanged = { tags, themes, regions ->
                viewModel.previewFilterCount(
                    tags =
                    tags,
                    themes =
                    themes,
                    regions =
                    regions,
                )
            },
            onDismissRequest = {
                openedFilterTab =
                    null

                viewModel.clearFilterPreview()
            },
            onApply = { tags, themes, regions ->
                selectedTagNames =
                    tags.map { type ->
                        type.name
                    }

                selectedThemeNames =
                    themes.map { type ->
                        type.name
                    }

                selectedRegionNames =
                    regions.map { type ->
                        type.name
                    }

                query =
                    ""

                pageMode =
                    SearchPageMode.RESULT

                openedFilterTab =
                    null

                closeSearchInput()

                viewModel.clearFilterPreview()

                viewModel.searchSpots(
                    keyword =
                    null,
                    tags =
                    tags,
                    themes =
                    themes,
                    regions =
                    regions,
                )
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
    val backIconRes =
        if (isResultMode) {
            R.drawable.ic_arrow_left_primary
        } else {
            R.drawable.ic_arrow_down_gray
        }

    val backIconRotation =
        if (isResultMode) {
            0f
        } else {
            90f
        }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    start = 14.dp,
                    top = 18.dp,
                    bottom = 18.dp,
                    end = 22.dp,
                ),
        verticalAlignment =
            Alignment.CenterVertically,
    ) {
        Image(
            painter =
                painterResource(
                    id = backIconRes,
                ),
            contentDescription =
                "뒤로 가기",
            modifier =
                Modifier
                    .size(
                        26.dp,
                    )
                    .rotate(
                        backIconRotation,
                    )
                    .clickable(
                        onClick =
                        onBackClick,
                    ),
        )

        Spacer(
            modifier =
                Modifier.size(
                    16.dp,
                ),
        )

        SearchBar(
            value =
            query,
            onValueChange =
            onQueryChange,
            style =
                if (isResultMode) {
                    SearchBarStyle.ACTIVE
                } else {
                    SearchBarStyle.DEFAULT
                },
            onSearch =
            onSearch,
            onFocusChanged =
            onSearchFocusChanged,
            modifier =
                Modifier.weight(
                    1f,
                ),
        )
    }
}

@Composable
private fun SearchAutocompleteLoading() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    top = 80.dp,
                ),
        contentAlignment =
            Alignment.TopCenter,
    ) {
        CircularProgressIndicator(
            color =
            Primary,
        )
    }
}

@Composable
private fun SearchAutocompleteEmpty() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    top = 120.dp,
                ),
        contentAlignment =
            Alignment.TopCenter,
    ) {
        Text(
            text =
                "일치하는 검색어가 없습니다.",
            style =
                LocalAppTypography
                    .current
                    .bodyLarge
                    .medium
                    .copy(
                        color =
                        Natural70,
                    ),
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
        modifier =
            Modifier.fillMaxSize(),
        contentPadding =
            PaddingValues(
                start = 22.dp,
                top = 0.dp,
                end = 22.dp,
                bottom = 12.dp,
            ),
        verticalArrangement =
            Arrangement.spacedBy(
                20.dp,
            ),
    ) {
        item {
            SearchFilterBar(
                selectedTags =
                selectedTags,
                selectedThemes =
                selectedThemes,
                selectedRegions =
                selectedRegions,
                onTagClick =
                onTagFilterClick,
                onThemeClick =
                onThemeFilterClick,
                onRegionClick =
                onRegionFilterClick,
            )
        }

        when {
            isLoading -> {
                item {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 160.dp,
                                ),
                        contentAlignment =
                            Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            color =
                            Primary,
                        )
                    }
                }
            }

            errorMessage != null -> {
                item {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 160.dp,
                                ),
                        contentAlignment =
                            Alignment.Center,
                    ) {
                        Text(
                            text =
                            errorMessage,
                            style =
                                LocalAppTypography
                                    .current
                                    .bodyLarge
                                    .medium
                                    .copy(
                                        color =
                                        Natural70,
                                    ),
                        )
                    }
                }
            }

            results.isEmpty() -> {
                item {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 200.dp,
                                ),
                        contentAlignment =
                            Alignment.Center,
                    ) {
                        Text(
                            text =
                                "검색 결과가 없습니다.",
                            style =
                                LocalAppTypography
                                    .current
                                    .bodyLarge
                                    .medium
                                    .copy(
                                        color =
                                        Natural70,
                                    ),
                        )
                    }
                }
            }

            else -> {
                items(
                    items =
                    results,
                    key = { item ->
                        item.id
                    },
                ) { item ->
                    SearchResultCard(
                        item =
                        item,
                        onClick =
                        onTourSpotClick,
                    )
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
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Natural100,
                    ),
        )
    }
}
