@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore

import android.R.attr.text
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.explore.components.SearchBar
import com.hdb.tourfolio.feature.explore.components.SearchBarStyle
import com.hdb.tourfolio.feature.explore.components.SearchFilterBar
import com.hdb.tourfolio.feature.explore.components.SearchFilterBottomSheet
import com.hdb.tourfolio.feature.explore.components.SearchFilterTab
import com.hdb.tourfolio.feature.explore.components.SearchHomeSections
import com.hdb.tourfolio.feature.explore.components.SearchResultCard
import com.hdb.tourfolio.feature.explore.components.filterTourSpots
import com.hdb.tourfolio.feature.explore.mock.TourSpotListItemUiModel
import com.hdb.tourfolio.feature.explore.mock.TourSpotMockData
import com.hdb.tourfolio.feature.explore.model.RegionType
import com.hdb.tourfolio.feature.explore.model.TagType
import com.hdb.tourfolio.feature.explore.model.ThemeType
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural70
import com.hdb.tourfolio.ui.theme.Natural90
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Primary
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

    /*
     * 검색창이 현재 입력 포커스를 가지고 있는지 나타냅니다.
     *
     * INITIAL과 RESULT 모두 이 값이 true이고 검색어가 존재하면
     * 자동완성 목록을 표시합니다.
     */
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
                item.id in listOf(
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

    /*
     * 관광지 이름 및 태그명을 기반으로 자동완성 후보를 만듭니다.
     */
    val autocompleteKeywords =
        remember(query) {
            createAutocompleteKeywords(
                query = query,
            )
        }

    /*
     * RESULT, 즉 ACTIVE 검색바 상태에서도
     * 검색창에 포커스가 있고 문자가 입력되어 있으면 자동완성을 표시합니다.
     */
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
                /*
                 * 필터가 적용된 경우 검색어 대신
                 * 전체 관광지 데이터에서 필터링합니다.
                 */
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

            /*
             * 새로운 검색어로 검색하면
             * 기존 필터 조건을 해제합니다.
             */
            clearAppliedFilters()

            pageMode = SearchPageMode.RESULT

            /*
             * 검색 실행 후 키보드와 자동완성 목록을 닫습니다.
             */
            closeSearchInput()
        }
    }

    fun selectAutocompleteKeyword(
        keyword: String,
    ) {
        /*
         * 선택한 자동완성 단어를 검색창에 넣습니다.
         */
        query = keyword

        clearAppliedFilters()

        /*
         * 선택한 단어로 검색 결과 화면을 표시합니다.
         */
        pageMode = SearchPageMode.RESULT

        closeSearchInput()
    }

    fun handleBack() {
        /*
         * 키보드 입력 중이라면 먼저 키보드와 자동완성 목록만 닫습니다.
         */
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

        /*
         * 자동완성 목록이 표시되는 동안에는
         * 홈 콘텐츠나 기존 검색 결과 대신 자동완성 목록을 표시합니다.
         */
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

                /*
                 * 필터 적용 시 기존 검색어를 제거합니다.
                 */
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

/**
 * 자동완성 목록 전체입니다.
 */
@Composable
private fun SearchAutocompleteContent(
    query: String,
    keywords: List<String>,
    onKeywordClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxSize(),
        contentPadding =
            PaddingValues(
                start = 22.dp,
                top = 0.dp,
                end = 22.dp,
                bottom = 32.dp,
            ),
    ) {
        items(
            items = keywords,
            key = { keyword ->
                keyword
            },
        ) { keyword ->
            SearchAutocompleteItem(
                query = query,
                keyword = keyword,
                onClick = {
                    onKeywordClick(keyword)
                },
            )

            HorizontalDivider(
                color = Natural90,
            )
        }
    }
}

/**
 * 자동완성 검색어 한 줄입니다.
 */
@Composable
private fun SearchAutocompleteItem(
    query: String,
    keyword: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(
                    vertical = 18.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter =
                painterResource(
                    id = R.drawable.ic_search_gray,
                ),
            contentDescription = null,
            modifier = Modifier.size(22.dp),
        )

        Spacer(
            modifier = Modifier.width(12.dp),
        )

        Text(
            /*
             * 사용자가 입력한 부분만 Primary 색상으로 표시합니다.
             */
            text =
                buildHighlightedKeyword(
                    keyword = keyword,
                    query = query,
                ),
            style =
                LocalAppTypography.current.bodyLarge.medium.copy(
                    color = Natural10,
                ),
        )
    }
}

/**
 * 자동완성 후보 중 사용자가 입력한 문자열과 일치하는 부분을
 * Primary 색상으로 강조합니다.
 *
 * 같은 문자열이 여러 번 등장하면 모든 일치 부분을 강조합니다.
 */
private fun buildHighlightedKeyword(
    keyword: String,
    query: String,
): AnnotatedString {
    val normalizedQuery =
        query
            .trim()
            .removePrefix("#")

    if (normalizedQuery.isBlank()) {
        return AnnotatedString(keyword)
    }

    return buildAnnotatedString {
        var currentIndex = 0

        while (currentIndex < keyword.length) {
            val matchedIndex =
                keyword.indexOf(
                    string = normalizedQuery,
                    startIndex = currentIndex,
                    ignoreCase = true,
                )

            if (matchedIndex < 0) {
                append(
                    keyword.substring(
                        startIndex = currentIndex,
                    ),
                )
                break
            }

            /*
             * 일치 문자열 앞부분은 기본 텍스트 색상으로 표시합니다.
             */
            append(
                keyword.substring(
                    startIndex = currentIndex,
                    endIndex = matchedIndex,
                ),
            )

            val matchedEndIndex =
                matchedIndex + normalizedQuery.length

            /*
             * 입력한 검색어와 일치하는 부분만 Primary로 표시합니다.
             */
            withStyle(
                style =
                    SpanStyle(
                        color = Primary,
                        fontWeight = FontWeight.Bold,
                    ),
            ) {
                append(
                    keyword.substring(
                        startIndex = matchedIndex,
                        endIndex = matchedEndIndex,
                    ),
                )
            }

            currentIndex = matchedEndIndex
        }
    }
}

/**
 * 자동완성 후보를 생성합니다.
 *
 * 현재는 관광지 제목과 관광지 태그명에서 검색합니다.
 */
private fun createAutocompleteKeywords(
    query: String,
): List<String> {
    val keyword =
        query
            .trim()
            .removePrefix("#")

    if (keyword.isBlank()) {
        return emptyList()
    }

    val tourSpotTitles =
        TourSpotMockData.listItems
            .map { item ->
                item.title
            }

    val tagNames =
        TourSpotMockData.listItems
            .flatMap { item ->
                item.tags
            }
            .map { tag ->
                tag.displayName
            }

    return (tourSpotTitles + tagNames)
        /*
         * 여러 관광지에서 같은 태그를 사용하므로
         * 중복 후보를 제거합니다.
         */
        .distinct()
        .filter { candidate ->
            candidate.contains(
                other = keyword,
                ignoreCase = true,
            )
        }
        /*
         * 검색어로 시작하는 후보를 먼저 보여주고,
         * 이후 검색어가 중간에 포함된 후보를 보여줍니다.
         */
        .sortedWith(
            compareBy<String> { candidate ->
                if (
                    candidate.startsWith(
                        prefix = keyword,
                        ignoreCase = true,
                    )
                ) {
                    0
                } else {
                    1
                }
            }.thenBy { candidate ->
                candidate.length
            }.thenBy { candidate ->
                candidate
            },
        )
        .take(AUTOCOMPLETE_LIMIT)
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

private fun searchTourSpots(
    query: String,
): List<TourSpotListItemUiModel> {
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

private const val AUTOCOMPLETE_LIMIT = 10

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