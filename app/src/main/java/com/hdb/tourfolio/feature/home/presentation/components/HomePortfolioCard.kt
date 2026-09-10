@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Primary

@Composable
fun HomePortfolioCard(
    totalAsset: Long,
    todayProfit: Long,
    todayProfitRate: Double,
    totalProfitRate: Double,
    stockCount: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(
                    210.dp,
                ),
    ) {
        Image(
            painter =
                painterResource(
                    id = R.drawable.bg_home_portfolio,
                ),
            contentDescription = null,
            modifier =
                Modifier.fillMaxSize(),
            contentScale =
                ContentScale.FillBounds,
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        start = 22.dp,
                        top = 55.dp,
                        end = 22.dp,
                        bottom = 22.dp,
                    ),
        ) {
            Text(
                text = "총 자산",
                style =
                    LocalAppTypography
                        .current
                        .bodySmall
                        .medium
                        .copy(
                            color = Natural10,
                        ),
            )

            Spacer(
                modifier =
                    Modifier.height(
                        2.dp,
                    ),
            )

            Text(
                text =
                    "${formatNumber(totalAsset)}P",
                style =
                    LocalAppTypography
                        .current
                        .titleLarge
                        .copy(
                            color = Natural10,
                        ),
            )

            Spacer(
                modifier =
                    Modifier.height(
                        2.dp,
                    ),
            )

            Text(
                text =
                    "${formatSignedPoint(todayProfit)} " +
                        "(${formatSignedRate(todayProfitRate)})",
                style =
                    LocalAppTypography
                        .current
                        .bodySmall
                        .bold
                        .copy(
                            color = Primary,
                        ),
            )

            Spacer(
                modifier =
                    Modifier.height(
                        8.dp,
                    ),
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.SpaceEvenly,
            ) {
                PortfolioInfoItem(
                    label = "보유 종목",
                    value = stockCount.toString(),
                    modifier =
                        Modifier.weight(
                            1f,
                        ),
                )

                PortfolioDivider()

                PortfolioInfoItem(
                    label = "오늘 수익률",
                    value =
                        formatSignedRate(
                            todayProfitRate,
                        ),
                    modifier =
                        Modifier.weight(
                            1f,
                        ),
                )

                PortfolioDivider()

                PortfolioInfoItem(
                    label = "총 수익률",
                    value =
                        formatSignedRate(
                            totalProfitRate,
                        ),
                    modifier =
                        Modifier.weight(
                            1f,
                        ),
                )
            }
        }
    }
}

@Composable
private fun PortfolioInfoItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(
                8.dp,
            ),
    ) {
        Text(
            text = label,
            style =
                LocalAppTypography
                    .current
                    .bodySmall
                    .medium
                    .copy(
                        color = Natural60,
                    ),
        )

        Text(
            text = value,
            style =
                LocalAppTypography
                    .current
                    .bodyLarge
                    .bold
                    .copy(
                        color = Natural10,
                    ),
        )
    }
}

@Composable
private fun PortfolioDivider() {
    Box(
        modifier =
            Modifier
                .width(
                    1.dp,
                )
                .height(
                    60.dp,
                ),
    )
}

private fun formatNumber(value: Long): String =
    "%,d".format(
        value,
    )

private fun formatSignedPoint(value: Long): String =
    when {
        value > 0 ->
            "+${formatNumber(value)}P"

        value < 0 ->
            "${formatNumber(value)}P"

        else ->
            "0P"
    }

private fun formatSignedRate(value: Double): String =
    when {
        value > 0 ->
            "+%.2f%%".format(
                value,
            )

        value < 0 ->
            "%.2f%%".format(
                value,
            )

        else ->
            "0.00%"
    }
