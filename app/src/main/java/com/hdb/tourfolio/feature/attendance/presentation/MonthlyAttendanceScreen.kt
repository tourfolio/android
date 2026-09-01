@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.attendance.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdb.tourfolio.domain.mission.model.AttendanceCalendar
import com.hdb.tourfolio.ui.components.CommonBackHeader
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural50
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural70
import com.hdb.tourfolio.ui.theme.Natural90
import com.hdb.tourfolio.ui.theme.Natural95
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.Primary95
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun MonthlyAttendanceScreen(
    onBackClick: () -> Unit,
    viewModel: MonthlyAttendanceViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MonthlyAttendanceEffect.AttendanceChecked -> {
                    Toast
                        .makeText(
                            context,
                            "출석 완료! ${effect.pointsAwarded}P 적립 (${effect.consecutiveDays}일 연속)",
                            Toast.LENGTH_SHORT,
                        )
                        .show()
                }

                is MonthlyAttendanceEffect.Error -> {
                    Toast
                        .makeText(context, effect.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Natural100),
    ) {
        CommonBackHeader(
            title = "출석 체크",
            onBackClick = onBackClick,
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp),
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text =
                    if (state.isCurrentMonth) {
                        "이번달의 출석 횟수"
                    } else {
                        "${state.yearMonth.monthValue}월 출석 횟수"
                    },
                style = LocalAppTypography.current.bodyLarge.medium,
                color = Natural60,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${(state.calendarState as? AttendanceCalendarRequestState.Success)?.calendar?.attendedCount ?: 0}회",
                style = LocalAppTypography.current.headlineLarge.bold,
                color = Primary,
            )

            Spacer(modifier = Modifier.height(28.dp))

            CalendarCard(
                yearMonth = state.yearMonth,
                canGoNext = state.canGoNext,
                calendarState = state.calendarState,
                onPrevMonth = { viewModel.processIntent(MonthlyAttendanceIntent.PrevMonth) },
                onNextMonth = { viewModel.processIntent(MonthlyAttendanceIntent.NextMonth) },
                onRetry = { viewModel.processIntent(MonthlyAttendanceIntent.Load) },
            )

            if (state.isCurrentMonth) {
                Spacer(modifier = Modifier.height(28.dp))

                val attendedToday =
                    (state.calendarState as? AttendanceCalendarRequestState.Success)
                        ?.calendar
                        ?.attendedDays
                        ?.contains(LocalDate.now().dayOfMonth) == true

                CheckInButton(
                    attendedToday = attendedToday,
                    isCheckingIn = state.isCheckingAttendance,
                    onClick = { viewModel.processIntent(MonthlyAttendanceIntent.CheckAttendance) },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private val WEEKDAY_LABELS = listOf("일", "월", "화", "수", "목", "금", "토")

@Composable
private fun CalendarCard(
    yearMonth: YearMonth,
    canGoNext: Boolean,
    calendarState: AttendanceCalendarRequestState,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        CalendarMonthHeader(
            yearMonth = yearMonth,
            canGoNext = canGoNext,
            onPrevMonth = onPrevMonth,
            onNextMonth = onNextMonth,
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            WEEKDAY_LABELS.forEach { label ->
                Text(
                    text = label,
                    style = LocalAppTypography.current.labelLarge.medium,
                    color = Natural60,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (calendarState) {
            AttendanceCalendarRequestState.Loading -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            }

            is AttendanceCalendarRequestState.Error -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = calendarState.message,
                            style = LocalAppTypography.current.bodySmall.medium,
                            color = Natural60,
                        )

                        TextButton(onClick = onRetry) {
                            Text(
                                text = "다시 시도",
                                style = LocalAppTypography.current.bodySmall.bold,
                                color = Primary,
                            )
                        }
                    }
                }
            }

            is AttendanceCalendarRequestState.Success -> {
                CalendarGrid(
                    yearMonth = yearMonth,
                    calendar = calendarState.calendar,
                )
            }
        }
    }
}

@Composable
private fun CalendarMonthHeader(
    yearMonth: YearMonth,
    canGoNext: Boolean,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Primary95)
                .padding(horizontal = 14.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.KeyboardArrowLeft,
            contentDescription = "이전 달",
            tint = Primary,
            modifier =
                Modifier
                    .size(24.dp)
                    .clickable(onClick = onPrevMonth),
        )

        Text(
            text = "${yearMonth.year}년 ${yearMonth.monthValue}월",
            style = LocalAppTypography.current.titleMedium.bold,
            color = Primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )

        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = "다음 달",
            tint = if (canGoNext) Primary else Primary.copy(alpha = 0.3f),
            modifier =
                Modifier
                    .size(24.dp)
                    .clickable(enabled = canGoNext, onClick = onNextMonth),
        )
    }
}

private enum class DayCellState {
    ATTENDED,
    MISSED_PAST,
    TODAY,
    FUTURE,
}

@Composable
private fun CalendarGrid(
    yearMonth: YearMonth,
    calendar: AttendanceCalendar,
    modifier: Modifier = Modifier,
) {
    val today = LocalDate.now()
    val daysInMonth = yearMonth.lengthOfMonth()

    /*
     * 일요일 시작 기준으로 1일 앞의 빈 칸 개수 (일=0 ... 토=6)
     */
    val leadingBlanks = yearMonth.atDay(1).dayOfWeek.value % 7

    val cells: List<Int?> =
        buildList {
            repeat(leadingBlanks) { add(null) }
            for (day in 1..daysInMonth) add(day)
            while (size % 7 != 0) add(null)
        }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        cells.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                week.forEach { day ->
                    Box(modifier = Modifier.weight(1f)) {
                        if (day != null) {
                            DayCell(
                                day = day,
                                cellState =
                                    resolveDayCellState(
                                        date = yearMonth.atDay(day),
                                        today = today,
                                        attended = calendar.attendedDays.contains(day),
                                    ),
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun resolveDayCellState(
    date: LocalDate,
    today: LocalDate,
    attended: Boolean,
): DayCellState =
    when {
        attended -> DayCellState.ATTENDED
        date.isEqual(today) -> DayCellState.TODAY
        date.isBefore(today) -> DayCellState.MISSED_PAST
        else -> DayCellState.FUTURE
    }

@Composable
private fun DayCell(
    day: Int,
    cellState: DayCellState,
    modifier: Modifier = Modifier,
) {
    val backgroundColor =
        when (cellState) {
            DayCellState.ATTENDED -> Primary
            DayCellState.MISSED_PAST -> Natural95
            DayCellState.TODAY -> Natural100
            DayCellState.FUTURE -> Natural100
        }

    val textColor =
        when (cellState) {
            DayCellState.ATTENDED -> Natural100
            DayCellState.MISSED_PAST -> Natural50
            DayCellState.TODAY -> Primary
            DayCellState.FUTURE -> Natural70
        }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .then(
                    if (cellState == DayCellState.TODAY) {
                        Modifier.border(1.5.dp, Primary, RoundedCornerShape(12.dp))
                    } else {
                        Modifier
                    },
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = day.toString(),
            style = LocalAppTypography.current.bodySmall.bold,
            color = textColor,
        )
    }
}

@Composable
private fun CheckInButton(
    attendedToday: Boolean,
    isCheckingIn: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val enabled = !attendedToday && !isCheckingIn

    val label =
        when {
            attendedToday -> "출석 완료"
            isCheckingIn -> "출석체크 중..."
            else -> "출석 체크 하기"
        }

    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(percent = 50))
                .background(if (enabled) Natural10 else Natural90)
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 40.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = LocalAppTypography.current.titleSmall.bold,
            color = if (enabled) Natural100 else Natural50,
        )
    }
}
