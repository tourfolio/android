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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.ui.theme.TourfolioTheme

@Composable
fun TourSpotCard(
    title: String,
    content: String,
    tags: List<String>,
    @DrawableRes imageRes: Int,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(185.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray),
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
                                        Color.Black.copy(alpha = 0.05f),
                                        Color.Black.copy(alpha = 0.25f),
                                        Color.Black.copy(alpha = 0.65f),
                                    ),
                            ),
                    ),
        )

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
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(28.dp)
                    .clickable {
                        onLikeClick()
                    },
        )

        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 22.dp, end = 22.dp, bottom = 22.dp),
        ) {
            Text(
                text = title,
                style =
                    MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = content,
                style =
                    MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.85f),
                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                tags.forEach { tag ->
                    TourSpotTag(text = tag)
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
                    color = Color(0xFF4A4A4A).copy(alpha = 0.9f),
                    shape = RoundedCornerShape(6.dp),
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "#$text",
            style =
                MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 80.dp),
        )
    }
}

@Preview(
    name = "TourSpotCard",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun TourSpotCardPreview() {
    val isLiked =
        remember {
            mutableStateOf(false)
        }

    TourfolioTheme {
        TourSpotCard(
            title = "첨성대",
            content = "동양에서 현존하는 가장 오래된 천문대",
            tags = listOf("역사", "궁궐", "공원"),
            imageRes = R.drawable.bg_cheomseongdae_demo,
            isLiked = isLiked.value,
            onLikeClick = {
                isLiked.value = !isLiked.value
            },
            modifier = Modifier.padding(16.dp),
        )
    }
}
