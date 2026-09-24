@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.mypage.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural50
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural90
import com.hdb.tourfolio.ui.theme.Primary

@Composable
fun MyPageSummaryCard(
    balance: Long,
    cardCount: Int,
    totalProfitRate: Double,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = Primary.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(
                    horizontal = 14.dp,
                    vertical = 20.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SummaryItem(
            value = "${"%,d".format(balance)}P",
            label = "보유 포인트",
            horizontalAlignment = Alignment.End,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )

        SummaryDivider()

        SummaryItem(
            value = "${cardCount}장",
            label = "보유 카드",
        )

        SummaryDivider()

        SummaryItem(
            value =
                if (totalProfitRate > 0) {
                    "+%.2f%%".format(totalProfitRate)
                } else {
                    "%.2f%%".format(totalProfitRate)
                },
            label = "총 수익률",
            horizontalAlignment = Alignment.Start,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SummaryItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    textAlign: TextAlign = TextAlign.Center,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = value,
            modifier =
                Modifier.wrapContentWidth(
                    align = horizontalAlignment,
                    unbounded = true,
                ),
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Visible,
            textAlign = textAlign,
            style =
                LocalAppTypography
                    .current
                    .bodyLarge
                    .heavy
                    .copy(
                        color = Primary,
                    ),
        )

        Text(
            text = label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = textAlign,
            style =
                LocalAppTypography
                    .current
                    .bodySmall
                    .medium
                    .copy(
                        color = Natural50,
                    ),
        )
    }
}

@Composable
private fun SummaryDivider() {
    Box(
        modifier =
            Modifier
                .width(1.dp)
                .height(60.dp)
                .background(
                    Natural90,
                ),
    )
}
