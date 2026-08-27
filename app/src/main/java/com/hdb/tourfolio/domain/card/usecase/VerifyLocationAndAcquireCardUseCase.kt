package com.hdb.tourfolio.domain.card.usecase

import com.hdb.tourfolio.domain.card.model.CardAcquisition
import com.hdb.tourfolio.domain.card.repository.CardRepository
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

sealed interface CardAcquireResult {
    data class Acquired(
        val acquisition: CardAcquisition,
    ) : CardAcquireResult

    data class TooFar(
        val spotName: String,
        val distanceMeters: Double,
    ) : CardAcquireResult
}

class VerifyLocationAndAcquireCardUseCase
    @Inject
    constructor(
        private val cardRepository: CardRepository,
    ) {
        suspend operator fun invoke(
            cardId: Long,
            userLatitude: Double,
            userLongitude: Double,
        ): CardAcquireResult {
            val location = cardRepository.getCardLocation(cardId)
            val distanceMeters =
                haversineDistanceMeters(
                    startLatitude = userLatitude,
                    startLongitude = userLongitude,
                    endLatitude = location.latitude,
                    endLongitude = location.longitude,
                )

            if (distanceMeters > CARD_ACQUIRE_DISTANCE_METERS) {
                return CardAcquireResult.TooFar(
                    spotName = location.spotName,
                    distanceMeters = distanceMeters,
                )
            }

            return CardAcquireResult.Acquired(cardRepository.acquireCard(cardId))
        }
    }

private const val CARD_ACQUIRE_DISTANCE_METERS = 200.0
private const val EARTH_RADIUS_METERS = 6371000.0

private fun haversineDistanceMeters(
    startLatitude: Double,
    startLongitude: Double,
    endLatitude: Double,
    endLongitude: Double,
): Double {
    val dLat = Math.toRadians(endLatitude - startLatitude)
    val dLng = Math.toRadians(endLongitude - startLongitude)
    val a =
        sin(dLat / 2).pow(2) +
            cos(Math.toRadians(startLatitude)) * cos(Math.toRadians(endLatitude)) * sin(dLng / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return EARTH_RADIUS_METERS * c
}
