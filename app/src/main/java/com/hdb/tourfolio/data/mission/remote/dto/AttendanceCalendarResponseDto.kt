package com.hdb.tourfolio.data.mission.remote.dto

data class AttendanceCalendarResponseDto(
    val year: Int,
    val month: Int,
    val attendedCount: Int,
    val attendedDates: List<String>,
)
