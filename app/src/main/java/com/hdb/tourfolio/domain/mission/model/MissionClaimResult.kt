package com.hdb.tourfolio.domain.mission.model

data class MissionClaimResult(
    val missionId: Long,
    val isCompleted: Boolean,
    val alreadyRewarded: Boolean,
    val pointsAwarded: Int,
    val balance: Long,
)
