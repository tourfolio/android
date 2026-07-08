@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
fun RegionCard(
    title: String,
    content: String,
    @DrawableRes imageRes: Int,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .width(156.dp)
                .height(230.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF5F5F5F)),
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
                    .size(32.dp)
                    .clickable {
                        onLikeClick()
                    },
        )

        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 22.dp, end = 18.dp, bottom = 26.dp),
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

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = content,
                style =
                    MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White.copy(alpha = 0.85f),
                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(
    name = "RegionCard Preview",
    showBackground = true,
    backgroundColor = 0xFF2B2B2B,
)
@Composable
private fun RegionCardPreview() {
    val isLiked =
        remember {
            mutableStateOf(false)
        }

    TourfolioTheme {
        RegionCard(
            title = "흰여울길",
            content = "부산",
            imageRes = R.drawable.bg_huinnyeoul_demo,
            isLiked = isLiked.value,
            onLikeClick = {
                isLiked.value = !isLiked.value
            },
            modifier = Modifier.padding(20.dp),
        )
    }
}
