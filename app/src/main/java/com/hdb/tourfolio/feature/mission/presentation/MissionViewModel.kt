package com.hdb.tourfolio.feature.mission.presentation

import com.hdb.tourfolio.core.mvi.MviEffect
import com.hdb.tourfolio.core.mvi.MviIntent
import com.hdb.tourfolio.core.mvi.MviState
import com.hdb.tourfolio.core.mvi.MviViewModel
import com.hdb.tourfolio.domain.mission.model.MissionClaimResult
import com.hdb.tourfolio.domain.mission.model.MissionOverview
import com.hdb.tourfolio.domain.mission.model.WeeklyAttendanceStatus
import com.hdb.tourfolio.domain.mission.usecase.CheckAttendanceUseCase
import com.hdb.tourfolio.domain.mission.usecase.ClaimCollectionMissionUseCase
import com.hdb.tourfolio.domain.mission.usecase.GetMissionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import java.time.LocalDate
import javax.inject.Inject

/*
 * 업적 화면 조회 상태
 */
sealed interface MissionRequestState {
    data object Loading : MissionRequestState

    data class Success(
        val overview: MissionOverview,
    ) : MissionRequestState

    data class Error(
        val message: String,
    ) : MissionRequestState
}

/*
 * Intent
 */
sealed interface MissionIntent : MviIntent {
    data object FetchMissions : MissionIntent

    data object CheckAttendance : MissionIntent

    data class ClaimMission(
        val missionId: Long,
    ) : MissionIntent
}

/*
 * State
 */
data class MissionState(
    val missionState: MissionRequestState =
        MissionRequestState.Loading,
    val isCheckingAttendance: Boolean = false,
    val claimingMissionIds: Set<Long> = emptySet(),
) : MviState

/*
 * 일회성 이벤트
 */
sealed interface MissionEffect : MviEffect {
    data class AttendanceChecked(
        val pointsAwarded: Int,
        val consecutiveDays: Int,
    ) : MissionEffect

    data class RewardClaimed(
        val pointsAwarded: Int,
    ) : MissionEffect

    data class Error(
        val message: String,
    ) : MissionEffect
}

@HiltViewModel
class MissionViewModel
    @Inject
    constructor(
        private val getMissionsUseCase: GetMissionsUseCase,
        private val checkAttendanceUseCase: CheckAttendanceUseCase,
        private val claimCollectionMissionUseCase: ClaimCollectionMissionUseCase,
    ) : MviViewModel<MissionIntent, MissionState, MissionEffect>(
            MissionState(),
        ) {
        override suspend fun handleIntent(intent: MissionIntent) {
            when (intent) {
                MissionIntent.FetchMissions ->
                    fetchMissions()

                MissionIntent.CheckAttendance ->
                    checkAttendance()

                is MissionIntent.ClaimMission ->
                    claimMission(intent.missionId)
            }
        }

        private suspend fun fetchMissions() {
            setState {
                copy(
                    missionState =
                        MissionRequestState.Loading,
                )
            }

            val result =
                try {
                    MissionRequestState.Success(
                        overview =
                            getMissionsUseCase(),
                    )
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    MissionRequestState.Error(
                        message =
                            e.message
                                ?: "업적 정보를 불러오지 못했습니다.",
                    )
                }

            setState {
                copy(
                    missionState = result,
                )
            }
        }

        /*
         * 로딩 화면 전환 없이 서버 최신값으로 갱신 (출석체크 직후 등)
         */
        private suspend fun refreshMissionsSilently() {
            val overview =
                try {
                    getMissionsUseCase()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    return
                }

            setState {
                copy(
                    missionState =
                        when (missionState) {
                            is MissionRequestState.Success ->
                                MissionRequestState.Success(overview = overview)

                            else -> missionState
                        },
                )
            }
        }

        private suspend fun checkAttendance() {
            val current = currentState.missionState
            if (current !is MissionRequestState.Success) return
            if (currentState.isCheckingAttendance) return
            if (current.overview.attendedToday) return

            setState { copy(isCheckingAttendance = true) }

            try {
                val result = checkAttendanceUseCase()

                setState {
                    copy(
                        isCheckingAttendance = false,
                        missionState =
                            when (val state = missionState) {
                                is MissionRequestState.Success ->
                                    MissionRequestState.Success(
                                        overview =
                                            state.overview.withAttendanceChecked(result.balance),
                                    )

                                else -> state
                            },
                    )
                }

                sendEffect(
                    MissionEffect.AttendanceChecked(
                        pointsAwarded = result.pointsAwarded,
                        consecutiveDays = result.consecutiveDays,
                    ),
                )

                refreshMissionsSilently()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                setState { copy(isCheckingAttendance = false) }

                sendEffect(
                    MissionEffect.Error(
                        message =
                            e.message
                                ?: "출석체크에 실패했습니다.",
                    ),
                )
            }
        }

        private suspend fun claimMission(missionId: Long) {
            if (missionId in currentState.claimingMissionIds) return

            setState { copy(claimingMissionIds = claimingMissionIds + missionId) }

            try {
                val result = claimCollectionMissionUseCase(missionId)

                setState {
                    copy(
                        claimingMissionIds = claimingMissionIds - missionId,
                        missionState =
                            when (val state = missionState) {
                                is MissionRequestState.Success ->
                                    MissionRequestState.Success(
                                        overview = state.overview.withMissionClaimed(result),
                                    )

                                else -> state
                            },
                    )
                }

                sendEffect(MissionEffect.RewardClaimed(pointsAwarded = result.pointsAwarded))

                refreshMissionsSilently()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                setState { copy(claimingMissionIds = claimingMissionIds - missionId) }

                sendEffect(
                    MissionEffect.Error(
                        message =
                            e.message
                                ?: "보상 수령에 실패했습니다.",
                    ),
                )
            }
        }
    }

/*
 * 출석체크 성공 시 서버 재조회 전까지 화면을 즉시 갱신하기 위한 로컬 반영
 */
private fun MissionOverview.withAttendanceChecked(newBalance: Long): MissionOverview {
    val todayIndex = LocalDate.now().dayOfWeek.value - 1

    val updatedAttendance =
        weeklyAttendance.mapIndexed { index, status ->
            if (index == todayIndex) WeeklyAttendanceStatus.ATTENDED else status
        }

    return copy(
        balance = newBalance,
        attendedToday = true,
        weeklyAttendance = updatedAttendance,
    )
}

/*
 * 보상 수령 성공 시 서버 재조회 전까지 화면을 즉시 갱신하기 위한 로컬 반영
 */
private fun MissionOverview.withMissionClaimed(result: MissionClaimResult): MissionOverview {
    val missions =
        missions.map { mission ->
            if (mission.id != result.missionId) return@map mission
            mission.copy(isCompleted = result.isCompleted, isClaimable = false)
        }

    return copy(
        balance = result.balance,
        missions = missions,
        inProgressCount = missions.count { !it.isCompleted },
        completedCount = missions.count { it.isCompleted },
    )
}
