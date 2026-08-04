@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.explore.mock.NearbyTourSpotUiModel
import com.hdb.tourfolio.feature.explore.mock.TourSpotDetailUiModel
import com.hdb.tourfolio.feature.explore.mock.TourSpotMockData
import com.hdb.tourfolio.feature.explore.model.TagType
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Primary70
import com.hdb.tourfolio.ui.theme.Primary95
import com.hdb.tourfolio.ui.theme.Primary99
import com.hdb.tourfolio.ui.theme.TourfolioTheme

private val DetailCardBackground = Color(0xFFFAFAFA)
private val NoticeBackground = Color(0xFFFFF3F0)

@Composable
fun ExploreDetailScreen(
    tourSpotId: Long,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onNearbySpotClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler {
        onBackClick()
    }
    val detail =
        remember(tourSpotId) {
            TourSpotMockData.findDetailById(tourSpotId)
        }

    if (detail == null) {
        TourSpotNotFoundScreen(
            onBackClick = onBackClick,
            modifier = modifier,
        )
        return
    }

    ExploreDetailContent(
        detail = detail,
        onBackClick = onBackClick,
        onShareClick = onShareClick,
        onNearbySpotClick = onNearbySpotClick,
        modifier = modifier,
    )
}

@Composable
private fun ExploreDetailContent(
    detail: TourSpotDetailUiModel,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onNearbySpotClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Natural100)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding(),
    ) {
        TourSpotHero(
            detail = detail,
            onBackClick = onBackClick,
            onShareClick = onShareClick,
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .offset(y = (-28).dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 32.dp,
                            topEnd = 32.dp,
                        ),
                    )
                    .background(Natural100)
                    .padding(
                        start = 22.dp,
                        top = 34.dp,
                        end = 22.dp,
                        bottom = 16.dp,
                    ),
        ) {
            Text(
                text = detail.title,
                style =
                    LocalAppTypography.current.headlineLarge.bold.copy(
                        color = Natural10,
                    ),
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = detail.description,
                style =
                    LocalAppTypography.current.bodyLarge.medium.copy(
                        color = Natural60,
                    ),
            )

            Spacer(modifier = Modifier.height(28.dp))

            DetailTagRow(
                tags = detail.tags,
            )

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "상세 정보",
                style =
                    LocalAppTypography.current.titleMedium.bold.copy(
                        color = Natural10,
                    ),
            )

            Spacer(modifier = Modifier.height(18.dp))

            TourSpotInformationCard(
                iconRes = R.drawable.ic_link,
                title = "홈페이지 주소",
            ) {
                Text(
                    text = detail.homepageUrl,
                    style =
                        LocalAppTypography.current.bodyLarge.medium.copy(
                            color = Natural60,
                            textDecoration = TextDecoration.Underline,
                        ),
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            TourSpotInformationCard(
                iconRes = R.drawable.ic_phone,
                title = "전화번호",
            ) {
                Text(
                    text = detail.phoneNumber,
                    style =
                        LocalAppTypography.current.bodyLarge.medium.copy(
                            color = Natural60,
                        ),
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            TourSpotInformationCard(
                iconRes = R.drawable.ic_location,
                title = "관광지 주소",
            ) {
                Text(
                    text = detail.address,
                    style =
                        LocalAppTypography.current.bodyLarge.medium.copy(
                            color = Natural60,
                        ),
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            TourSpotInformationCard(
                iconRes = R.drawable.ic_clock,
                title = "관람 시간",
            ) {
                Text(
                    text = detail.operatingHours,
                    style =
                        LocalAppTypography.current.bodyLarge.medium.copy(
                            color = Natural60,
                        ),
                )

                if (
                    detail.operatingNoticeTitle != null &&
                    detail.operatingNoticeContent != null
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    OperatingNotice(
                        title = detail.operatingNoticeTitle,
                        content = detail.operatingNoticeContent,
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            TourSpotInformationCard(
                iconRes = R.drawable.ic_closed,
                title = "쉬는 날",
            ) {
                Text(
                    text = detail.closedDays,
                    style =
                        LocalAppTypography.current.bodyLarge.medium.copy(
                            color = Natural60,
                        ),
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "주변 관광지",
                style =
                    LocalAppTypography.current.titleMedium.bold.copy(
                        color = Natural10,
                    ),
            )

            Spacer(modifier = Modifier.height(18.dp))

            NearbyTourSpotSection(
                items = detail.nearbySpots,
                onItemClick = onNearbySpotClick,
            )
        }
    }
}

@Composable
private fun TourSpotHero(
    detail: TourSpotDetailUiModel,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(430.dp),
    ) {
        Image(
            painter = painterResource(id = detail.imageRes),
            contentDescription = detail.title,
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
                                colors =
                                    listOf(
                                        Color.Black.copy(alpha = 0.28f),
                                        Color.Transparent,
                                    ),
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
            )

            DetailHeaderButton(
                iconRes = R.drawable.ic_share1,
                contentDescription = "공유하기",
                onClick = onShareClick,
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
) {
    Box(
        modifier =
            modifier
                .size(44.dp)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            modifier =
                Modifier
                    .size(26.dp)
                    .rotate(rotationDegrees),
        )
    }
}

@Composable
private fun DetailTagRow(
    tags: List<TagType>,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(tags) { tag ->
            Box(
                modifier =
                    Modifier
                        .background(
                            color = Primary70,
                            shape = RoundedCornerShape(8.dp),
                        )
                        .padding(
                            horizontal = 12.dp,
                            vertical = 10.dp,
                        ),
            ) {
                Text(
                    text = "#${tag.displayName}",
                    style =
                        LocalAppTypography.current.bodySmall.bold.copy(
                            color = Natural100,
                        ),
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
                .background(
                    color = Primary99,
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(
                    horizontal = 18.dp,
                    vertical = 18.dp,
                ),
        verticalAlignment = Alignment.Top,
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                style =
                    LocalAppTypography.current.bodyLarge.bold.copy(
                        color = Natural10,
                    ),
            )

            Spacer(modifier = Modifier.height(10.dp))

            content()
        }
    }
}

@Composable
private fun OperatingNotice(
    title: String,
    content: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = Primary95,
                    shape = RoundedCornerShape(10.dp),
                )
                .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_info),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title,
                style =
                    LocalAppTypography.current.bodySmall.bold.copy(
                        color = Natural10,
                    ),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = content,
            style =
                LocalAppTypography.current.bodySmall.medium.copy(
                    color = Natural60,
                ),
        )
    }
}

@Composable
private fun NearbyTourSpotSection(
    items: List<NearbyTourSpotUiModel>,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(end = 12.dp),
    ) {
        items(
            items = items,
            key = { item ->
                item.id
            },
        ) { item ->
            NearbyTourSpotCard(
                item = item,
                onClick = {
                    onItemClick(item.id)
                },
            )
        }
    }
}

@Composable
private fun NearbyTourSpotCard(
    item: NearbyTourSpotUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(
                    width = 190.dp,
                    height = 210.dp,
                )
                .clip(RoundedCornerShape(10.dp))
                .clickable(onClick = onClick),
    ) {
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = item.title,
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
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.65f),
                                    ),
                            ),
                    ),
        )

        Text(
            text = item.title,
            style =
                LocalAppTypography.current.titleMedium.bold.copy(
                    color = Natural100,
                ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
        )
    }
}

@Composable
private fun TourSpotNotFoundScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Natural100),
    ) {
        Text(
            text = "관광지 정보를 찾을 수 없습니다.",
            style =
                LocalAppTypography.current.bodyLarge.medium.copy(
                    color = Natural60,
                ),
            modifier = Modifier.align(Alignment.Center),
        )

        DetailHeaderButton(
            iconRes = R.drawable.ic_arrow_left_black,
            contentDescription = "뒤로 가기",
            onClick = onBackClick,
            modifier =
                Modifier
                    .statusBarsPadding()
                    .padding(16.dp),
        )
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
        ExploreDetailScreen(
            tourSpotId = 1L,
            onBackClick = {},
            onShareClick = {},
            onNearbySpotClick = {},
        )
    }
}
