package com.hdb.tourfolio.feature.notification.presentation.utils

import androidx.annotation.DrawableRes
import com.hdb.tourfolio.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/*
 * 알림 타입에 맞는 아이콘 반환
 */
@DrawableRes
fun getNotificationIconRes(type: String): Int? =
    when (type) {
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
