package com.hdb.tourfolio.data.notification.remote.dto

data class NotificationResponseDto(
    val newNotifications: List<NotificationItemDto>,
    val pastNotifications: List<NotificationItemDto>,
)

data class NotificationItemDto(
    val id: Long,
    val type: String,
    val message: String,
    val createdAt: String,
)