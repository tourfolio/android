package com.hdb.tourfolio.domain.mission.usecase

import com.hdb.tourfolio.domain.card.model.CardAcquisition
import com.hdb.tourfolio.domain.card.model.CardCollection
import com.hdb.tourfolio.domain.card.model.CardDetail
import com.hdb.tourfolio.domain.card.model.CardLocation
import com.hdb.tourfolio.domain.card.repository.CardRepository
import com.hdb.tourfolio.domain.mission.model.AttendanceCalendar
import com.hdb.tourfolio.domain.mission.model.AttendanceCheckResult
import com.hdb.tourfolio.domain.mission.model.Mission
import com.hdb.tourfolio.domain.mission.model.MissionCategory
import com.hdb.tourfolio.domain.mission.model.MissionClaimResult
import com.hdb.tourfolio.domain.mission.model.MissionOverview
import com.hdb.tourfolio.domain.mission.repository.MissionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GetMissionsUseCaseTest {
    @Test
    fun localProgressAndServerCompletionAreCombined() =
        runBlocking {
            val repository = FakeMissions(listOf(mission(1, "길 위의 사람", 3), mission(2, "대한민국 정복", 6, true)))
            val result = GetMissionsUseCase(repository, FakeCards(2))()
            assertEquals(listOf(2, 6), result.missions.map { it.currentProgress })
            assertEquals(listOf(false, true), result.missions.map { it.isCompleted })
            assertTrue(repository.claims.isEmpty())
        }

    @Test
    fun onlyAllowedCollectMissionsAreClaimedAndServerBalanceIsUsed() =
        runBlocking {
            val visit = mission(3, "첫 발도장", 1).copy(category = MissionCategory.VISIT)
            val repository = FakeMissions(listOf(mission(71, "첫 발도장", 1), mission(72, "길 위의 사람", 3), visit, mission(4, "다른 수집", 1)))
            repository.claim = { MissionClaimResult(it, true, true, 0, 5100) }
            val result = GetMissionsUseCase(repository, FakeCards(3))()
            assertEquals(listOf(71L, 72L), repository.claims)
            assertEquals(5100L, result.balance)
            assertEquals(2, result.completedCount)
            assertEquals(2, result.inProgressCount)
            assertFalse(result.hasPendingCollectionClaims)
            assertEquals(visit, result.missions[2])
        }

    @Test
    fun failedClaimPreservesProgressAndRetriesWithoutBlockingOtherClaims() =
        runBlocking {
            val repository = FakeMissions(listOf(mission(1, "첫 발도장", 1), mission(2, "길 위의 사람", 3)))
            repository.claim = {
                if (it == 1L) error("connection lost")
                MissionClaimResult(it, true, false, 300, 5300)
            }
            val useCase = GetMissionsUseCase(repository, FakeCards(3))
            val failed = useCase()
            assertTrue(failed.hasPendingCollectionClaims)
            assertEquals(1, failed.missions.first().currentProgress)
            assertFalse(failed.missions.first().isCompleted)
            assertTrue(failed.missions.last().isCompleted)
            repository.overview = failed
            repository.claim = { MissionClaimResult(it, true, true, 0, 5400) }
            val retried = useCase()
            assertEquals(listOf(1L, 2L, 1L), repository.claims)
            assertFalse(retried.hasPendingCollectionClaims)
            assertEquals(5400L, retried.balance)
        }

    @Test(expected = CancellationException::class)
    fun cancellationIsPropagated() {
        runBlocking {
            val repository = FakeMissions(listOf(mission(1, "첫 발도장", 1)))
            repository.claim = { throw CancellationException() }
            GetMissionsUseCase(repository, FakeCards(1))()
        }
    }

    @Test
    fun mismatchedClaimResponseDoesNotCompleteMission() =
        runBlocking {
            val repository = FakeMissions(listOf(mission(1, "첫 발도장", 1)))
            repository.claim = { MissionClaimResult(999, true, false, 100, 5100) }
            val result = GetMissionsUseCase(repository, FakeCards(1))()
            assertFalse(result.missions.single().isCompleted)
            assertTrue(result.hasPendingCollectionClaims)
            assertEquals(5000L, result.balance)
        }
}

private fun mission(
    id: Long,
    title: String,
    target: Int,
    completed: Boolean = false,
) = Mission(id, MissionCategory.COLLECTION, title, 100, 0, target, completed)

private class FakeMissions(missions: List<Mission>) : MissionRepository {
    var overview = MissionOverview(5000, emptyList(), false, missions.size, 0, missions)
    val claims = mutableListOf<Long>()
    var claim: (Long) -> MissionClaimResult = { MissionClaimResult(it, true, false, 100, 5100) }

    override suspend fun getMissions() = overview

    override suspend fun claimCollectionMission(missionId: Long): MissionClaimResult {
        claims.add(missionId)
        return claim(missionId)
    }

    override suspend fun checkAttendance(): AttendanceCheckResult = error("unused")

    override suspend fun getAttendanceCalendar(
        year: Int,
        month: Int,
    ): AttendanceCalendar = error("unused")
}

private class FakeCards(private val owned: Int) : CardRepository {
    override suspend fun getCollection(
        region: String?,
        theme: String?,
        rarity: String?,
    ): CardCollection {
        assertEquals(null, region)
        assertEquals(null, theme)
        assertEquals(null, rarity)
        return CardCollection(0.0, owned, 6, 6, emptyList())
    }

    override suspend fun getCardDetail(cardId: Long): CardDetail = error("unused")

    override suspend fun getCardLocation(cardId: Long): CardLocation = error("unused")

    override suspend fun acquireCard(cardId: Long): CardAcquisition = error("unused")
}
