@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.point.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdb.tourfolio.domain.point.model.PointHistory
import com.hdb.tourfolio.domain.point.model.PointHistoryEntry
import com.hdb.tourfolio.ui.theme.Blue
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural90
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.Primary99
import com.hdb.tourfolio.ui.theme.Red
import java.text.NumberFormat
import java.time.OffsetDateTime
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointHistoryBottomSheet(
    onDismissRequest: () -> Unit,
    viewModel: PointHistoryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.processIntent(PointHistoryIntent.FetchPointHistory)
    }

    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Natural100,
        scrimColor =
            Color.Black.copy(
                alpha = 0.8f,
            ),
        dragHandle = {
            Box(
                modifier =
                    Modifier
                        .padding(
                            top = 14.dp,
                            bottom = 22.dp,
                        )
                        .size(
                            width = 72.dp,
                            height = 6.dp,
                        )
                        .background(
                            color = Color(0xFFD7D7D7),
                            shape = RoundedCornerShape(50),
                        ),
            )
        },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
        ) {
            Text(
                text = "포인트 내역",
                style =
                    LocalAppTypography.current.titleSmall.bold.copy(
                        color = Natural10,
                    ),
                modifier =
                    Modifier.padding(
                        horizontal = 22.dp,
                        vertical = 6.dp,
                    ),
            )

            Spacer(modifier = Modifier.height(18.dp))

            HorizontalDivider(color = Natural90)

            when (val historyState = state.historyState) {
                PointHistoryRequestState.Loading -> {
                    PointHistoryPlaceholder {
                        CircularProgressIndicator(color = Primary)
                    }
                }

                is PointHistoryRequestState.Error -> {
                    PointHistoryPlaceholder {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = historyState.message,
                                style = LocalAppTypography.current.bodySmall.medium,
                                color = Natural60,
                            )

                            TextButton(
                                onClick = {
                                    viewModel.processIntent(
                                        PointHistoryIntent.FetchPointHistory,
                                    )
                                },
                            ) {
                                Text(
                                    text = "다시 시도",
                                    style = LocalAppTypography.current.bodySmall.bold,
                                    color = Primary,
                                )
                            }
                        }
                    }
                }

                is PointHistoryRequestState.Success -> {
                    PointHistoryContent(pointHistory = historyState.pointHistory)
                }
            }
        }
    }
}

@Composable
private fun PointHistoryContent(
    pointHistory: PointHistory,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.height(22.dp))

        Column(
            modifier =
                Modifier
                    .padding(horizontal = 22.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Primary99)
                    .padding(
                        horizontal = 20.dp,
                        vertical = 18.dp,
                    ),
        ) {
            Text(
                text = "보유 포인트",
                style = LocalAppTypography.current.bodySmall.medium,
                color = Natural60,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${formatAmount(pointHistory.balance)}P",
                style = LocalAppTypography.current.titleSmall.bold,
                color = Natural10,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (pointHistory.entries.isEmpty()) {
            PointHistoryPlaceholder {
                Text(
                    text = "포인트 내역이 없습니다.",
                    style = LocalAppTypography.current.bodyLarge.medium,
                    color = Natural60,
                )
            }
        } else {
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .heightIn(max = 480.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
            ) {
                itemsIndexed(
                    items = pointHistory.entries,
                ) { index, entry ->
                    PointHistoryRow(entry = entry)

                    if (index != pointHistory.entries.lastIndex) {
                        HorizontalDivider(
                            color = Natural90,
                            modifier = Modifier.padding(horizontal = 22.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PointHistoryRow(
    entry: PointHistoryEntry,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 22.dp,
                    vertical = 16.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = entry.title,
                style = LocalAppTypography.current.bodyLarge.bold,
                color = Natural10,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatPointDate(entry.createdAt),
                style = LocalAppTypography.current.labelLarge.medium,
                color = Natural60,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "${formatSignedAmount(entry.amount)}P",
            style = LocalAppTypography.current.bodyLarge.bold,
            color = if (entry.amount < 0) Blue else Red,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun PointHistoryPlaceholder(content: @Composable () -> Unit) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp)
                .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

private fun formatAmount(value: Long): String =
    NumberFormat
        .getNumberInstance(Locale.KOREA)
        .format(value)

private fun formatSignedAmount(value: Long): String =
    if (value > 0) {
        "+${formatAmount(value)}"
    } else {
        formatAmount(value)
    }

private fun formatPointDate(createdAt: String): String =
    runCatching {
        val date = OffsetDateTime.parse(createdAt).toLocalDate()

        "%04d.%02d.%02d".format(date.year, date.monthValue, date.dayOfMonth)
    }.getOrDefault(createdAt.take(10))
