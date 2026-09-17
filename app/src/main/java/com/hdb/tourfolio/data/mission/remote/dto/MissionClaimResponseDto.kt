package com.hdb.tourfolio.data.mission.remote.dto

data class MissionClaimResponseDto(
    val missionId: Long,
    val isCompleted: Boolean,
    val alreadyRewarded: Boolean,
    val pointsAwarded: Int,
    val balance: Long,
)
