@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.explore.model.TagType
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.TourfolioTheme

@Composable
fun TourSpotCard(
    id: Long,
    title: String,
    content: String,
    tags: List<TagType>,
    @DrawableRes imageRes: Int,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                    onClick(id)
                },
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        brush =
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.12f),
                                        Color.Black.copy(alpha = 0.72f),
                                    ),
                            ),
                    ),
        )

        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(20.dp),
        ) {
            Text(
                text = title,
                style =
                    LocalAppTypography.current.titleMedium.bold.copy(
                        color = Natural100,
                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = content,
                style =
                    LocalAppTypography.current.bodySmall.medium.copy(
                        color = Natural100.copy(alpha = 0.88f),
                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                tags.take(3).forEach { tag ->
                    TourSpotTag(
                        text = tag.displayName,
                    )
                }
            }
        }
    }
}

@Composable
private fun TourSpotTag(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .background(
                    color = Primary,
                    shape = RoundedCornerShape(7.dp),
                )
                .padding(
                    horizontal = 12.dp,
                    vertical = 8.dp,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "#$text",
            style =
                LocalAppTypography.current.bodySmall.bold.copy(
                    color = Natural100,
                ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 72.dp),
        )
    }
}

@Preview(
    name = "Tour Spot Card",
    showBackground = true,
    widthDp = 412,
)
@Composable
private fun TourSpotCardPreview() {
    TourfolioTheme {
        TourSpotCard(
            id = 7L,
            title = "첨성대",
            content = "동양에서 현존하는 가장 오래된 천문대",
            tags =
                listOf(
                    TagType.HISTORY,
                    TagType.PALACE,
                    TagType.POPULAR_PLACE,
                ),
            imageRes = R.drawable.bg_cheomseongdae_demo,
            onClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}