package com.hdb.tourfolio.feature.attendance.presentation

import com.hdb.tourfolio.core.mvi.MviEffect
import com.hdb.tourfolio.core.mvi.MviIntent
import com.hdb.tourfolio.core.mvi.MviState
import com.hdb.tourfolio.core.mvi.MviViewModel
import com.hdb.tourfolio.domain.mission.model.AttendanceCalendar
import com.hdb.tourfolio.domain.mission.usecase.CheckAttendanceUseCase
import com.hdb.tourfolio.domain.mission.usecase.GetAttendanceCalendarUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import java.time.YearMonth
import javax.inject.Inject

/*
 * 월간 출석 달력 조회 상태
 */
sealed interface AttendanceCalendarRequestState {
    data object Loading : AttendanceCalendarRequestState

    data class Success(
        val calendar: AttendanceCalendar,
    ) : AttendanceCalendarRequestState

    data class Error(
        val message: String,
    ) : AttendanceCalendarRequestState
}

sealed interface MonthlyAttendanceIntent : MviIntent {
    data object Load : MonthlyAttendanceIntent

    data object PrevMonth : MonthlyAttendanceIntent

    data object NextMonth : MonthlyAttendanceIntent

    data object CheckAttendance : MonthlyAttendanceIntent
}

data class MonthlyAttendanceState(
    val yearMonth: YearMonth = YearMonth.now(),
    val calendarState: AttendanceCalendarRequestState =
        AttendanceCalendarRequestState.Loading,
    val isCheckingAttendance: Boolean = false,
) : MviState {
    /*
     * 현재 달 이후로는 이동 불가
     */
    val canGoNext: Boolean
        get() = yearMonth < YearMonth.now()

    val isCurrentMonth: Boolean
        get() = yearMonth == YearMonth.now()
}

sealed interface MonthlyAttendanceEffect : MviEffect {
    data class AttendanceChecked(
        val pointsAwarded: Int,
        val consecutiveDays: Int,
    ) : MonthlyAttendanceEffect

    data class Error(
        val message: String,
    ) : MonthlyAttendanceEffect
}

@HiltViewModel
class MonthlyAttendanceViewModel
    @Inject
    constructor(
        private val getAttendanceCalendarUseCase: GetAttendanceCalendarUseCase,
        private val checkAttendanceUseCase: CheckAttendanceUseCase,
    ) : MviViewModel<MonthlyAttendanceIntent, MonthlyAttendanceState, MonthlyAttendanceEffect>(
            MonthlyAttendanceState(),
        ) {
        init {
            processIntent(MonthlyAttendanceIntent.Load)
        }

        override suspend fun handleIntent(intent: MonthlyAttendanceIntent) {
            when (intent) {
                MonthlyAttendanceIntent.Load ->
                    fetchCalendar()

                MonthlyAttendanceIntent.PrevMonth -> {
                    setState { copy(yearMonth = yearMonth.minusMonths(1)) }
                    fetchCalendar()
                }

                MonthlyAttendanceIntent.NextMonth -> {
                    if (currentState.canGoNext) {
                        setState { copy(yearMonth = yearMonth.plusMonths(1)) }
                        fetchCalendar()
                    }
                }

                MonthlyAttendanceIntent.CheckAttendance ->
                    checkAttendance()
            }
        }

        private suspend fun fetchCalendar(showLoading: Boolean = true) {
            val target = currentState.yearMonth

            if (showLoading) {
                setState {
                    copy(calendarState = AttendanceCalendarRequestState.Loading)
                }
            }

            val result =
                try {
                    AttendanceCalendarRequestState.Success(
                        calendar =
                            getAttendanceCalendarUseCase(
                                year = target.year,
                                month = target.monthValue,
                            ),
                    )
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    AttendanceCalendarRequestState.Error(
                        message =
                            e.message
                                ?: "출석 달력을 불러오지 못했습니다.",
                    )
                }

            /*
             * 무음 갱신 중에는 기존 화면을 유지하고 실패는 무시
             */
            if (!showLoading && result is AttendanceCalendarRequestState.Error) return

            setState { copy(calendarState = result) }
        }

        private suspend fun checkAttendance() {
            if (currentState.isCheckingAttendance) return

            setState { copy(isCheckingAttendance = true) }

            try {
                val result = checkAttendanceUseCase()

                sendEffect(
                    MonthlyAttendanceEffect.AttendanceChecked(
                        pointsAwarded = result.pointsAwarded,
                        consecutiveDays = result.consecutiveDays,
                    ),
                )

                setState { copy(isCheckingAttendance = false) }

                fetchCalendar(showLoading = false)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                setState { copy(isCheckingAttendance = false) }

                sendEffect(
                    MonthlyAttendanceEffect.Error(
                        message =
                            e.message
                                ?: "출석체크에 실패했습니다.",
                    ),
                )
            }
        }
    }
