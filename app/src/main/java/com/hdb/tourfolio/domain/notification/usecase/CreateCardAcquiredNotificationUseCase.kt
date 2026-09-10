package com.hdb.tourfolio.domain.notification.usecase

import com.hdb.tourfolio.domain.notification.repository.NotificationRepository
import javax.inject.Inject

class CreateCardAcquiredNotificationUseCase
@Inject
constructor(
    private val notificationRepository: NotificationRepository,
) {
    suspend operator fun invoke(cardName: String) {
        notificationRepository.createCardAcquiredNotification(
            cardName = cardName,
        )
    }
}