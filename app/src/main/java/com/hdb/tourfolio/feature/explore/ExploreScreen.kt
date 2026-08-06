@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.explore.components.PlaceCard
import com.hdb.tourfolio.feature.explore.components.RegionCard
import com.hdb.tourfolio.feature.explore.components.SearchBar
import com.hdb.tourfolio.feature.explore.components.TourSpotCard
import com.hdb.tourfolio.feature.explore.components.TourSpotCarousel
import com.hdb.tourfolio.feature.explore.mock.TourSpotMockData
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.TourfolioTheme

data class ThemeTravelItem(
    val id: Long,
    val title: String,
    val places: Int,
    val imageRes: Int,
)

@Composable
fun ExploreScreen(
    onCityTravelClick: (Long) -> Unit = {},
    onTourSpotClick: (Long) -> Unit = {},
    onSearchClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val themeTravelItems =
        remember {
            listOf(
                ThemeTravelItem(
                    id = 1L,
                    title = "서울로 떠나는 역사탐방",
                    places = 10,
                    imageRes = R.drawable.bg_seoul_demo,
                ),
                ThemeTravelItem(
                    id = 3L,
                    title = "경주로 떠나는 야경명소",
                    places = 10,
                    imageRes = R.drawable.bg_gyeongju_demo,
                ),
                ThemeTravelItem(
                    id = 2L,
                    title = "여름에 꼭 봐야할 부산 명소",
                    places = 6,
                    imageRes = R.drawable.bg_busan_demo,
                ),
            )
        }

    val featuredItems =
        remember {
            TourSpotMockData.listItems.take(4)
        }

    val recommendedItems =
        remember {
            TourSpotMockData.detailItems
                .filter { item ->
                    item.id in listOf(1L, 3L, 7L)
                }
        }

    val trendingItems =
        remember {
            TourSpotMockData.listItems
                .filter { item ->
                    item.id in listOf(6L, 3L, 5L, 2L)
                }
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Natural100)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 36.dp),
    ) {
        ExploreHeader(
            modifier =
                Modifier.padding(
                    start = 22.dp,
                    top = 20.dp,
                    end = 22.dp,
                ),
        )

        Spacer(modifier = Modifier.height(28.dp))

        SearchBar(
            value = "",
            onValueChange = {},
            readOnly = true,
            onClick = onSearchClick,
            modifier =
                Modifier.padding(horizontal = 22.dp),
        )

        Spacer(modifier = Modifier.height(18.dp))

        TourSpotCarousel(
            items = featuredItems,
            onItemClick = onTourSpotClick,
        )

        Spacer(modifier = Modifier.height(30.dp))

        ExploreSectionTitle(
            title = "지역별 맞춤 여행지",
            modifier = Modifier.padding(horizontal = 22.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier =
                Modifier.padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.spacedBy(17.dp),
        ) {
            themeTravelItems.forEach { item ->
                PlaceCard(
                    title = item.title,
                    places = item.places,
                    imageRes = item.imageRes,
                    onClick = {
                        /*
                         * 현재 CityTravelDetail 임시 데이터는
                         * 부산 id 2만 연결된 상태입니다.
                         */
                        if (item.id == 2L) {
                            onCityTravelClick(item.id)
                        }
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(34.dp))

        ExploreSectionTitle(
            title = "놓치면 아쉬운 추천 명소",
            modifier = Modifier.padding(horizontal = 22.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding =
                androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 22.dp,
                ),
        ) {
            items(
                items = recommendedItems,
                key = { item ->
                    item.id
                },
            ) { item ->
                TourSpotCard(
                    id = item.id,
                    title = item.title,
                    content = item.description,
                    tags = item.tags,
                    imageRes = item.imageRes,
                    onClick = onTourSpotClick,
                    modifier = Modifier.width(360.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(34.dp))

        ExploreSectionTitle(
            title = "지금 뜨는 여행지",
            modifier = Modifier.padding(horizontal = 22.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding =
                androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 22.dp,
                ),
        ) {
            items(
                items = trendingItems,
                key = { item ->
                    item.id
                },
            ) { item ->
                RegionCard(
                    id = item.id,
                    title = item.title,
                    regionName = item.regionType.displayName,
                    imageRes = item.imageRes,
                    onClick = onTourSpotClick,
                )
            }
        }
    }
}

@Composable
private fun ExploreHeader(modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Tourfolio",
            style =
                LocalAppTypography.current.headlineLarge.heavy.copy(
                    color = Natural10,
                ),
        )

        Image(
            painter = painterResource(id = R.drawable.ic_bell_black),
            contentDescription = "알림",
            modifier = Modifier.size(27.dp),
        )
    }
}

@Composable
private fun ExploreSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    showMoreIcon: Boolean = false,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style =
                LocalAppTypography.current.titleMedium.bold.copy(
                    color = Natural10,
                ),
        )

        if (showMoreIcon) {
            Image(
                painter =
                    painterResource(
                        id = R.drawable.ic_arrow_down_gray,
                    ),
                contentDescription = "더보기",
                modifier =
                    Modifier
                        .size(16.dp)
                        .rotate(-90f),
            )
        }
    }
}

@Preview(
    name = "Explore Screen Preview",
    showBackground = true,
    widthDp = 412,
    heightDp = 915,
)
@Composable
private fun ExploreScreenPreview() {
    TourfolioTheme {
        ExploreScreen()
    }
}
