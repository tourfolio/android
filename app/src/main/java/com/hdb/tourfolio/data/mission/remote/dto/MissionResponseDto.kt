package com.hdb.tourfolio.data.mission.remote.dto

data class MissionResponseDto(
    val balance: Long,
    val weeklyAttendance: List<String>,
    val attendedToday: Boolean,
    val inProgressCount: Int,
    val completedCount: Int,
    val missions: List<MissionItemDto>,
)

data class MissionItemDto(
    val missionId: Long,
    val category: String,
    val title: String,
    val rewardPoints: Int,
    val currentProgress: Int,
    val conditionTarget: Int,
    val isCompleted: Boolean,
)
