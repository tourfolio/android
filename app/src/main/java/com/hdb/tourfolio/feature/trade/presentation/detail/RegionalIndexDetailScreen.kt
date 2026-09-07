@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.trade.presentation.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdb.tourfolio.R
import com.hdb.tourfolio.domain.stock.model.RegionalIndex
import com.hdb.tourfolio.ui.components.CommonBackHeader
import com.hdb.tourfolio.ui.theme.Blue
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural20
import com.hdb.tourfolio.ui.theme.Natural50
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural95
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.Red
import com.hdb.tourfolio.ui.theme.TourfolioTheme

@Composable
fun RegionalIndexDetailScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegionalIndexDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    RegionalIndexDetailContent(
        requestState = state.requestState,
        onBackClick = onBackClick,
        onRetryClick = { viewModel.processIntent(RegionalIndexDetailIntent.Fetch) },
        modifier = modifier,
    )
}

@Composable
private fun RegionalIndexDetailContent(
    requestState: RegionalIndexDetailRequestState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Natural100),
    ) {
        CommonBackHeader(
            title = "오늘의 주요 지수",
            onBackClick = onBackClick,
        )

        when (requestState) {
            is RegionalIndexDetailRequestState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            }

            is RegionalIndexDetailRequestState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "오늘의 주요 지수를 불러오지 못했습니다.",
                            style = LocalAppTypography.current.bodyLarge.bold,
                            color = Natural20,
                        )

                        Text(
                            text = requestState.message,
                            style = LocalAppTypography.current.bodySmall.medium,
                            color = Natural50,
                        )

                        TextButton(onClick = onRetryClick) {
                            Text(
                                text = "다시 시도",
                                style = LocalAppTypography.current.bodySmall.bold,
                                color = Primary,
                            )
                        }
                    }
                }
            }

            is RegionalIndexDetailRequestState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(items = requestState.items, key = { it.region }) { item ->
                        RegionalIndexRow(item = item)
                    }
                }
            }
        }
    }
}

@Composable
private fun RegionalIndexRow(
    item: RegionalIndex,
    modifier: Modifier = Modifier,
) {
    val changeColor =
        when {
            item.averageChangeRate > 0 -> Red
            item.averageChangeRate < 0 -> Blue
            else -> Natural50
        }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Natural95)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        /*
         * 지역별 실제 이미지 API가 없어 임시로 고정 이미지를 사용한다.
         */
        Image(
            painter = painterResource(id = R.drawable.bg_home_hero),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier =
                Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp)),
        )

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = item.region,
            style = LocalAppTypography.current.bodyLarge.bold,
            color = Natural10,
            modifier = Modifier.weight(1f),
        )

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "%+.2f%%".format(item.averageChangeRate),
                style = LocalAppTypography.current.bodyLarge.bold,
                color = changeColor,
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${item.spotCount}개 종목",
                style = LocalAppTypography.current.bodySmall.medium,
                color = Natural60,
            )
        }
    }
}

@Preview(
    name = "Regional Index Detail Preview",
    showBackground = true,
    widthDp = 412,
    heightDp = 800,
)
@Composable
private fun RegionalIndexDetailPreview() {
    TourfolioTheme(dynamicColor = false) {
        RegionalIndexDetailContent(
            requestState =
                RegionalIndexDetailRequestState.Success(
                    listOf(
                        RegionalIndex(region = "서울", averageChangeRate = 1.24, spotCount = 12),
                        RegionalIndex(region = "부산", averageChangeRate = -0.85, spotCount = 8),
                        RegionalIndex(region = "경주", averageChangeRate = 0.42, spotCount = 5),
                        RegionalIndex(region = "제주", averageChangeRate = -1.10, spotCount = 6),
                    ),
                ),
            onBackClick = {},
            onRetryClick = {},
        )
    }
}
