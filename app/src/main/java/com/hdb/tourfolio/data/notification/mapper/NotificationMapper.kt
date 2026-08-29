package com.hdb.tourfolio.data.notification.mapper

import com.hdb.tourfolio.data.notification.remote.dto.NotificationItemDto
import com.hdb.tourfolio.data.notification.remote.dto.NotificationResponseDto
import com.hdb.tourfolio.domain.notification.model.Notification
import com.hdb.tourfolio.domain.notification.model.NotificationList

fun NotificationItemDto.toDomain(): Notification =
    Notification(
        id = id,
        type = type,
        message = message,
        createdAt = createdAt,
    )

fun NotificationResponseDto.toDomain(): NotificationList =
    NotificationList(
        newNotifications =
            newNotifications.map { notification ->
                notification.toDomain()
            },
        pastNotifications =
            pastNotifications.map { notification ->
                notification.toDomain()
            },
    )