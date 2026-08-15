@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.card.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural90
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.Primary99

@Composable
fun OwnedCardStatus(
    ownedCardCount: Int,
    totalCardCount: Int,
    modifier: Modifier = Modifier,
) {
    val progress =
        if (totalCardCount > 0) {
            ownedCardCount.toFloat() / totalCardCount.toFloat()
        } else {
            0f
        }

    val percentage =
        (progress * 100)
            .toInt()
            .coerceIn(
                minimumValue = 0,
                maximumValue = 100,
            )

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = Primary99,
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(
                    start = 26.dp,
                    top = 24.dp,
                    end = 26.dp,
                    bottom = 24.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "보유 카드",
                style =
                    LocalAppTypography.current.bodySmall.medium.copy(
                        color = Natural60,
                    ),
            )

            Text(
                text = "$ownedCardCount / $totalCardCount",
                style =
                    LocalAppTypography.current.titleMedium.bold.copy(
                        color = Natural10,
                    ),
            )
        }

        CardProgressIndicator(
            percentage = percentage,
        )
    }
}

@Composable
private fun CardProgressIndicator(
    percentage: Int,
    modifier: Modifier = Modifier,
) {
    val normalizedProgress =
        percentage
            .coerceIn(
                minimumValue = 0,
                maximumValue = 100,
            ) / 100f

    Box(
        modifier = modifier.size(88.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier.size(88.dp),
        ) {
            val strokeWidth = 6.dp.toPx()
            val inset = strokeWidth / 2

            drawArc(
                color = Natural90,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft =
                    Offset(
                        x = inset,
                        y = inset,
                    ),
                size =
                    Size(
                        width = size.width - strokeWidth,
                        height = size.height - strokeWidth,
                    ),
                style =
                    Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                    ),
            )

            drawArc(
                color = Primary,
                startAngle = -90f,
                sweepAngle = 360f * normalizedProgress,
                useCenter = false,
                topLeft =
                    Offset(
                        x = inset,
                        y = inset,
                    ),
                size =
                    Size(
                        width = size.width - strokeWidth,
                        height = size.height - strokeWidth,
                    ),
                style =
                    Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                    ),
            )
        }

        Text(
            text = "$percentage%",
            style =
                LocalAppTypography.current.bodySmall.heavy.copy(
                    color = Primary,
                ),
        )
    }
}
