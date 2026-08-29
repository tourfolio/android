package com.hdb.tourfolio.domain.notification.usecase

import com.hdb.tourfolio.domain.notification.model.NotificationList
import com.hdb.tourfolio.domain.notification.repository.NotificationRepository
import javax.inject.Inject

class GetNotificationsUseCase
@Inject
constructor(
    private val notificationRepository: NotificationRepository,
) {
    suspend operator fun invoke(): NotificationList =
        notificationRepository.getNotifications()
}