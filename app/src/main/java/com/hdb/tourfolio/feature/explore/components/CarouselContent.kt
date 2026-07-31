@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.explore.model.ThemeType
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural30
import com.hdb.tourfolio.ui.theme.Natural90
import com.hdb.tourfolio.ui.theme.Natural95
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.Primary70
import com.hdb.tourfolio.ui.theme.TourfolioTheme

private val OrangeAccent = Color(0xFFFF5C35)

@Composable
fun CarouselContent(
    title: String,
    content: String,
    place: String,
    tags: List<String>,
    themeType: ThemeType,
    currentIndex: Int,
    totalCount: Int,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
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
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Black.copy(alpha = 0.9f),
                                    ),
                            ),
                    ),
        )

        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style =
                        LocalAppTypography.current.headlineLarge.bold.copy(
                            color = Natural100,
                        ),
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(end = 16.dp),
                )

                CarouselThemeMark(
                    themeType = themeType,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = content,
                style =
                    LocalAppTypography.current.bodyLarge.medium.copy(
                        color = Natural90,
                        letterSpacing = 0.1.sp,
                    ),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_location),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(OrangeAccent),
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = place,
                    style =
                        LocalAppTypography.current.bodySmall.medium.copy(
                            color = Natural90,
                        ),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(tags) { tag ->
                    CarouselTag(text = tag)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            CarouselIndicator(
                currentIndex = currentIndex,
                totalCount = totalCount,
            )
        }
    }
}

@Composable
private fun CarouselThemeMark(
    themeType: ThemeType,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(56.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(OrangeAccent),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = themeType.toIconRes()),
            contentDescription = themeType.displayName,
            modifier = Modifier.size(32.dp),
        )
    }
}

@DrawableRes
private fun ThemeType.toIconRes(): Int =
    when (this) {
        ThemeType.HISTORY -> R.drawable.ic_theme_history
        ThemeType.NATURE -> R.drawable.ic_theme_nature
        ThemeType.CULTURE -> R.drawable.ic_theme_culture
    }

@Composable
private fun CarouselTag(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .width(47.dp)
                .height(31.dp)
                .background(
                    color = Primary,
                    shape = RoundedCornerShape(10.dp),
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "#$text",
            style =
                LocalAppTypography.current.labelLarge.bold.copy(
                    color = Natural95,
                ),
            maxLines = 1,
        )
    }
}

@Composable
private fun CarouselIndicator(
    currentIndex: Int,
    totalCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(totalCount) { index ->
            val isSelected = index == currentIndex

            Box(
                modifier =
                    Modifier
                        .height(8.dp)
                        .then(
                            if (isSelected) {
                                Modifier.width(24.dp)
                            } else {
                                Modifier.size(8.dp)
                            },
                        )
                        .clip(CircleShape)
                        .background(
                            if (isSelected) {
                                Natural100
                            } else {
                                Natural100.copy(alpha = 0.4f)
                            },
                        ),
            )
        }
    }
}

@Preview(
    name = "ExploreCarouselContent Preview",
    showBackground = true,
    backgroundColor = 0xFF1A1A1A,
    widthDp = 412,
    heightDp = 500,
)
@Composable
private fun CarouselContentPreview() {
    TourfolioTheme {
        CarouselContent(
            title = "경복궁",
            content = "조선의 시간을 품은 궁궐\n500년의 역사가 살아 숨 쉬는 곳",
            place = "서울특별시 종로구",
            tags = listOf("역사", "궁궐", "공원", "산책"),
            themeType = ThemeType.HISTORY,
            currentIndex = 0,
            totalCount = 3,
        )
    }
}