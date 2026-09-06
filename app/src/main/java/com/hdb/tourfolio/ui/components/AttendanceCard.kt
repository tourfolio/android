@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural50
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural90
import com.hdb.tourfolio.ui.theme.Natural95
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.Primary99

enum class AttendanceDayState {
    CHECKED,
    MISSED,
    PENDING,
    BEFORE_SIGNUP,
}

data class AttendanceDay(
    val label: String,
    val state: AttendanceDayState,
)

@Composable
fun AttendanceCard(
    weekDays: List<AttendanceDay>,
    onCheckInClick: () -> Unit,
    modifier: Modifier = Modifier,
    onMoreClick: () -> Unit = {},
    attendedToday: Boolean = false,
    isCheckingIn: Boolean = false,
) {
    val checkInEnabled = !attendedToday && !isCheckingIn

    val checkInLabel =
        when {
            attendedToday -> "출석 완료"
            isCheckingIn -> "출석체크 중..."
            else -> "출석체크 하기"
        }

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onMoreClick),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "이번주 출석체크",
                style = LocalAppTypography.current.bodyLarge.bold,
                color = Natural10,
            )

            Image(
                painter = painterResource(id = R.drawable.ic_chevron_right_white),
                contentDescription = "출석체크 더보기",
                modifier = Modifier.size(8.dp),
                colorFilter = ColorFilter.tint(Natural50),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Primary99)
                    .padding(
                        horizontal = 22.dp,
                        vertical = 20.dp,
                    ),
        ) {
            Image(
                painter = painterResource(id = R.drawable.check),
                contentDescription = "출석체크",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                contentScale = ContentScale.Fit,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                weekDays.forEach { day ->
                    AttendanceDayItem(day = day)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (checkInEnabled) Primary else Natural90)
                        .clickable(
                            enabled = checkInEnabled,
                            onClick = onCheckInClick,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = checkInLabel,
                    style = LocalAppTypography.current.titleSmall.bold,
                    color = if (checkInEnabled) Natural100 else Natural50,
                )
            }
        }
    }
}

@Composable
private fun AttendanceDayItem(
    day: AttendanceDay,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when (day.state) {
                            AttendanceDayState.CHECKED -> Primary
                            AttendanceDayState.MISSED -> Natural95
                            AttendanceDayState.PENDING -> Natural100
                            AttendanceDayState.BEFORE_SIGNUP -> Natural95
                        },
                    )
                    .then(
                        if (day.state == AttendanceDayState.PENDING) {
                            Modifier.border(1.dp, Natural90, CircleShape)
                        } else {
                            Modifier
                        },
                    ),
            contentAlignment = Alignment.Center,
        ) {
            when (day.state) {
                AttendanceDayState.CHECKED -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "출석 완료",
                        tint = Natural100,
                        modifier = Modifier.size(16.dp),
                    )
                }

                AttendanceDayState.MISSED -> {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "출석 실패",
                        tint = Natural50,
                        modifier = Modifier.size(16.dp),
                    )
                }

                AttendanceDayState.PENDING -> {}

                AttendanceDayState.BEFORE_SIGNUP -> {}
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = day.label,
            style = LocalAppTypography.current.labelLarge.medium,
            color = Natural60,
        )
    }
}
