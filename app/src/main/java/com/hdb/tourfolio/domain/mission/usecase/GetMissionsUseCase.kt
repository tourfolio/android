package com.hdb.tourfolio.domain.mission.usecase

import com.hdb.tourfolio.domain.card.repository.CardRepository
import com.hdb.tourfolio.domain.mission.model.MissionCategory
import com.hdb.tourfolio.domain.mission.model.MissionOverview
import com.hdb.tourfolio.domain.mission.repository.MissionRepository
import kotlinx.coroutines.CancellationException
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
                var balance = overview.balance
                var pendingClaims = false
                val missions =
                    overview.missions.map { mission ->
                        if (mission.category != MissionCategory.COLLECTION) return@map mission
                        if (mission.isCompleted) return@map mission.copy(currentProgress = mission.conditionTarget)

                        val progress = ownedCount.coerceIn(0, mission.conditionTarget.coerceAtLeast(0))
                        val localMission = mission.copy(currentProgress = progress)
                        // IDs in the API documentation are examples; use the ID returned by the server.
                        val allowed = collectionTargets[mission.title] == mission.conditionTarget
                        if (!allowed || progress < mission.conditionTarget) return@map localMission

                        try {
                            val result = missionRepository.claimCollectionMission(mission.id)
                            check(result.missionId == mission.id && result.isCompleted) {
                                "수집 업적 완료를 확인하지 못했습니다."
                            }
                            // The server balance is authoritative, including alreadyRewarded responses.
                            balance = result.balance
                            localMission.copy(isCompleted = result.isCompleted)
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            pendingClaims = true
                            localMission
                        }
                    }
                overview.copy(
                    balance = balance,
                    missions = missions,
                    inProgressCount = missions.count { !it.isCompleted },
                    completedCount = missions.count { it.isCompleted },
                    hasPendingCollectionClaims = pendingClaims,
                )
            }
    }

private val collectionTargets = mapOf("첫 발도장" to 1, "길 위의 사람" to 3, "대한민국 정복" to 6)
