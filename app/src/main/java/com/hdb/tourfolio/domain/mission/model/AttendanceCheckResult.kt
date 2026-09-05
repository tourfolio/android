package com.hdb.tourfolio.domain.mission.model

/*
 * 출석체크 결과
 */
data class AttendanceCheckResult(
    val pointsAwarded: Int,
    val balance: Long,
    val consecutiveDays: Int,
)

/*
 * 월간 출석 달력
 * attendedDays 는 해당 월에 출석한 "일(day of month)" 목록
 */
data class AttendanceCalendar(
    val year: Int,
    val month: Int,
    val attendedCount: Int,
    val attendedDays: Set<Int>,
)
