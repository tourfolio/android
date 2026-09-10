package com.hdb.tourfolio.domain.notification.repository

import com.hdb.tourfolio.domain.notification.model.NotificationList

interface NotificationRepository {
    suspend fun getNotifications(): NotificationList

    suspend fun createLocationPermissionNotification()

    suspend fun createCardAcquiredNotification(cardName: String)
}
