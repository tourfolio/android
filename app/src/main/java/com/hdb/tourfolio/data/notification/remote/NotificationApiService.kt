package com.hdb.tourfolio.data.notification.remote

import com.hdb.tourfolio.data.common.network.Authenticated
import com.hdb.tourfolio.data.notification.remote.dto.CardAcquiredNotificationRequestDto
import com.hdb.tourfolio.data.notification.remote.dto.NotificationResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NotificationApiService {
    @Authenticated
    @GET("api/v1/notifications")
    suspend fun getNotifications(): NotificationResponseDto

    @Authenticated
    @POST("api/v1/notifications/location-permission")
    suspend fun createLocationPermissionNotification()

    @Authenticated
    @POST("api/v1/notifications/card-acquired")
    suspend fun createCardAcquiredNotification(
        @Body request: CardAcquiredNotificationRequestDto,
    )
}
