package com.hdb.tourfolio.data.mission.remote

import com.hdb.tourfolio.data.common.network.Authenticated
import com.hdb.tourfolio.data.mission.remote.dto.AttendanceCalendarResponseDto
import com.hdb.tourfolio.data.mission.remote.dto.AttendanceCheckResponseDto
import com.hdb.tourfolio.data.mission.remote.dto.MissionResponseDto
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MissionApiService {
    @Authenticated
    @GET("api/v1/missions")
    suspend fun getMissions(): MissionResponseDto

    @Authenticated
    @POST("api/v1/attendance/check")
    suspend fun checkAttendance(): AttendanceCheckResponseDto

    @Authenticated
    @GET("api/v1/attendance/calendar")
    suspend fun getAttendanceCalendar(
        @Query("year") year: Int,
        @Query("month") month: Int,
    ): AttendanceCalendarResponseDto
}
