@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.mission.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdb.tourfolio.domain.mission.model.Mission
import com.hdb.tourfolio.domain.mission.model.MissionCategory
import com.hdb.tourfolio.domain.mission.model.MissionOverview
import com.hdb.tourfolio.domain.mission.model.WeeklyAttendanceStatus
import com.hdb.tourfolio.feature.mission.presentation.components.MissionListItem
import com.hdb.tourfolio.feature.mission.presentation.components.MissionTabBar
import com.hdb.tourfolio.feature.point.presentation.PointHistoryBottomSheet
import com.hdb.tourfolio.feature.trade.presentation.components.TradeCategoryChip
import com.hdb.tourfolio.ui.components.AttendanceCard
import com.hdb.tourfolio.ui.components.AttendanceDay
import com.hdb.tourfolio.ui.components.AttendanceDayState
import com.hdb.tourfolio.ui.components.CommonHeader
import com.hdb.tourfolio.ui.components.HoldingPointCard
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.TourfolioTheme
import java.time.LocalDate

@Composable
fun MissionScreen(
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onAttendanceHistoryClick: () -> Unit = {},
    viewModel: MissionViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MissionEffect.AttendanceChecked -> {
                    Toast
                        .makeText(
                            context,
                            "출석 완료! ${effect.pointsAwarded}P 적립 (${effect.consecutiveDays}일 연속)",
                            Toast.LENGTH_SHORT,
                        )
                        .show()
                }

                is MissionEffect.Error -> {
                    Toast
                        .makeText(context, effect.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    when (val missionState = state.missionState) {
        MissionRequestState.Loading -> {
            MissionLoading()
        }

        is MissionRequestState.Error -> {
            MissionError(
                message = missionState.message,
                onRetryClick = {
                    viewModel.processIntent(MissionIntent.FetchMissions)
                },
            )
        }

        is MissionRequestState.Success -> {
            MissionScreenContent(
                overview = missionState.overview,
                isCheckingAttendance = state.isCheckingAttendance,
                onCheckInClick = {
                    viewModel.processIntent(MissionIntent.CheckAttendance)
                },
                onAttendanceHistoryClick = onAttendanceHistoryClick,
                onProfileClick = onProfileClick,
                onNotificationClick = onNotificationClick,
            )
        }
    }
}

@Composable
private fun MissionScreenContent(
    overview: MissionOverview,
    isCheckingAttendance: Boolean,
    onCheckInClick: () -> Unit,
    onAttendanceHistoryClick: () -> Unit,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable {
        mutableStateOf(MissionTab.IN_PROGRESS)
    }

    var selectedCategory by rememberSaveable {
        mutableStateOf<MissionCategory?>(null)
    }

    var showPointHistory by rememberSaveable {
        mutableStateOf(false)
    }

    val weekDays =
        remember(overview.weeklyAttendance) {
            buildAttendanceDays(overview.weeklyAttendance)
        }

    val filteredMissions =
        remember(selectedTab, selectedCategory, overview.missions) {
            overview.missions
                .filter { mission ->
                    when (selectedTab) {
                        MissionTab.IN_PROGRESS -> !mission.isCompleted
                        MissionTab.COMPLETED -> mission.isCompleted
                    }
                }
                .filter { mission ->
                    selectedCategory == null || mission.category == selectedCategory
                }
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Natural100),
    ) {
        CommonHeader(
            onProfileClick = onProfileClick,
            onNotificationClick = onNotificationClick,
            modifier =
                Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 18.dp,
                ),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                HoldingPointCard(
                    point = overview.balance,
                    onClick = { showPointHistory = true },
                    modifier = Modifier.padding(horizontal = 22.dp),
                )
            }

            item {
                AttendanceCard(
                    weekDays = weekDays,
                    onCheckInClick = onCheckInClick,
                    onMoreClick = onAttendanceHistoryClick,
                    attendedToday = overview.attendedToday,
                    isCheckingIn = isCheckingAttendance,
                    modifier = Modifier.padding(horizontal = 22.dp),
                )
            }

            item {
                MissionTabBar(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        selectedTab = tab
                    },
                    inProgressCount = overview.missions.count { mission -> !mission.isCompleted },
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            item {
                MissionCategoryFilterRow(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { category ->
                        selectedCategory = category
                    },
                    modifier =
                        Modifier.padding(
                            top = 4.dp,
                            start = 22.dp,
                            end = 22.dp,
                        ),
                )
            }

            if (filteredMissions.isEmpty()) {
                item {
                    EmptyMissions(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 60.dp),
                    )
                }
            } else {
                items(
                    items = filteredMissions,
                    key = { mission -> mission.id },
                ) { mission ->
                    MissionListItem(
                        mission = mission,
                        modifier = Modifier.padding(horizontal = 22.dp),
                    )
                }
            }
        }
    }

    if (showPointHistory) {
        PointHistoryBottomSheet(
            onDismissRequest = { showPointHistory = false },
        )
    }
}

@Composable
private fun MissionCategoryFilterRow(
    selectedCategory: MissionCategory?,
    onCategorySelected: (MissionCategory?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(missionCategoryFilters) { category ->
            TradeCategoryChip(
                label = category.label,
                selected = selectedCategory == category,
                onClick = {
                    onCategorySelected(category)
                },
            )
        }
    }
}

@Composable
private fun EmptyMissions(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "표시할 업적이 없습니다.",
            style = LocalAppTypography.current.bodyLarge.medium,
            color = Natural60,
        )
    }
}

@Composable
private fun MissionLoading(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Natural100),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Primary)
    }
}

@Composable
private fun MissionError(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Natural100),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "업적 정보를 불러오지 못했습니다.",
                style = LocalAppTypography.current.bodyLarge.bold,
                color = Natural10,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = LocalAppTypography.current.bodySmall.medium,
                color = Natural60,
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

private val ATTENDANCE_DAY_LABELS = listOf("월", "화", "수", "목", "금", "토", "일")

/*
 * weeklyAttendance 는 월요일부터 일요일까지의 출석 상태(7개)를 서버가 그대로 내려줌
 * 단, 오늘은 출석 전이어도 MISSED(X)로 보여주지 않고 아직 오지 않은 날처럼 표시
 */
private fun buildAttendanceDays(weeklyAttendance: List<WeeklyAttendanceStatus>): List<AttendanceDay> {
    val todayIndex = LocalDate.now().dayOfWeek.value - 1

    return ATTENDANCE_DAY_LABELS.mapIndexed { index, label ->
        val status = weeklyAttendance.getOrNull(index)

        val dayState =
            when {
                status == WeeklyAttendanceStatus.MISSED && index == todayIndex -> AttendanceDayState.PENDING
                status == WeeklyAttendanceStatus.ATTENDED -> AttendanceDayState.CHECKED
                status == WeeklyAttendanceStatus.MISSED -> AttendanceDayState.MISSED
                status == WeeklyAttendanceStatus.BEFORE_SIGNUP -> AttendanceDayState.BEFORE_SIGNUP
                else -> AttendanceDayState.PENDING
            }

        AttendanceDay(label = label, state = dayState)
    }
}

@Preview(
    name = "Mission Screen Preview",
    showBackground = true,
    widthDp = 412,
    heightDp = 915,
)
@Composable
private fun MissionScreenPreview() {
    TourfolioTheme(
        dynamicColor = false,
    ) {
        MissionScreenContent(
            overview =
                MissionOverview(
                    balance = 50_000L,
                    weeklyAttendance =
                        listOf(
                            WeeklyAttendanceStatus.ATTENDED,
                            WeeklyAttendanceStatus.ATTENDED,
                            WeeklyAttendanceStatus.ATTENDED,
                            WeeklyAttendanceStatus.MISSED,
                            WeeklyAttendanceStatus.FUTURE,
                            WeeklyAttendanceStatus.FUTURE,
                            WeeklyAttendanceStatus.FUTURE,
                        ),
                    attendedToday = false,
                    inProgressCount = 3,
                    completedCount = 1,
                    missions =
                        listOf(
                            Mission(
                                id = 1L,
                                category = MissionCategory.VISIT,
                                title = "관광지 5곳 방문하기",
                                rewardPoints = 300,
                                currentProgress = 1,
                                conditionTarget = 3,
                                isCompleted = false,
                            ),
                            Mission(
                                id = 2L,
                                category = MissionCategory.COLLECTION,
                                title = "포토카드 3장 모으기",
                                rewardPoints = 300,
                                currentProgress = 1,
                                conditionTarget = 3,
                                isCompleted = false,
                            ),
                        ),
                ),
            isCheckingAttendance = false,
            onCheckInClick = {},
            onAttendanceHistoryClick = {},
            onProfileClick = {},
            onNotificationClick = {},
        )
    }
}
