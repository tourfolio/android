package com.hdb.tourfolio.domain.mission.usecase

import com.hdb.tourfolio.domain.mission.model.MissionClaimResult
import com.hdb.tourfolio.domain.mission.repository.MissionRepository
import javax.inject.Inject

class ClaimCollectionMissionUseCase
    @Inject
    constructor(
        private val missionRepository: MissionRepository,
    ) {
        suspend operator fun invoke(missionId: Long): MissionClaimResult =
            missionRepository
                .claimCollectionMission(missionId)
    }
