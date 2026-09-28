package com.hdb.tourfolio.domain.mission.usecase

import com.hdb.tourfolio.domain.card.repository.CardRepository
import com.hdb.tourfolio.domain.mission.model.MissionCategory
import com.hdb.tourfolio.domain.mission.model.MissionOverview
import com.hdb.tourfolio.domain.mission.repository.MissionRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMissionsUseCase
    @Inject
    constructor(
        private val missionRepository: MissionRepository,
        private val cardRepository: CardRepository,
    ) {
        private val mutex = Mutex()

        suspend operator fun invoke(): MissionOverview =
            mutex.withLock {
                val overview = missionRepository.getMissions()
                val ownedCount = cardRepository.getCollection().ownedCount
                val missions =
                    overview.missions.map { mission ->
                        if (mission.category != MissionCategory.COLLECTION) return@map mission
                        if (mission.isCompleted) return@map mission.copy(currentProgress = mission.conditionTarget)

                        val progress = ownedCount.coerceIn(0, mission.conditionTarget.coerceAtLeast(0))
                        // IDs in the API documentation are examples; use the ID returned by the server.
                        val allowed = collectionTargets[mission.title] == mission.conditionTarget
                        mission.copy(
                            currentProgress = progress,
                            isClaimable = allowed && progress >= mission.conditionTarget,
                        )
                    }
                overview.copy(
                    missions = missions,
                    inProgressCount = missions.count { !it.isCompleted },
                    completedCount = missions.count { it.isCompleted },
                )
            }
    }

private val collectionTargets = mapOf("첫 발도장" to 1, "길 위의 사람" to 3, "대한민국 정복" to 6)
