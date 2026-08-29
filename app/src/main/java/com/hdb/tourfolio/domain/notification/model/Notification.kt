package com.hdb.tourfolio.domain.notification.model

data class Notification(
    val id: Long,
    val type: String,
    val message: String,
    val createdAt: String,
)

data class NotificationList(
    val newNotifications: List<Notification>,
    val pastNotifications: List<Notification>,
)
