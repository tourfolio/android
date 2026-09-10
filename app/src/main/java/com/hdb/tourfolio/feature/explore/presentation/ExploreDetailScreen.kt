@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.presentation

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.explore.presentation.model.ExploreAttractionPointUiModel
import com.hdb.tourfolio.feature.explore.presentation.model.ExploreSpotDetailUiModel
import com.hdb.tourfolio.ui.components.SpotImage
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Primary70
import com.hdb.tourfolio.ui.theme.Primary99
import com.hdb.tourfolio.ui.theme.TourfolioTheme

@Composable
fun ExploreDetailScreen(
    tourSpotId: Long,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExploreDetailViewModel = hiltViewModel(),
) {
    BackHandler {
        onBackClick()
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    /*
     * 해당 관광지 ID로 상세 API 호출
     */
    LaunchedEffect(tourSpotId) {
        viewModel.processIntent(ExploreDetailIntent.FetchSpotDetail(spotId = tourSpotId))
    }

    /*
     * 상세 화면을 완전히 나갈 때 상태 정리
     */
    DisposableEffect(Unit) {
        onDispose {
            viewModel.processIntent(ExploreDetailIntent.ClearSpotDetail)
        }
    }

    when (val spotDetail = state.spotDetail) {
        ExploreSpotDetailUiState.Idle,
        ExploreSpotDetailUiState.Loading,
        -> {
            ExploreDetailLoading(modifier = modifier)
        }

        is ExploreSpotDetailUiState.Success -> {
            ExploreDetailContent(
                detail = spotDetail.detail,
                onBackClick = onBackClick,
                onShareClick = onShareClick,
                modifier = modifier,
            )
        }

        is ExploreSpotDetailUiState.Error -> {
            ExploreDetailError(
                message = spotDetail.message,
                onBackClick = onBackClick,
                onRetryClick = {
                    viewModel.processIntent(ExploreDetailIntent.FetchSpotDetail(spotId = tourSpotId))
                },
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun ExploreDetailContent(
    detail: ExploreSpotDetailUiModel,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val websiteUrl = detail.website.trim()
    val isWebsiteLink =
        websiteUrl.startsWith("https://", ignoreCase = true) ||
            websiteUrl.startsWith("http://", ignoreCase = true)

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Natural100)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding(),
    ) {
        TourSpotHero(
            title = detail.title,
            imageUrl = detail.imageUrl,
            hasImage = detail.hasImage,
            onBackClick = onBackClick,
            onShareClick = onShareClick,
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .offset(y = (-28).dp)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Natural100)
                    .padding(start = 22.dp, top = 34.dp, end = 22.dp, bottom = 16.dp),
        ) {
            Text(
                text = detail.title,
                style = LocalAppTypography.current.headlineLarge.bold.copy(color = Natural10),
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = detail.description,
                style = LocalAppTypography.current.bodyLarge.medium.copy(color = Natural60),
            )

            Spacer(modifier = Modifier.height(28.dp))

            DetailTagRow(tags = detail.tags)

            Spacer(modifier = Modifier.height(36.dp))

            /*
             * 매력 포인트
             *
             * 서버에 attractionPoints가 있을 때만 표시
             */
            if (detail.attractionPoints.isNotEmpty()) {
                Text(
                    text = "매력 포인트",
                    style = LocalAppTypography.current.titleMedium.bold.copy(color = Natural10),
                )

                Spacer(modifier = Modifier.height(18.dp))

                AttractionPointSection(items = detail.attractionPoints)

                Spacer(modifier = Modifier.height(36.dp))
            }

            Text(
                text = "상세 정보",
                style = LocalAppTypography.current.titleMedium.bold.copy(color = Natural10),
            )

            Spacer(modifier = Modifier.height(18.dp))

            TourSpotInformationCard(iconRes = R.drawable.ic_link, title = "홈페이지 주소") {
                Text(
                    text = detail.website,
                    modifier =
                        if (isWebsiteLink) {
                            Modifier.clickable { uriHandler.openUri(websiteUrl) }
                        } else {
                            Modifier
                        },
                    style =
                        LocalAppTypography.current.bodyLarge.medium.copy(
                            color = Natural60,
                            textDecoration =
                                if (isWebsiteLink) {
                                    TextDecoration.Underline
                                } else {
                                    TextDecoration.None
                                },
                        ),
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            TourSpotInformationCard(iconRes = R.drawable.ic_phone, title = "전화번호") {
                Text(
                    text = detail.phoneNumber,
                    style = LocalAppTypography.current.bodyLarge.medium.copy(color = Natural60),
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            TourSpotInformationCard(iconRes = R.drawable.ic_location, title = "관광지 주소") {
                Text(
                    text = detail.address,
                    style = LocalAppTypography.current.bodyLarge.medium.copy(color = Natural60),
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            TourSpotInformationCard(iconRes = R.drawable.ic_clock, title = "관람 시간") {
                Text(
                    text = detail.operatingHours,
                    style = LocalAppTypography.current.bodyLarge.medium.copy(color = Natural60),
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            TourSpotInformationCard(iconRes = R.drawable.ic_closed, title = "쉬는 날") {
                Text(
                    text = detail.closedDays,
                    style = LocalAppTypography.current.bodyLarge.medium.copy(color = Natural60),
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            TourSpotInformationCard(iconRes = R.drawable.ic_info, title = "입장료") {
                Text(
                    text = detail.admissionFee,
                    style = LocalAppTypography.current.bodyLarge.medium.copy(color = Natural60),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun TourSpotHero(
    title: String,
    imageUrl: String,
    hasImage: Boolean,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth().height(430.dp),
    ) {
        SpotImage(
            hasImage = hasImage,
            model = imageUrl,
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        brush =
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = if (hasImage) 0.28f else 0f), Color.Transparent),
                            ),
                    ),
        )

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .offset(y = (-4).dp)
                    .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DetailHeaderButton(
                iconRes = R.drawable.ic_chevron_right_white,
                contentDescription = "뒤로 가기",
                onClick = onBackClick,
                rotationDegrees = 180f,
                tint = if (hasImage) Color.White else Color.Gray,
            )
        }
    }
}

@Composable
private fun DetailHeaderButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    rotationDegrees: Float = 0f,
    tint: Color = Color.White,
) {
    Box(
        modifier = modifier.size(44.dp).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(26.dp).rotate(rotationDegrees),
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(tint),
        )
    }
}

/*
 * API에서는 tags가 List<String>이므로
 * 기존 TagType 의존성을 제거합니다.
 */
@Composable
private fun DetailTagRow(
    tags: List<String>,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items = tags, key = { tag -> tag }) { tag ->
            Box(
                modifier =
                    Modifier
                        .background(color = Primary70, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Text(
                    text = "#$tag",
                    style = LocalAppTypography.current.bodySmall.bold.copy(color = Natural100),
                )
            }
        }
    }
}

@Composable
private fun AttractionPointSection(
    items: List<ExploreAttractionPointUiModel>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items.forEach { item ->
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .background(color = Primary99, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = item.title,
                    style = LocalAppTypography.current.bodySmall.bold.copy(color = Natural10),
                )
            }
        }
    }
}

@Composable
private fun TourSpotInformationCard(
    iconRes: Int,
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(color = Primary99, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = LocalAppTypography.current.bodyLarge.bold.copy(color = Natural10),
            )

            Spacer(modifier = Modifier.height(10.dp))

            content()
        }
    }
}

/*
 * ---------------------------------------------------------
 * Loading
 * ---------------------------------------------------------
 */
@Composable
private fun ExploreDetailLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().background(Natural100),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Primary70)
    }
}

/*
 * ---------------------------------------------------------
 * Error
 * ---------------------------------------------------------
 */
@Composable
private fun ExploreDetailError(
    message: String,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize().background(Natural100),
    ) {
        DetailHeaderButton(
            iconRes = R.drawable.ic_arrow_left_black,
            contentDescription = "뒤로 가기",
            onClick = onBackClick,
            modifier = Modifier.statusBarsPadding().padding(16.dp),
        )

        Column(
            modifier = Modifier.align(Alignment.Center).padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "관광지 정보를 불러오지 못했습니다.",
                style = LocalAppTypography.current.titleMedium.bold.copy(color = Natural10),
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = message,
                style = LocalAppTypography.current.bodyLarge.medium.copy(color = Natural60),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "다시 시도",
                style = LocalAppTypography.current.bodyLarge.bold.copy(color = Primary70),
                modifier = Modifier.clickable(onClick = onRetryClick),
            )
        }
    }
}

@Preview(
    name = "Tour Spot Detail",
    showBackground = true,
    widthDp = 412,
    heightDp = 915,
)
@Composable
private fun TourSpotDetailScreenPreview() {
    TourfolioTheme {
        /*
         * 실제 Screen은 Hilt + API에 의존하므로
         * 간단한 빈 Preview 유지
         */
        Box(
            modifier = Modifier.fillMaxSize().background(Natural100),
        )
    }
}
