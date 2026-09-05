package com.hdb.tourfolio.domain.mission.usecase

import com.hdb.tourfolio.domain.mission.model.MissionOverview
import com.hdb.tourfolio.domain.mission.repository.MissionRepository
import javax.inject.Inject

class GetMissionsUseCase
    @Inject
    constructor(
        private val missionRepository: MissionRepository,
    ) {
        suspend operator fun invoke(): MissionOverview = missionRepository.getMissions()
    }
