@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.card.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.card.mock.CardRarity
import com.hdb.tourfolio.feature.explore.model.RegionType
import com.hdb.tourfolio.feature.explore.model.ThemeType
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural70
import com.hdb.tourfolio.ui.theme.Primary

data class CardFilterState(
    val region: RegionType? = null,
    val theme: ThemeType? = null,
    val rarity: CardRarity? = null,
) {
    val isAllSelected: Boolean
        get() =
            region == null &&
                theme == null &&
                rarity == null
}

@Composable
fun CardFilterBar(
    filterState: CardFilterState,
    onAllClick: () -> Unit,
    onRegionSelected: (RegionType) -> Unit,
    onThemeSelected: (ThemeType) -> Unit,
    onRaritySelected: (CardRarity) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .horizontalScroll(
                    rememberScrollState(),
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AllFilterButton(
            selected = filterState.isAllSelected,
            onClick = onAllClick,
        )

        FilterDropdownButton(
            defaultText = "지역별",
            selectedText =
                filterState.region
                    ?.displayName,
            options = RegionType.entries,
            optionText = { region ->
                region.displayName
            },
            onOptionSelected = onRegionSelected,
        )

        FilterDropdownButton(
            defaultText = "테마별",
            selectedText =
                filterState.theme
                    ?.displayName,
            options = ThemeType.entries,
            optionText = { theme ->
                theme.displayName
            },
            onOptionSelected = onThemeSelected,
        )

        FilterDropdownButton(
            defaultText = "희귀도",
            selectedText =
                filterState.rarity
                    ?.displayName,
            options = CardRarity.entries,
            optionText = { rarity ->
                rarity.displayName
            },
            onOptionSelected = onRaritySelected,
        )
    }
}

@Composable
private fun AllFilterButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor =
        if (selected) {
            Primary
        } else {
            Natural70
        }

    val textColor =
        if (selected) {
            Primary
        } else {
            Natural60
        }

    Box(
        modifier =
            modifier
                .height(31.dp)
                .border(
                    width = 1.2.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(24.dp),
                )
                .clickable(onClick = onClick)
                .padding(
                    horizontal = 15.dp,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "전체",
            style =
                LocalAppTypography.current.labelLarge.bold.copy(
                    color = textColor,
                ),
        )
    }
}

@Composable
private fun <T> FilterDropdownButton(
    defaultText: String,
    selectedText: String?,
    options: List<T>,
    optionText: (T) -> String,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    var anchorWidthPx by remember {
        mutableIntStateOf(0)
    }

    val density =
        LocalDensity.current

    val isSelected =
        selectedText != null

    val borderColor =
        if (isSelected) {
            Primary
        } else {
            Natural70
        }

    val contentColor =
        if (isSelected) {
            Primary
        } else {
            Natural60
        }

    Box(
        modifier =
            modifier
                .onSizeChanged { size ->
                    anchorWidthPx = size.width
                },
    ) {
        Row(
            modifier =
                Modifier
                    .height(31.dp)
                    .border(
                        width = 1.2.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(24.dp),
                    )
                    .clickable {
                        expanded = true
                    }
                    .padding(
                        horizontal = 15.dp,
                    ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = selectedText ?: defaultText,
                style =
                    LocalAppTypography.current.labelLarge.bold.copy(
                        color = contentColor,
                    ),
            )

            Spacer(
                modifier = Modifier.width(5.dp),
            )

            Image(
                painter =
                    painterResource(
                        id =
                            if (isSelected) {
                                R.drawable.ic_arrow_left_primary
                            } else {
                                R.drawable.ic_arrow_down_gray
                            },
                    ),
                contentDescription = "$defaultText 열기",
                modifier =
                    Modifier
                        .size(10.dp)
                        .rotate(
                            if (isSelected) {
                                90f +
                                    if (expanded) {
                                        180f
                                    } else {
                                        0f
                                    }
                            } else {
                                if (expanded) {
                                    180f
                                } else {
                                    0f
                                }
                            },
                        ),
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier =
                Modifier
                    .then(
                        if (anchorWidthPx > 0) {
                            Modifier.width(
                                with(density) {
                                    anchorWidthPx
                                        .toDp()
                                        .coerceAtLeast(80.dp)
                                },
                            )
                        } else {
                            Modifier.width(80.dp)
                        },
                    )
                    .heightIn(
                        max = 250.dp,
                    ),
            containerColor = Natural100,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 8.dp,
        ) {
            options.forEach { option ->
                val text =
                    optionText(option)

                val optionSelected =
                    selectedText == text

                DropdownMenuItem(
                    text = {
                        Text(
                            text = text,
                            style =
                                LocalAppTypography.current.bodySmall.bold.copy(
                                    color =
                                        if (optionSelected) {
                                            Primary
                                        } else {
                                            Natural70
                                        },
                                ),
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    contentPadding =
                        androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 12.dp,
                            vertical = 4.dp,
                        ),
                )
            }
        }
    }
}
