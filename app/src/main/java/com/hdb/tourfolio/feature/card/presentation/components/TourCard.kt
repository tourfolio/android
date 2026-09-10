@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.card.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hdb.tourfolio.R
import com.hdb.tourfolio.domain.card.model.CardRarity
import com.hdb.tourfolio.domain.common.model.RegionType
import com.hdb.tourfolio.domain.common.model.ThemeType
import com.hdb.tourfolio.feature.card.presentation.model.CardListItemUiModel
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.TourfolioTheme

@Composable
fun TourCard(
    item: CardListItemUiModel,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape =
        RoundedCornerShape(10.dp)

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(270.dp)
                .clip(shape)
                .clickable {
                    onClick(item.id)
                },
    ) {
        if (!item.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier =
                    Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else if (item.imageRes != null) {
            Image(
                painter =
                    painterResource(
                        id = item.imageRes,
                    ),
                contentDescription = item.title,
                modifier =
                    Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }

        if (!item.isAcquired) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black.copy(
                                alpha = 0.85f,
                            ),
                        ),
            )

            Text(
                text = "Tourfolio",
                style =
                    LocalAppTypography.current.bodySmall.heavy.copy(
                        color = Primary,
                    ),
                modifier =
                    Modifier.align(
                        Alignment.Center,
                    ),
            )
        }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(
                        Alignment.BottomCenter,
                    )
                    .padding(
                        horizontal = 10.dp,
                        vertical = 8.dp,
                    ),
        ) {
            Text(
                text = item.title,
                style =
                    LocalAppTypography.current.bodySmall.bold.copy(
                        color = Natural100,
                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(
    name = "Acquired Card",
    showBackground = true,
)
@Composable
private fun AcquiredTourCardPreview() {
    TourfolioTheme {
        TourCard(
            item =
                CardListItemUiModel(
                    id = 5L,
                    title = "남산타워",
                    regionType = RegionType.SEOUL,
                    themeType = ThemeType.CULTURE,
                    rarity = CardRarity.RARE,
                    acquiredDate = "2026.05.18",
                    isAcquired = true,
                    imageUrl = null,
                    imageRes = R.drawable.bg_namsan_demo,
                ),
            onClick = {},
            modifier =
                Modifier.padding(16.dp),
        )
    }
}

@Preview(
    name = "Locked Card",
    showBackground = true,
)
@Composable
private fun LockedTourCardPreview() {
    TourfolioTheme {
        TourCard(
            item =
                CardListItemUiModel(
                    id = 7L,
                    title = "첨성대",
                    regionType = RegionType.GYEONGBUK,
                    themeType = ThemeType.HISTORY,
                    rarity = CardRarity.EPIC,
                    acquiredDate = null,
                    isAcquired = false,
                    imageUrl = null,
                    imageRes = R.drawable.bg_cheomseongdae_demo,
                ),
            onClick = {},
            modifier =
                Modifier.padding(16.dp),
        )
    }
}
