package com.hdb.tourfolio.domain.notification.usecase

import com.hdb.tourfolio.domain.notification.repository.NotificationRepository
import javax.inject.Inject

class CreateLocationPermissionNotificationUseCase
    @Inject
    constructor(
        private val notificationRepository: NotificationRepository,
    ) {
        suspend operator fun invoke() {
            notificationRepository.createLocationPermissionNotification()
        }
    }
