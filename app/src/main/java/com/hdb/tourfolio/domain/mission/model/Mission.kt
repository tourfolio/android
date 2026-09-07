package com.hdb.tourfolio.domain.mission.model

/*
 * 업적 화면 전체 조회 결과
 */
data class MissionOverview(
    val balance: Long,
    val weeklyAttendance: List<WeeklyAttendanceStatus>,
    val attendedToday: Boolean,
    val inProgressCount: Int,
    val completedCount: Int,
    val missions: List<Mission>,
)

enum class WeeklyAttendanceStatus {
    ATTENDED,
    MISSED,
    FUTURE,
    BEFORE_SIGNUP,
    ;

    companion object {
        fun from(raw: String?): WeeklyAttendanceStatus =
            when (raw?.trim()?.uppercase()) {
                "ATTENDED" -> ATTENDED
                "MISSED" -> MISSED
                "BEFORE_SIGNUP" -> BEFORE_SIGNUP
                else -> FUTURE
            }
    }
}

data class Mission(
    val id: Long,
    val category: MissionCategory,
    val title: String,
    val rewardPoints: Int,
    val currentProgress: Int,
    val conditionTarget: Int,
    val isCompleted: Boolean,
)

enum class MissionCategory {
    VISIT,
    COLLECTION,
    TRADE,
    ATTENDANCE,
    ETC,
    ;

    companion object {
        fun from(raw: String?): MissionCategory =
            when (raw?.trim()?.uppercase()) {
                "VISIT" -> VISIT
                "COLLECT" -> COLLECTION
                "INVEST" -> TRADE
                "ATTENDANCE" -> ATTENDANCE
                else -> ETC
            }
    }
}
