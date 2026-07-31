@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.explore.model.RegionType
import com.hdb.tourfolio.feature.explore.model.TagType
import com.hdb.tourfolio.feature.explore.model.ThemeType
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Primary

@Composable
fun SearchFilterBar(
    selectedTags: Set<TagType>,
    selectedThemes: Set<ThemeType>,
    selectedRegions: Set<RegionType>,
    onTagClick: () -> Unit,
    onThemeClick: () -> Unit,
    onRegionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SearchFilterButton(
            text =
                createFilterLabel(
                    defaultText = "태그별",
                    selectedTexts =
                        TagType.entries
                            .filter { it in selectedTags }
                            .map { it.displayName },
                ),
            selected = selectedTags.isNotEmpty(),
            onClick = onTagClick,
        )

        SearchFilterButton(
            text =
                createFilterLabel(
                    defaultText = "테마별",
                    selectedTexts =
                        ThemeType.entries
                            .filter { it in selectedThemes }
                            .map { it.displayName },
                ),
            selected = selectedThemes.isNotEmpty(),
            onClick = onThemeClick,
        )

        SearchFilterButton(
            text =
                createFilterLabel(
                    defaultText = "지역별",
                    selectedTexts =
                        RegionType.entries
                            .filter { it in selectedRegions }
                            .map { it.displayName },
                ),
            selected = selectedRegions.isNotEmpty(),
            onClick = onRegionClick,
        )
    }
}

private fun createFilterLabel(
    defaultText: String,
    selectedTexts: List<String>,
): String =
    when (selectedTexts.size) {
        0 -> defaultText
        1 -> selectedTexts.first()
        else -> "${selectedTexts.first()} 외 ${selectedTexts.size - 1}"
    }

@Composable
private fun SearchFilterButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor =
        if (selected) {
            Primary
        } else {
            Color(0xFF858585)
        }

    val borderColor =
        if (selected) {
            Primary
        } else {
            Color(0xFFB8B8B8)
        }

    Row(
        modifier =
            modifier
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(22.dp),
                )
                .clickable(onClick = onClick)
                .padding(
                    horizontal = 14.dp,
                    vertical = 10.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Text(
            text = text,
            style =
                LocalAppTypography.current.bodySmall.medium.copy(
                    color = contentColor,
                ),
        )

        Image(
            painter =
                painterResource(
                    id =
                        if (selected) {
                            R.drawable.ic_arrow_left_primary
                        } else {
                            R.drawable.ic_arrow_down_gray
                        },
                ),
            contentDescription = null,
            modifier =
                Modifier
                    .size(14.dp)
                    .rotate(if (selected) 90f else 0f),
        )
    }
}
