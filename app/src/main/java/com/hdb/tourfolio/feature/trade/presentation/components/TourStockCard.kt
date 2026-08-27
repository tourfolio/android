@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.trade.presentation.components

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
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural99
import com.hdb.tourfolio.ui.theme.Primary10
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
    showLike: Boolean = true,
    isLiked: Boolean = false,
    onLikeClick: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    val changeColor =
        when (changeType) {
            PriceChangeType.RISE -> Red
            PriceChangeType.FALL -> Blue
            PriceChangeType.UNCHANGED -> Natural50
        }

    val titleStyle =
        if (showLike) {
            LocalAppTypography.current.titleSmall.bold
        } else {
            LocalAppTypography.current.bodyLarge.bold
        }
    val titleColor = if (showLike) Natural10 else Primary10

    val subtitleStyle =
        if (showLike) {
            LocalAppTypography.current.bodyLarge.medium
        } else {
            LocalAppTypography.current.bodySmall.medium
        }
    val subtitleColor = if (showLike) Natural50 else Natural60

    val priceColor = if (showLike) Natural10 else Primary10

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Natural99)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
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

            Spacer(modifier = Modifier.width(12.dp))
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = titleStyle,
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                Spacer(modifier = Modifier.width(6.dp))

                if (showLike) {
                    Image(
                        painter =
                            painterResource(
                                id =
                                    if (isLiked) {
                                        R.drawable.ic_like_full
                                    } else {
                                        R.drawable.ic_like_empty
                                    },
                            ),
                        contentDescription = "like",
                        modifier =
                            Modifier
                                .size(20.dp)
                                .clickable {
                                    onLikeClick()
                                },
                    )
                } else {
                    Text(
                        text = priceText,
                        style = LocalAppTypography.current.titleSmall.bold,
                        color = priceColor,
                        maxLines = 1,
                    )
                }
            }

            if (showLike) {
                if (!subtitle.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = subtitle,
                        style = subtitleStyle,
                        color = subtitleColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = priceText,
                        style = LocalAppTypography.current.titleSmall.bold,
                        color = priceColor,
                        maxLines = 1,
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = changeText,
                        style = LocalAppTypography.current.bodySmall.bold,
                        color = changeColor,
                        maxLines = 1,
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        if (subtitle.isNullOrBlank()) {
                            Arrangement.End
                        } else {
                            Arrangement.SpaceBetween
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (!subtitle.isNullOrBlank()) {
                        Text(
                            text = subtitle,
                            style = subtitleStyle,
                            color = subtitleColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    Text(
                        text = changeText,
                        style = LocalAppTypography.current.bodySmall.bold,
                        color = changeColor,
                        maxLines = 1,
                    )
                }
            }
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
                isLiked = true,
            )
        }
    }
}
