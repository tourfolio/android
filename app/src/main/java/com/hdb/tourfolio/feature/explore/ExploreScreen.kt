@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.explore.components.PlaceCard
import com.hdb.tourfolio.feature.explore.components.RegionCard
import com.hdb.tourfolio.feature.explore.components.SearchBar
import com.hdb.tourfolio.feature.explore.components.TourSpotCard
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.TourfolioTheme

data class ThemeTravelItem(
    val title: String,
    val places: Int,
    val imageRes: Int,
)

data class RegionTravelItem(
    val title: String,
    val content: String,
    val imageRes: Int,
)

data class TourSpotItem(
    val title: String,
    val content: String,
    val tags: List<String>,
    val imageRes: Int,
)

@Composable
fun ExploreScreen() {
    val tourSpotLikedStates =
        remember {
            mutableStateListOf(false, false)
        }

    val regionLikedStates =
        remember {
            mutableStateListOf(false, true, false)
        }

    val searchText =
        remember {
            mutableStateOf("")
        }

    val themeTravelItems =
        listOf(
            ThemeTravelItem(
                title = "서울로 떠나는 맛집 탐방",
                places = 10,
                imageRes = R.drawable.bg_seoul_demo,
            ),
            ThemeTravelItem(
                title = "부산으로 떠나는 여름여행",
                places = 6,
                imageRes = R.drawable.bg_busan_demo,
            ),
            ThemeTravelItem(
                title = "경주로 떠나는 역사여행",
                places = 10,
                imageRes = R.drawable.bg_gyeongju_demo,
            ),
        )

    val regionTravelItems =
        listOf(
            RegionTravelItem(
                title = "흰여울길",
                content = "부산",
                imageRes = R.drawable.bg_huinnyeoul_demo,
            ),
            RegionTravelItem(
                title = "성산일출봉",
                content = "제주도",
                imageRes = R.drawable.bg_seongsan_demo,
            ),
            RegionTravelItem(
                title = "남산타워",
                content = "서울",
                imageRes = R.drawable.bg_namsan_demo,
            ),
        )

    val tourSpotItems =
        listOf(
            TourSpotItem(
                title = "첨성대",
                content = "동양에서 현존하는 가장 오래된 천문대",
                tags = listOf("역사", "궁궐", "공원"),
                imageRes = R.drawable.bg_cheomseongdae_demo,
            ),
            TourSpotItem(
                title = "첨성대",
                content = "동양에서 현존하는 가장 오래된 천문대",
                tags = listOf("역사", "궁궐", "공원"),
                imageRes = R.drawable.bg_cheomseongdae_demo,
            ),
        )

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(0xFF2B2B2B)),
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_search_top),
            contentDescription = null,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(360.dp),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .background(
                        brush =
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        Color.Transparent,
                                        Color.Transparent,
                                        Color(0x802B2B2B),
                                        Color(0xCC2B2B2B),
                                        Color(0xFF2B2B2B),
                                    ),
                            ),
                    ),
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(25.dp),
        ) {
            ExploreHeader()

            ExploreIntroText()

            SearchBar(
                value = searchText.value,
                onValueChange = {
                    searchText.value = it
                    // 추후 검색 API 연동
                },
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(tourSpotItems) { index, item ->
                    TourSpotCard(
                        title = item.title,
                        content = item.content,
                        tags = item.tags,
                        imageRes = item.imageRes,
                        isLiked = tourSpotLikedStates[index],
                        onLikeClick = {
                            tourSpotLikedStates[index] = !tourSpotLikedStates[index]
                        },
                        modifier = Modifier.width(360.dp),
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                SectionTitle(title = "테마로 떠나는 여행")

                themeTravelItems.forEach { item ->
                    PlaceCard(
                        title = item.title,
                        places = item.places,
                        imageRes = item.imageRes,
                        onClick = {
                            // 테마 여행 상세 화면 이동
                        },
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "지금 뜨는 여행지",
                    style =
                        LocalAppTypography.current.titleMedium.bold.copy(
                            color = Color.White,
                        ),
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    itemsIndexed(regionTravelItems) { index, item ->
                        RegionCard(
                            title = item.title,
                            content = item.content,
                            imageRes = item.imageRes,
                            isLiked = regionLikedStates[index],
                            onLikeClick = {
                                regionLikedStates[index] = !regionLikedStates[index]
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreHeader() {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_tourfolio_logo),
            contentDescription = "Tourfolio",
            modifier =
                Modifier
                    .width(138.dp)
                    .height(36.dp),
        )

        Image(
            painter = painterResource(id = R.drawable.ic_bell),
            contentDescription = "notification",
            modifier = Modifier.size(28.dp),
        )
    }
}

@Composable
private fun ExploreIntroText() {
    Column(
        modifier = Modifier.padding(top = 26.dp),
    ) {
        Text(
            text = "어디로",
            style =
                MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                ),
        )

        Text(
            text = "떠나고 싶나요?",
            style =
                MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                ),
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "당신의 다음 여행이 기다리고 있어요",
            style =
                LocalAppTypography.current.bodyLarge.medium.copy(
                    color = Color.White,
                ),
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style =
                LocalAppTypography.current.titleMedium.bold.copy(
                    color = Color.White,
                ),
        )

        Image(
            painter = painterResource(id = R.drawable.ic_chevron_right_white),
            contentDescription = "더보기",
            modifier =
                Modifier
                    .size(16.dp),
        )
    }
}

// preview
@Preview(
    name = "Explore Screen Preview",
    showBackground = true,
    widthDp = 412,
    heightDp = 915,
)
@Composable
fun ExploreScreenPreview() {
    TourfolioTheme {
        ExploreScreen()
    }
}
