@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.explore.mock.TourSpotListItemUiModel
import com.hdb.tourfolio.feature.explore.model.RegionType
import com.hdb.tourfolio.feature.explore.model.TagType
import com.hdb.tourfolio.feature.explore.model.ThemeType
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural70
import com.hdb.tourfolio.ui.theme.Natural90
import com.hdb.tourfolio.ui.theme.Primary

private data class FilterOption(
    val key: String,
    val displayName: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFilterBottomSheet(
    initialTab: SearchFilterTab,
    appliedTags: Set<TagType>,
    appliedThemes: Set<ThemeType>,
    appliedRegions: Set<RegionType>,
    allTourSpots: List<TourSpotListItemUiModel>,
    onDismissRequest: () -> Unit,
    onApply: (
        selectedTags: Set<TagType>,
        selectedThemes: Set<ThemeType>,
        selectedRegions: Set<RegionType>,
    ) -> Unit,
) {
    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )

    var selectedTab by remember(initialTab) {
        mutableStateOf(initialTab)
    }

    var temporaryTags by remember(appliedTags) {
        mutableStateOf(appliedTags)
    }

    var temporaryThemes by remember(appliedThemes) {
        mutableStateOf(appliedThemes)
    }

    var temporaryRegions by remember(appliedRegions) {
        mutableStateOf(appliedRegions)
    }

    /*
     * 세 필터 유형 중 하나라도 선택되어 있는지 확인합니다.
     * 아무것도 선택되지 않았다면 필터 결과를 전체 데이터로 처리하지 않고
     * 0개로 표시합니다.
     */
    val allTags =
        remember {
            TagType.entries.toSet()
        }

    val allThemes =
        remember {
            ThemeType.entries.toSet()
        }

    val allRegions =
        remember {
            RegionType.entries.toSet()
        }


    val hasAnySelection =
        temporaryTags.isNotEmpty() ||
                temporaryThemes.isNotEmpty() ||
                temporaryRegions.isNotEmpty()

    val allTagsSelected =
        temporaryTags.isNotEmpty() &&
                temporaryTags.containsAll(allTags)

    val allThemesSelected =
        temporaryThemes.isNotEmpty() &&
                temporaryThemes.containsAll(allThemes)

    val allRegionsSelected =
        temporaryRegions.isNotEmpty() &&
                temporaryRegions.containsAll(allRegions)

    val resultCount =
        remember(
            temporaryTags,
            temporaryThemes,
            temporaryRegions,
            allTourSpots,
        ) {
            if (!hasAnySelection) {
                0
            } else {
                filterTourSpots(
                    items = allTourSpots,
                    selectedTags =
                        if (allTagsSelected) {
                            emptySet()
                        } else {
                            temporaryTags
                        },
                    selectedThemes =
                        if (allThemesSelected) {
                            emptySet()
                        } else {
                            temporaryThemes
                        },
                    selectedRegions =
                        if (allRegionsSelected) {
                            emptySet()
                        } else {
                            temporaryRegions
                        },
                ).size
            }
        }

    val isApplyEnabled =
        hasAnySelection && resultCount > 0

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Natural100,
        dragHandle = {
            Box(
                modifier =
                    Modifier
                        .padding(
                            top = 14.dp,
                            bottom = 16.dp,
                        )
                        .size(
                            width = 72.dp,
                            height = 6.dp,
                        )
                        .background(
                            color = Color(0xFFD8D8D8),
                            shape = RoundedCornerShape(50),
                        ),
            )
        },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .navigationBarsPadding(),
        ) {
            Text(
                text = "필터",
                style =
                    LocalAppTypography.current.titleMedium.bold.copy(
                        color = Natural10,
                    ),
                modifier =
                    Modifier.padding(
                        horizontal = 22.dp,
                        vertical = 4.dp,
                    ),
            )

            Spacer(modifier = Modifier.height(12.dp))

            SearchFilterTabBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                },
            )

            when (selectedTab) {
                SearchFilterTab.TAG -> {
                    FilterOptionGrid(
                        options =
                            TagType.entries.map { type ->
                                FilterOption(
                                    key = type.name,
                                    displayName = type.displayName,
                                )
                            },
                        selectedKeys =
                            temporaryTags
                                .map { type ->
                                    type.name
                                }
                                .toSet(),
                        columns = 3,
                        onAllClick = {
                            temporaryTags =
                                if (temporaryTags.containsAll(allTags)) {
                                    emptySet()
                                } else {
                                    allTags
                                }
                        },
                        onOptionClick = { key ->
                            temporaryTags =
                                temporaryTags.toggle(
                                    TagType.valueOf(key),
                                )
                        },
                        modifier = Modifier.weight(1f),
                    )
                }

                SearchFilterTab.THEME -> {
                    FilterOptionGrid(
                        options =
                            ThemeType.entries.map { type ->
                                FilterOption(
                                    key = type.name,
                                    displayName = type.displayName,
                                )
                            },
                        selectedKeys =
                            temporaryThemes
                                .map { type ->
                                    type.name
                                }
                                .toSet(),
                        columns = 2,
                        onAllClick = {
                            temporaryThemes =
                                if (temporaryThemes.containsAll(allThemes)) {
                                    emptySet()
                                } else {
                                    allThemes
                                }
                        },
                        onOptionClick = { key ->
                            temporaryThemes =
                                temporaryThemes.toggle(
                                    ThemeType.valueOf(key),
                                )
                        },
                        modifier = Modifier.weight(1f),
                    )
                }

                SearchFilterTab.REGION -> {
                    FilterOptionGrid(
                        options =
                            RegionType.entries.map { type ->
                                FilterOption(
                                    key = type.name,
                                    displayName = type.displayName,
                                )
                            },
                        selectedKeys =
                            temporaryRegions
                                .map { type ->
                                    type.name
                                }
                                .toSet(),
                        columns = 3,
                        onAllClick = {
                            temporaryRegions =
                                if (temporaryRegions.containsAll(allRegions)) {
                                    emptySet()
                                } else {
                                    allRegions
                                }
                        },
                        onOptionClick = { key ->
                            temporaryRegions =
                                temporaryRegions.toggle(
                                    RegionType.valueOf(key),
                                )
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            HorizontalDivider(
                color = Natural90,
            )

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 22.dp,
                            vertical = 18.dp,
                        ),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(54.dp)
                            .border(
                                width = 1.dp,
                                color = Color(0xFFB8B8B8),
                                shape = RoundedCornerShape(10.dp),
                            )
                            .clickable(onClick = onDismissRequest),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "취소",
                        style =
                            LocalAppTypography.current.bodyLarge.bold.copy(
                                color = Natural70,
                            ),
                    )
                }

                Box(
                    modifier =
                        Modifier
                            .weight(2.7f)
                            .height(54.dp)
                            .background(
                                color =
                                    if (isApplyEnabled) {
                                        Primary
                                    } else {
                                        Natural90
                                    },
                                shape = RoundedCornerShape(10.dp),
                            )
                            .clickable(
                                enabled = isApplyEnabled,
                                onClick = {
                                    onApply(
                                        temporaryTags,
                                        temporaryThemes,
                                        temporaryRegions,
                                    )
                                },
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "${resultCount}개의 관광지 보기",
                        style =
                            LocalAppTypography.current.bodyLarge.bold.copy(
                                color =
                                    if (isApplyEnabled) {
                                        Natural100
                                    } else {
                                        Natural70
                                    },
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterOptionGrid(
    options: List<FilterOption>,
    selectedKeys: Set<String>,
    columns: Int,
    onAllClick: () -> Unit,
    onOptionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val displayOptions =
        listOf(
            FilterOption(
                key = FILTER_ALL_KEY,
                displayName = "전체",
            ),
        ) + options

    val optionKeys =
        remember(options) {
            options
                .map { option ->
                    option.key
                }
                .toSet()
        }

    val areAllSelected =
        optionKeys.isNotEmpty() &&
            selectedKeys.containsAll(optionKeys)

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier =
            modifier
                .fillMaxWidth(),
        contentPadding =
            PaddingValues(
                horizontal = 22.dp,
                vertical = 22.dp,
            ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        items(
            items = displayOptions,
            key = { option ->
                option.key
            },
        ) { option ->
            val isAllOption =
                option.key == FILTER_ALL_KEY

            val selected =
                if (isAllOption) {
                    areAllSelected
                } else {
                    option.key in selectedKeys
                }

            FilterCheckItem(
                text = option.displayName,
                selected = selected,
                onClick = {
                    if (isAllOption) {
                        onAllClick()
                    } else {
                        onOptionClick(option.key)
                    }
                },
            )
        }
    }
}

@Composable
private fun FilterCheckItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        if (selected) {
            Image(
                painter =
                    painterResource(
                        id = R.drawable.ic_checkbox,
                    ),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
            )
        } else {
            Box(
                modifier =
                    Modifier
                        .size(28.dp)
                        .border(
                            width = 1.2.dp,
                            color = Color(0xFFB8B8B8),
                            shape = RoundedCornerShape(5.dp),
                        ),
            )
        }

        Text(
            text = text,
            style =
                LocalAppTypography.current.bodyLarge.medium.copy(
                    color =
                        if (selected) {
                            Natural10
                        } else {
                            Natural70
                        },
                ),
        )
    }
}

private fun <T> Set<T>.toggle(item: T): Set<T> =
    if (item in this) {
        this - item
    } else {
        this + item
    }

fun filterTourSpots(
    items: List<TourSpotListItemUiModel>,
    selectedTags: Set<TagType>,
    selectedThemes: Set<ThemeType>,
    selectedRegions: Set<RegionType>,
): List<TourSpotListItemUiModel> =
    items.filter { item ->
        val matchesTags =
            selectedTags.isEmpty() ||
                item.tags.any { tag ->
                    tag in selectedTags
                }

        val matchesThemes =
            selectedThemes.isEmpty() ||
                item.themeType in selectedThemes

        val matchesRegions =
            selectedRegions.isEmpty() ||
                item.regionType in selectedRegions

        matchesTags &&
            matchesThemes &&
            matchesRegions
    }

private const val FILTER_ALL_KEY = "__all__"
