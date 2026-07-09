@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.explore.components.CarouselContent
import com.hdb.tourfolio.navigation.Screen
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100
import kotlinx.coroutines.delay

private data class CarouselItem(
    val imageRes: Int,
    val title: String,
    val content: String,
    val place: String,
    val tags: List<String>,
)

private val carouselItems =
    listOf(
        CarouselItem(
            imageRes = R.drawable.bg_explore_gyeongbokgung_demo,
            title = "경복궁",
            content = "조선의 시간을 품은 궁궐\n500년의 역사가 살아 숨 쉬는 곳",
            place = "서울특별시 종로구",
            tags = listOf("역사", "궁궐", "공원", "산책"),
        ),
        CarouselItem(
            imageRes = R.drawable.bg_explore_gyeongbokgung_demo,
            title = "성산일출봉",
            content = "유네스코 세계자연유산\n제주의 상징적인 화산 분화구",
            place = "제주특별자치도 서귀포시",
            tags = listOf("자연", "세계유산", "트레킹"),
        ),
        CarouselItem(
            imageRes = R.drawable.bg_explore_gyeongbokgung_demo,
            title = "흰여울길",
            content = "영화 같은 골목길\n부산 영도의 숨겨진 보석",
            place = "부산광역시 영도구",
            tags = listOf("골목", "바다", "사진"),
        ),
    )

private const val SLIDE_DURATION_MS = 3000L

@Composable
fun ExploreCarouselScreen(navController: NavController) {
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(currentIndex) {
        delay(SLIDE_DURATION_MS)
        if (currentIndex < carouselItems.lastIndex) {
            currentIndex++
        } else {
            navController.navigate(Screen.Explore.route) {
                popUpTo(Screen.ExploreCarousel.route) { inclusive = true }
            }
        }
    }

    val item = carouselItems[currentIndex]

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        ExploreCarouselHeader(
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
        )

        CarouselContent(
            title = item.title,
            content = item.content,
            place = item.place,
            tags = item.tags,
            currentIndex = currentIndex,
            totalCount = carouselItems.size,
            onNextClick = {
                if (currentIndex < carouselItems.lastIndex) {
                    currentIndex++
                } else {
                    navController.navigate(Screen.Explore.route) {
                        popUpTo(Screen.ExploreCarousel.route) { inclusive = true }
                    }
                }
            },
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(),
        )
    }
}

@Composable
private fun ExploreCarouselHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Tourfolio",
            style =
                LocalAppTypography.current.headlineLarge.bold.copy(
                    color = Natural100,
                ),
        )

        Image(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = "search",
            modifier = Modifier.size(28.dp),
        )
    }
}
