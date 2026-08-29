package com.hdb.tourfolio.data.notification.remote

import com.hdb.tourfolio.data.common.network.Authenticated
import com.hdb.tourfolio.data.notification.remote.dto.NotificationResponseDto
import retrofit2.http.GET

interface NotificationApiService {
    @Authenticated
    @GET("api/v1/notifications")
    suspend fun getNotifications(): NotificationResponseDto
}