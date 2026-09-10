package com.hdb.tourfolio.feature.notification.presentation.utils

import androidx.annotation.DrawableRes
import com.hdb.tourfolio.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun getNotificationTypeLabel(type: String): String =
    when (type) {
        "ATTENDANCE_POINT", "ATTENDANCE_STREAK" -> "출석체크"
        "CARD_ACQUIRED" -> "카드"
        "MISSION_COMPLETE" -> "포인트"
        "STOCK_TRADE" -> "주식"
        "LOCATION_PERMISSION" -> "위치"
        "SIGNUP_BONUS" -> "회원가입"
        else -> type
    }

/*
 * 알림 타입에 맞는 아이콘 반환
 */
@DrawableRes
fun getNotificationIconRes(type: String): Int? =
    when (getNotificationTypeLabel(type)) {
        "카드" ->
            R.drawable.ic_card_green

        "포인트" ->
            R.drawable.ic_point

        "주식" ->
            R.drawable.ic_stock_blue

        "출석체크" ->
            R.drawable.ic_clock_yellow

        "위치" ->
            R.drawable.ic_location

        "회원가입" ->
            R.drawable.ic_point

        else ->
            null
    }

/*
 * 서버 날짜:
 */
fun formatNotificationDate(createdAt: String): String =
    try {
        val dateTime =
            LocalDateTime.parse(
                createdAt,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            )

        "${dateTime.monthValue}월 ${dateTime.dayOfMonth}일"
    } catch (_: Exception) {
        createdAt
    }
