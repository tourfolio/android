@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.trade.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.ui.theme.Blue
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural30
import com.hdb.tourfolio.ui.theme.Natural50
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural70
import com.hdb.tourfolio.ui.theme.Natural95
import com.hdb.tourfolio.ui.theme.Natural99
import com.hdb.tourfolio.ui.theme.Red
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * 화면마다 지원하는 기간 API 파라미터 집합이 달라(예: 종목 상세는 1W/3M/1Y/5Y/ALL,
 * 포트폴리오 요약은 1W/1M/3M/1Y/ALL) 화면별로 별도 enum을 만들고 이 인터페이스만 공유한다.
 */
interface PeriodOption {
    val label: String
}

/*
 * api/stocks/{spotId}/chart의 period 파라미터(1W/3M/1Y/5Y/ALL)와 1:1 대응한다.
 */
enum class AssetPeriod(
    override val label: String,
) : PeriodOption {
    WEEK("1주"),
    THREE_MONTH("3달"),
    YEAR("1년"),
    FIVE_YEAR("5년"),
    ALL("전체"),
}

data class AssetPoint(
    val dateLabel: String,
    val value: Long,
)

@Composable
fun <T : PeriodOption> AssetChartCard(
    label: String,
    totalAmount: Long,
    changeAmount: Long,
    changeRate: Double,
    assetHistory: List<AssetPoint>,
    periods: List<T>,
    selectedPeriod: T,
    onPeriodSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val changeColor =
        when {
            changeAmount > 0 -> Red
            changeAmount < 0 -> Blue
            else -> Natural50
        }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Natural99)
                .padding(20.dp),
    ) {
        Text(
            text = label,
            style = LocalAppTypography.current.bodySmall.medium,
            color = Natural10,
            maxLines = 1,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "%,dP".format(totalAmount),
            style = LocalAppTypography.current.titleLarge,
            color = Natural10,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "%+,dP (%+.2f%%)".format(changeAmount, changeRate),
            style = LocalAppTypography.current.bodySmall.bold,
            color = changeColor,
        )

        Spacer(modifier = Modifier.height(20.dp))

        AssetLineChart(
            values = assetHistory.map { it.value.toFloat() },
            lineColor = changeColor,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(140.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        ChartAxisLabels(
            labels = assetHistory.map { it.dateLabel },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(20.dp))

        PeriodTabRow(
            periods = periods,
            selectedPeriod = selectedPeriod,
            onPeriodSelected = onPeriodSelected,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun AssetLineChart(
    values: List<Float>,
    lineColor: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        if (values.size < 2) return@Canvas

        val minValue = values.min()
        val maxValue = values.max()
        val valueRange = (maxValue - minValue).takeIf { it > 0f } ?: 1f

        val topPadding = size.height * 0.1f
        val chartHeight = size.height - topPadding
        val xStep = size.width / (values.size - 1)

        val linePoints =
            values.mapIndexed { index, value ->
                val normalized = (value - minValue) / valueRange
                Offset(
                    x = index * xStep,
                    y = topPadding + chartHeight * (1f - normalized),
                )
            }

        val linePath =
            Path().apply {
                moveTo(linePoints.first().x, linePoints.first().y)
                linePoints.drop(1).forEach { point ->
                    lineTo(point.x, point.y)
                }
            }

        val areaPath =
            Path().apply {
                addPath(linePath)
                lineTo(linePoints.last().x, size.height)
                lineTo(linePoints.first().x, size.height)
                close()
            }

        drawPath(
            path = areaPath,
            brush =
                Brush.verticalGradient(
                    colors = listOf(lineColor.copy(alpha = 0.35f), Color.Transparent),
                    startY = 0f,
                    endY = size.height,
                ),
        )

        drawPath(
            path = linePath,
            color = lineColor,
            style =
                Stroke(
                    width = 2.5.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
        )
    }
}

private const val MAX_AXIS_LABEL_COUNT = 6

/*
 * 데이터 포인트가 많아지면(1달 이상) 라벨을 전부 표시하면 겹치므로,
 * 그래프 선(전체 데이터)은 그대로 두고 하단 날짜 라벨만 최대 6개로 균등 샘플링해서 보여준다.
 */
private fun <T> List<T>.evenlySampled(maxCount: Int): List<T> {
    if (size <= maxCount) return this

    return (0 until maxCount)
        .map { index -> this[(index * (size - 1) / (maxCount - 1).toFloat()).roundToInt()] }
        .distinct()
}

@Composable
private fun ChartAxisLabels(
    labels: List<String>,
    modifier: Modifier = Modifier,
) {
    val visibleLabels =
        remember(labels) {
            labels.evenlySampled(MAX_AXIS_LABEL_COUNT)
        }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        visibleLabels.forEach { label ->
            Text(
                text = label,
                style = LocalAppTypography.current.labelLarge.medium,
                color = Natural60,
            )
        }
    }
}

@Composable
fun <T : PeriodOption> PeriodTabRow(
    periods: List<T>,
    selectedPeriod: T,
    onPeriodSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        periods.forEach { period ->
            PeriodTabChip(
                label = period.label,
                selected = period == selectedPeriod,
                onClick = {
                    onPeriodSelected(period)
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PeriodTabChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .widthIn(min = 57.dp)
                .height(31.dp)
                .clip(RoundedCornerShape(10.dp))
                .then(
                    if (selected) {
                        Modifier.background(Natural30)
                    } else {
                        Modifier.border(1.dp, Natural70, RoundedCornerShape(10.dp))
                    },
                ).clickable(onClick = onClick)
                .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = LocalAppTypography.current.labelLarge.bold,
            color = if (selected) Natural95 else Natural60,
            maxLines = 1,
            softWrap = false,
        )
    }
}

/*
 * 실제 API가 현재 시점 값만 제공하고 기간별 추이는 제공하지 않을 때,
 * 임시 추이 데이터의 끝점을 실제 값에 맞춰 보정한다.
 */
fun alignAssetHistory(
    history: List<AssetPoint>,
    lastValue: Long,
): List<AssetPoint> {
    if (history.isEmpty()) return history

    val offset = lastValue - history.last().value
    return history.map { it.copy(value = it.value + offset) }
}

private const val CHART_POINT_COUNT = 6

/*
 * 임시 데이터 - 시세 API 연동 전까지 사용
 * 선택된 기간의 날짜 간격(stepDays)만큼 오늘부터 -stepDays씩 계속 감소시키며 포인트를 만든다.
 */
fun mockAssetHistory(
    period: AssetPeriod,
    seed: Long = 0L,
): List<AssetPoint> {
    val stepDays =
        when (period) {
            AssetPeriod.WEEK -> 7L
            AssetPeriod.THREE_MONTH -> 90L
            AssetPeriod.YEAR -> 365L
            AssetPeriod.FIVE_YEAR -> 1_825L
            AssetPeriod.ALL -> 3_650L
        }

    val random = Random(seed * 31 + period.ordinal + 1L)
    val formatter = DateTimeFormatter.ofPattern("M/d")
    val today = LocalDate.now()

    var value = 10_000_000L
    val points = mutableListOf<AssetPoint>()

    for (i in (CHART_POINT_COUNT - 1) downTo 0) {
        if (i != CHART_POINT_COUNT - 1) {
            val changePercent = random.nextDouble(-0.03, 0.05)
            value = (value * (1 + changePercent)).toLong()
        }

        points +=
            AssetPoint(
                dateLabel = today.minusDays(i * stepDays).format(formatter),
                value = value,
            )
    }

    return points
}
