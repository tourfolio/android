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

/*
 * ExploreSearchScreen에서 처음 보여줄 추천 태그 8개입니다.
 *
 * 모두 실제 서버에서 사용하는 태그 값입니다.
 */
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
    /*
     * ---------------------------------------------------------
     * API State
     * ---------------------------------------------------------
     */

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

    /*
     * 검색 화면 최초 진입 시에만
     * 콘텐츠 허브 API를 호출합니다.
     */
    LaunchedEffect(Unit) {
        if (hubUiState is ExploreHubUiState.Idle) {
            viewModel.fetchHub()
        }
    }

    /*
     * ---------------------------------------------------------
     * 화면 상태
     * ---------------------------------------------------------
     */

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

    /*
     * Enum 자체를 rememberSaveable에 넣는 것보다
     * name 문자열로 보관합니다.
     */
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

    /*
     * ---------------------------------------------------------
     * 저장된 String → Enum
     * ---------------------------------------------------------
     */

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

    /*
     * ---------------------------------------------------------
     * 초기 화면 데이터
     * ---------------------------------------------------------
     */

    val recommendedTags =
        remember {
            RECOMMENDED_SEARCH_TAGS
        }

    /*
     * 인기 검색어는 아직 API 연동하지 않으므로
     * 기존 데이터를 그대로 유지합니다.
     */
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

    /*
     * 콘텐츠 허브 API의 trendingSpots를
     * 추천 관광지 영역에 연결합니다.
     */
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

    /*
     * ---------------------------------------------------------
     * 자동완성
     * ---------------------------------------------------------
     *
     * query가 변경되고 검색창에 focus가 있는 경우
     * ViewModel에서 search API 기반 자동완성을 요청합니다.
     */
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

    /*
     * 검색창 입력 중에는 자동완성 화면 영역을 사용합니다.
     *
     * 결과가 0개더라도 입력 중에는 INITIAL 화면으로
     * 갑자기 돌아가지 않도록 query/focus 기준으로 판단합니다.
     */
    val showAutocompleteArea =
        isSearchFocused &&
            query.isNotBlank()

    /*
     * ---------------------------------------------------------
     * 실제 검색 결과
     * ---------------------------------------------------------
     */

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

    /*
     * ---------------------------------------------------------
     * 공통 함수
     * ---------------------------------------------------------
     */

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

    /*
     * 직접 검색어를 입력하고 키보드 Search 또는
     * 검색 아이콘을 눌렀을 때 호출합니다.
     *
     * 새 검색어 검색 시 기존 필터는 제거합니다.
     */
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

    /*
     * 자동완성 항목 클릭
     */
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

    /*
     * 뒤로가기
     */
    fun handleBack() {
        /*
         * 검색 입력 중이면 먼저 키보드/자동완성만 닫습니다.
         */
        if (isSearchFocused) {
            closeSearchInput()
            return
        }

        /*
         * 결과 화면이면 초기 검색 화면으로 돌아갑니다.
         */
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

    /*
     * ---------------------------------------------------------
     * Screen
     * ---------------------------------------------------------
     */

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

        /*
         * ---------------------------------------------------------
         * 자동완성 영역
         * ---------------------------------------------------------
         */
        if (showAutocompleteArea) {
            when (
                val state =
                    autocompleteUiState
            ) {
                ExploreAutocompleteUiState.Idle -> {
                    /*
                     * 입력 직후 debounce 전의 짧은 구간입니다.
                     */
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
            /*
             * ---------------------------------------------------------
             * 초기 / 결과 화면
             * ---------------------------------------------------------
             */
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
                        /*
                         * Hub API가 실패하더라도
                         * 추천 태그 / 인기 검색어는 그대로 보여줍니다.
                         *
                         * 추천 관광지만 빈 목록이 됩니다.
                         */
                        SearchHomeSections(
                            recommendedTags =
                            recommendedTags,
                            popularKeywords =
                            popularKeywords,
                            recommendedSpots =
                            recommendedSpots,
                            /*
                             * 추천 태그 클릭은 keyword 검색이 아니라
                             * tags 필터로 서버에 요청합니다.
                             */
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
                            /*
                             * 인기 검색어는 keyword 검색
                             */
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
            /*
             * 서버 search API의 totalCount
             */
            resultCount =
            filterResultCount,
            /*
             * 체크박스 변경 후 count 조회 중인지
             */
            isResultCountLoading =
            isFilterResultCountLoading,
            /*
             * 바텀시트 내부 임시 선택값이 변경될 때마다
             * 검색 API를 이용해 결과 개수만 미리 조회
             */
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
