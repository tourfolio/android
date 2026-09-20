@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.ui.components.SpotImageOverlay
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.TourfolioTheme

@Composable
fun RegionCard(
    id: Long,
    title: String,
    regionName: String,
    imageUrl: String,
    hasImage: Boolean,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    SpotImageOverlay(
        hasImage = hasImage,
        model = imageUrl,
        contentDescription = title,
        modifier =
            modifier
                .width(
                    180.dp,
                )
                .clickable {
                    onClick(
                        id,
                    )
                },
    ) {
        Column(
            modifier =
                Modifier
                    .padding(
                        start = 18.dp,
                        end = 18.dp,
                        top = 12.dp,
                        bottom = 20.dp,
                    ),
        ) {
            Text(
                text =
                title,
                style =
                    LocalAppTypography
                        .current
                        .titleMedium
                        .bold
                        .copy(
                            color =
                            Natural100,
                        ),
                maxLines =
                1,
                overflow =
                    TextOverflow.Ellipsis,
            )

            Spacer(
                modifier =
                    Modifier.height(
                        5.dp,
                    ),
            )

            Text(
                text =
                regionName,
                style =
                    LocalAppTypography
                        .current
                        .bodyLarge
                        .medium
                        .copy(
                            color =
                                Natural100.copy(
                                    alpha = 0.85f,
                                ),
                        ),
                maxLines =
                1,
                overflow =
                    TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(
    name = "Region Card Preview",
    showBackground = true,
    widthDp = 220,
    heightDp = 290,
)
@Composable
private fun RegionCardPreview() {
    TourfolioTheme {
        RegionCard(
            hasImage = true,
            id = 1L,
            title = "경복궁",
            regionName = "서울",
            imageUrl = "",
            onClick = {},
            modifier =
                Modifier.padding(
                    20.dp,
                ),
        )
    }
}
