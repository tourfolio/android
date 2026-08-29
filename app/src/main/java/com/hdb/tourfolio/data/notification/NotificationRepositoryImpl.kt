package com.hdb.tourfolio.data.notification

import com.hdb.tourfolio.data.notification.mapper.toDomain
import com.hdb.tourfolio.data.notification.remote.NotificationApiService
import com.hdb.tourfolio.domain.notification.model.NotificationList
import com.hdb.tourfolio.domain.notification.repository.NotificationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl
    @Inject
    constructor(
        private val notificationApiService: NotificationApiService,
    ) : NotificationRepository {
        override suspend fun getNotifications(): NotificationList =
            notificationApiService
                .getNotifications()
                .toDomain()
    }
