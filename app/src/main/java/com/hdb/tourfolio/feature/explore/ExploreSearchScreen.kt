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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.explore.components.SearchAutocompleteContent
import com.hdb.tourfolio.feature.explore.components.SearchBar
import com.hdb.tourfolio.feature.explore.components.SearchBarStyle
import com.hdb.tourfolio.feature.explore.components.SearchFilterBar
import com.hdb.tourfolio.feature.explore.components.SearchFilterBottomSheet
import com.hdb.tourfolio.feature.explore.components.SearchFilterTab
import com.hdb.tourfolio.feature.explore.components.SearchHomeSections
import com.hdb.tourfolio.feature.explore.components.SearchResultCard
import com.hdb.tourfolio.feature.explore.components.createAutocompleteKeywords
import com.hdb.tourfolio.feature.explore.components.filterTourSpots
import com.hdb.tourfolio.feature.explore.mock.TourSpotListItemUiModel
import com.hdb.tourfolio.feature.explore.mock.TourSpotMockData
import com.hdb.tourfolio.feature.explore.model.RegionType
import com.hdb.tourfolio.feature.explore.model.TagType
import com.hdb.tourfolio.feature.explore.model.ThemeType
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural70
import com.hdb.tourfolio.ui.theme.TourfolioTheme

private enum class SearchPageMode {
    INITIAL,
    RESULT,
}

@Composable
fun ExploreSearchScreen(
    onBackClick: () -> Unit,
    onTourSpotClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable {
        mutableStateOf("")
    }

    var pageMode by rememberSaveable {
        mutableStateOf(SearchPageMode.INITIAL)
    }

    var isSearchFocused by remember {
        mutableStateOf(false)
    }

    var selectedTagNames by rememberSaveable {
        mutableStateOf(emptyList<String>())
    }

    var selectedThemeNames by rememberSaveable {
        mutableStateOf(emptyList<String>())
    }

    var selectedRegionNames by rememberSaveable {
        mutableStateOf(emptyList<String>())
    }

    var openedFilterTab by remember {
        mutableStateOf<SearchFilterTab?>(null)
    }

    val focusManager =
        LocalFocusManager.current

    val selectedTags =
        remember(selectedTagNames) {
            selectedTagNames
                .mapNotNull { name ->
                    TagType.entries.firstOrNull { type ->
                        type.name == name
                    }
                }
                .toSet()
        }

    val selectedThemes =
        remember(selectedThemeNames) {
            selectedThemeNames
                .mapNotNull { name ->
                    ThemeType.entries.firstOrNull { type ->
                        type.name == name
                    }
                }
                .toSet()
        }

    val selectedRegions =
        remember(selectedRegionNames) {
            selectedRegionNames
                .mapNotNull { name ->
                    RegionType.entries.firstOrNull { type ->
                        type.name == name
                    }
                }
                .toSet()
        }

    val recommendedTags =
        remember {
            listOf(
                TagType.BEACH,
                TagType.SEA,
                TagType.CULTURAL_HERITAGE,
                TagType.WALK,
                TagType.NIGHT_VIEW,
                TagType.VILLAGE,
                TagType.MOUNTAIN,
                TagType.PHOTO_SPOT,
            )
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
        remember {
            TourSpotMockData.listItems.filter { item ->
                item.id in
                    listOf(
                        2L,
                        1L,
                        4L,
                    )
            }
        }

    val hasAppliedFilters =
        selectedTags.isNotEmpty() ||
            selectedThemes.isNotEmpty() ||
            selectedRegions.isNotEmpty()

    val autocompleteKeywords =
        remember(query) {
            createAutocompleteKeywords(
                query = query,
                tourSpots = TourSpotMockData.listItems,
            )
        }

    val showAutocomplete =
        isSearchFocused &&
            query.isNotBlank() &&
            autocompleteKeywords.isNotEmpty()

    val searchResults =
        remember(
            query,
            pageMode,
            selectedTags,
            selectedThemes,
            selectedRegions,
        ) {
            if (pageMode != SearchPageMode.RESULT) {
                emptyList()
            } else if (hasAppliedFilters) {
                filterTourSpots(
                    items = TourSpotMockData.listItems,
                    selectedTags = selectedTags,
                    selectedThemes = selectedThemes,
                    selectedRegions = selectedRegions,
                )
            } else {
                searchTourSpots(
                    query = query,
                )
            }
        }

    fun clearAppliedFilters() {
        selectedTagNames = emptyList()
        selectedThemeNames = emptyList()
        selectedRegionNames = emptyList()
    }

    fun closeSearchInput() {
        isSearchFocused = false
        focusManager.clearFocus()
    }

    fun submitSearch() {
        val normalizedQuery =
            query.trim()

        if (normalizedQuery.isNotEmpty()) {
            query = normalizedQuery

            clearAppliedFilters()

            pageMode = SearchPageMode.RESULT

            closeSearchInput()
        }
    }

    fun selectAutocompleteKeyword(keyword: String) {
        query = keyword

        clearAppliedFilters()

        pageMode = SearchPageMode.RESULT

        closeSearchInput()
    }

    fun handleBack() {
        if (isSearchFocused) {
            closeSearchInput()
            return
        }

        if (pageMode == SearchPageMode.RESULT) {
            query = ""
            clearAppliedFilters()
            pageMode = SearchPageMode.INITIAL
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
                .background(Natural100),
    ) {
        SearchHeader(
            query = query,
            onQueryChange = { newQuery ->
                query = newQuery
            },
            isResultMode =
                pageMode == SearchPageMode.RESULT,
            onSearchFocusChanged = { focused ->
                isSearchFocused = focused
            },
            onBackClick = {
                handleBack()
            },
            onSearch = {
                submitSearch()
            },
        )

        if (showAutocomplete) {
            SearchAutocompleteContent(
                query = query,
                keywords = autocompleteKeywords,
                onKeywordClick = { keyword ->
                    selectAutocompleteKeyword(keyword)
                },
            )
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
                            recommendedTags = recommendedTags,
                            popularKeywords = popularKeywords,
                            recommendedSpots = recommendedSpots,
                            onTagClick = { tag ->
                                query = tag.displayName
                                submitSearch()
                            },
                            onKeywordClick = { keyword ->
                                query = keyword
                                submitSearch()
                            },
                            onSpotClick = onTourSpotClick,
                        )
                    }
                }

                SearchPageMode.RESULT -> {
                    SearchResultContent(
                        results = searchResults,
                        selectedTags = selectedTags,
                        selectedThemes = selectedThemes,
                        selectedRegions = selectedRegions,
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
                        onTourSpotClick = onTourSpotClick,
                    )
                }
            }
        }
    }

    openedFilterTab?.let { initialTab ->
        SearchFilterBottomSheet(
            initialTab = initialTab,
            appliedTags = selectedTags,
            appliedThemes = selectedThemes,
            appliedRegions = selectedRegions,
            allTourSpots = TourSpotMockData.listItems,
            onDismissRequest = {
                openedFilterTab = null
            },
            onApply = { tags, themes, regions ->
                selectedTagNames =
                    TagType.entries
                        .filter { type ->
                            type in tags
                        }
                        .map { type ->
                            type.name
                        }

                selectedThemeNames =
                    ThemeType.entries
                        .filter { type ->
                            type in themes
                        }
                        .map { type ->
                            type.name
                        }

                selectedRegionNames =
                    RegionType.entries
                        .filter { type ->
                            type in regions
                        }
                        .map { type ->
                            type.name
                        }

                query = ""
                pageMode = SearchPageMode.RESULT
                openedFilterTab = null
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
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter =
                painterResource(
                    id = backIconRes,
                ),
            contentDescription = "뒤로 가기",
            modifier =
                Modifier
                    .size(26.dp)
                    .rotate(backIconRotation)
                    .clickable(onClick = onBackClick),
        )

        Spacer(
            modifier = Modifier.size(16.dp),
        )

        SearchBar(
            value = query,
            onValueChange = onQueryChange,
            style =
                if (isResultMode) {
                    SearchBarStyle.ACTIVE
                } else {
                    SearchBarStyle.DEFAULT
                },
            onSearch = onSearch,
            onFocusChanged = onSearchFocusChanged,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SearchResultContent(
    results: List<TourSpotListItemUiModel>,
    selectedTags: Set<TagType>,
    selectedThemes: Set<ThemeType>,
    selectedRegions: Set<RegionType>,
    onTagFilterClick: () -> Unit,
    onThemeFilterClick: () -> Unit,
    onRegionFilterClick: () -> Unit,
    onTourSpotClick: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding =
            PaddingValues(
                start = 22.dp,
                top = 0.dp,
                end = 22.dp,
                bottom = 12.dp,
            ),
        verticalArrangement =
            Arrangement.spacedBy(20.dp),
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

        if (results.isEmpty()) {
            item {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 200.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "검색 결과가 없습니다.",
                        style =
                            LocalAppTypography.current.bodyLarge.medium.copy(
                                color = Natural70,
                            ),
                    )
                }
            }
        } else {
            items(
                items = results,
                key = { item ->
                    item.id
                },
            ) { item ->
                SearchResultCard(
                    item = item,
                    onClick = onTourSpotClick,
                )
            }
        }
    }
}

private fun searchTourSpots(query: String): List<TourSpotListItemUiModel> {
    val keyword =
        query
            .trim()
            .removePrefix("#")

    if (keyword.isBlank()) {
        return emptyList()
    }

    return TourSpotMockData.listItems.filter { item ->
        val matchesTitle =
            item.title.contains(
                other = keyword,
                ignoreCase = true,
            )

        val matchesTag =
            item.tags.any { tag ->
                tag.displayName.contains(
                    other = keyword,
                    ignoreCase = true,
                )
            }

        matchesTitle || matchesTag
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
        ExploreSearchScreen(
            onBackClick = {},
            onTourSpotClick = {},
        )
    }
}
