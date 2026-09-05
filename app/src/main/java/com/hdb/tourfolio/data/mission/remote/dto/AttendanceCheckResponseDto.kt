package com.hdb.tourfolio.data.mission.remote.dto

data class AttendanceCheckResponseDto(
    val pointsAwarded: Int,
    val balance: Long,
    val consecutiveDays: Int,
)
