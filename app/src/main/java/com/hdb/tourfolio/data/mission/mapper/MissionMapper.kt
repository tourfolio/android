package com.hdb.tourfolio.data.mission.mapper

import com.hdb.tourfolio.data.mission.remote.dto.AttendanceCalendarResponseDto
import com.hdb.tourfolio.data.mission.remote.dto.AttendanceCheckResponseDto
import com.hdb.tourfolio.data.mission.remote.dto.MissionItemDto
import com.hdb.tourfolio.data.mission.remote.dto.MissionResponseDto
import com.hdb.tourfolio.domain.mission.model.AttendanceCalendar
import com.hdb.tourfolio.domain.mission.model.AttendanceCheckResult
import com.hdb.tourfolio.domain.mission.model.Mission
import com.hdb.tourfolio.domain.mission.model.MissionCategory
import com.hdb.tourfolio.domain.mission.model.MissionOverview
import com.hdb.tourfolio.domain.mission.model.WeeklyAttendanceStatus
import java.time.LocalDate

fun MissionResponseDto.toDomain(): MissionOverview =
    MissionOverview(
        balance = balance,
        weeklyAttendance = weeklyAttendance.map { WeeklyAttendanceStatus.from(it) },
        attendedToday = attendedToday,
        inProgressCount = inProgressCount,
        completedCount = completedCount,
        missions =
            missions.map { mission ->
                mission.toDomain()
            },
    )

fun AttendanceCalendarResponseDto.toDomain(): AttendanceCalendar =
    AttendanceCalendar(
        year = year,
        month = month,
        attendedCount = attendedCount,
        attendedDays =
            attendedDates
                .mapNotNull { date ->
                    runCatching { LocalDate.parse(date).dayOfMonth }.getOrNull()
                }
                .toSet(),
    )

fun AttendanceCheckResponseDto.toDomain(): AttendanceCheckResult =
    AttendanceCheckResult(
        pointsAwarded = pointsAwarded,
        balance = balance,
        consecutiveDays = consecutiveDays,
    )

private fun MissionItemDto.toDomain(): Mission =
    Mission(
        id = missionId,
        category = MissionCategory.from(category),
        title = title,
        rewardPoints = rewardPoints,
        currentProgress = currentProgress,
        conditionTarget = conditionTarget,
        isCompleted = isCompleted,
    )
