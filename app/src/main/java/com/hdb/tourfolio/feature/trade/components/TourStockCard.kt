@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.trade.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.ui.theme.Blue
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural50
import com.hdb.tourfolio.ui.theme.Natural99
import com.hdb.tourfolio.ui.theme.Red
import com.hdb.tourfolio.ui.theme.TourfolioTheme

enum class PriceChangeType {
    RISE,
    FALL,
    UNCHANGED,
}

@Composable
fun TourStockCard(
    title: String,
    priceText: String,
    changeText: String,
    changeType: PriceChangeType,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    @DrawableRes imageRes: Int? = null,
    onClick: () -> Unit = {},
) {
    val changeColor =
        when (changeType) {
            PriceChangeType.RISE -> Red
            PriceChangeType.FALL -> Blue
            PriceChangeType.UNCHANGED -> Natural50
        }

    val hasDetailContent =
        imageRes != null || !subtitle.isNullOrBlank()

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Natural99)
                .clickable(onClick = onClick)
                .padding(horizontal = 22.dp, vertical = 20.dp),
    ) {
        if (hasDetailContent) {
            HoldingStockCardContent(
                title = title,
                subtitle = subtitle,
                imageRes = imageRes,
                priceText = priceText,
                changeText = changeText,
                changeColor = changeColor,
            )
        } else {
            ExploreStockCardContent(
                title = title,
                priceText = priceText,
                changeText = changeText,
                changeColor = changeColor,
            )
        }
    }
}

@Composable
private fun ExploreStockCardContent(
    title: String,
    priceText: String,
    changeText: String,
    changeColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                style = LocalAppTypography.current.titleSmall.bold,
                color = Natural10,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = priceText,
                style = LocalAppTypography.current.titleSmall.bold,
                color = Natural10,
                maxLines = 1,
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = changeText,
            style = LocalAppTypography.current.bodySmall.bold,
            color = changeColor,
            maxLines = 1,
            modifier = Modifier.align(Alignment.End),
        )
    }
}

@Composable
private fun HoldingStockCardContent(
    title: String,
    subtitle: String?,
    @DrawableRes imageRes: Int?,
    priceText: String,
    changeText: String,
    changeColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (imageRes != null) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier =
                    Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.width(16.dp))
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = title,
                style = LocalAppTypography.current.titleSmall.bold,
                color = Natural10,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = subtitle,
                    style = LocalAppTypography.current.bodyLarge.medium,
                    color = Natural50,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = priceText,
                style = LocalAppTypography.current.titleSmall.bold,
                color = Natural10,
                maxLines = 1,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = changeText,
                style = LocalAppTypography.current.bodySmall.bold,
                color = changeColor,
                maxLines = 1,
            )
        }
    }
}

@Preview(
    name = "종목탐색 카드",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 412,
)
@Composable
private fun ExploreTourStockCardPreview() {
    TourfolioTheme(dynamicColor = false) {
        Box(
            modifier = Modifier.padding(16.dp),
        ) {
            TourStockCard(
                title = "경복궁",
                priceText = "00,000P",
                changeText = "+00,000P (+0.00%)",
                changeType = PriceChangeType.RISE,
            )
        }
    }
}

@Preview(
    name = "보유종목 카드",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 412,
)
@Composable
private fun HoldingTourStockCardPreview() {
    TourfolioTheme(dynamicColor = false) {
        Box(
            modifier = Modifier.padding(16.dp),
        ) {
            TourStockCard(
                title = "안압지",
                subtitle = "경주",
                priceText = "00,000P",
                changeText = "+00,000P (+0.00%)",
                changeType = PriceChangeType.RISE,
                imageRes = R.drawable.bg_cheomseongdae_demo,
            )
        }
    }
}
