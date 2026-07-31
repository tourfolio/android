@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.explore.components.CityTravelSpotCard
import com.hdb.tourfolio.feature.explore.model.CityTravelDetailUiModel
import com.hdb.tourfolio.feature.explore.model.CityTravelSpotUiModel
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.TourfolioTheme

private val busanSummerTravelDetail =
    CityTravelDetailUiModel(
        id = 2L,
        categoryTitle = "도시별 추천여행",
        title = "부산으로 떠나는 여름여행",
        placeCount = 6,
        cityImageRes = R.drawable.bg_busan_demo,
        spots =
            listOf(
                CityTravelSpotUiModel(
                    id = 1L,
                    title = "흰여울길",
                    imageRes = R.drawable.bg_huinnyeoul_demo,
                ),
                CityTravelSpotUiModel(
                    id = 2L,
                    title = "광안대교",
                    imageRes = R.drawable.bg_busan_demo,
                ),
                CityTravelSpotUiModel(
                    id = 3L,
                    title = "해운대",
                    imageRes = R.drawable.bg_busan_demo,
                ),
                CityTravelSpotUiModel(
                    id = 4L,
                    title = "황령산 전망대",
                    imageRes = R.drawable.bg_busan_demo,
                ),
                CityTravelSpotUiModel(
                    id = 5L,
                    title = "감천 문화마을",
                    imageRes = R.drawable.bg_busan_demo,
                ),
                CityTravelSpotUiModel(
                    id = 6L,
                    title = "해동 용궁사",
                    imageRes = R.drawable.bg_busan_demo,
                ),
            ),
    )

@Composable
fun CityTravelDetailScreen(
    travelId: Long,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onSpotClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    /*
     * 현재 부산 임시 데이터(travelId=2) 하나만 사용
     */
    val travelDetail =
        remember(travelId) {
            busanSummerTravelDetail
        }

    CityTravelDetailContent(
        travelDetail = travelDetail,
        onBackClick = onBackClick,
        onShareClick = onShareClick,
        onSpotClick = onSpotClick,
        modifier = modifier,
    )
}

@Composable
private fun CityTravelDetailContent(
    travelDetail: CityTravelDetailUiModel,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onSpotClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Natural100),
    ) {
        CityTravelTopBar(
            title = travelDetail.categoryTitle,
            onBackClick = onBackClick,
            onShareClick = onShareClick,
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding =
                PaddingValues(
                    start = 20.dp,
                    top = 12.dp,
                    end = 20.dp,
                    bottom = 32.dp,
                ),
        ) {
            item(
                span = {
                    GridItemSpan(maxLineSpan)
                },
            ) {
                CityTravelSummary(
                    title = travelDetail.title,
                    placeCount = travelDetail.placeCount,
                    cityImageRes = travelDetail.cityImageRes,
                )
            }

            item(
                span = {
                    GridItemSpan(maxLineSpan)
                },
            ) {
                Spacer(
                    modifier = Modifier.height(34.dp),
                )
            }

            items(
                items = travelDetail.spots,
                key = { spot ->
                    spot.id
                },
            ) { spot ->
                CityTravelSpotCard(
                    title = spot.title,
                    imageRes = spot.imageRes,
                    onClick = {
                        onSpotClick(spot.id)
                    },
                )
            }
        }
    }
}

@Composable
private fun CityTravelTopBar(
    title: String,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Natural100)
                .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(48.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_left_black),
                contentDescription = "뒤로 가기",
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(Natural10),
            )
        }

        Text(
            text = title,
            style =
                LocalAppTypography.current.titleSmall.bold.copy(
                    color = Natural10,
                ),
            modifier =
                Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
        )

        IconButton(
            onClick = onShareClick,
            modifier = Modifier.size(48.dp),
        ) {
            Image(
                painter =
                    painterResource(
                        id = R.drawable.ic_share_black,
                    ),
                contentDescription = "공유하기",
                modifier = Modifier.size(26.dp),
                colorFilter = ColorFilter.tint(Natural10),
            )
        }
    }
}

@Composable
private fun CityTravelSummary(
    title: String,
    placeCount: Int,
    cityImageRes: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(end = 20.dp),
        ) {
            Text(
                text = title,
                style =
                    LocalAppTypography.current.headlineLarge.bold.copy(
                        color = Natural10,
                    ),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_location),
                    contentDescription = null,
                    modifier = Modifier.size(27.dp),
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "$placeCount places",
                    style =
                        LocalAppTypography.current.bodyLarge.medium.copy(
                            color = Natural60,
                        ),
                )
            }
        }

        Image(
            painter = painterResource(id = cityImageRes),
            contentDescription = title,
            modifier =
                Modifier
                    .size(82.dp)
                    .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
    }
}

@Preview(
    name = "City Travel Detail",
    showBackground = true,
    widthDp = 412,
    heightDp = 915,
)
@Composable
private fun CityTravelDetailScreenPreview() {
    TourfolioTheme {
        CityTravelDetailContent(
            travelDetail = busanSummerTravelDetail,
            onBackClick = {},
            onShareClick = {},
            onSpotClick = {},
        )
    }
}
