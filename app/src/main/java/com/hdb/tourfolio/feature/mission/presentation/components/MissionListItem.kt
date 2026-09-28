@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.mission.presentation.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.domain.mission.model.Mission
import com.hdb.tourfolio.domain.mission.model.MissionCategory
import com.hdb.tourfolio.feature.mission.presentation.label
import com.hdb.tourfolio.ui.theme.Amber
import com.hdb.tourfolio.ui.theme.Blue
import com.hdb.tourfolio.ui.theme.Green
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural90
import com.hdb.tourfolio.ui.theme.Natural95
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.TourfolioTheme

@Composable
fun MissionListItem(
    mission: Mission,
    modifier: Modifier = Modifier,
    isClaiming: Boolean = false,
    onClaim: () -> Unit = {},
) {
    val iconRes =
        when (mission.category) {
            MissionCategory.VISIT -> R.drawable.ic_location
            MissionCategory.COLLECTION -> R.drawable.ic_card_green
            MissionCategory.TRADE -> R.drawable.ic_stock_blue
            MissionCategory.ATTENDANCE -> R.drawable.ic_clock_yellow
            MissionCategory.ETC -> R.drawable.ic_location
        }

    val accentColor =
        when (mission.category) {
            MissionCategory.VISIT -> Primary
            MissionCategory.COLLECTION -> Green
            MissionCategory.TRADE -> Blue
            MissionCategory.ATTENDANCE -> Amber
            MissionCategory.ETC -> Primary
        }

    val claimButtonBackground =
        when (mission.category) {
            MissionCategory.ATTENDANCE -> AttendanceClaimBackground
            MissionCategory.TRADE -> TradeClaimBackground
            else -> accentColor.copy(alpha = 0.16f)
        }

    val progress =
        if (mission.conditionTarget <= 0) {
            if (mission.isCompleted) 1f else 0f
        } else {
            (mission.currentProgress.toFloat() / mission.conditionTarget.toFloat())
                .coerceIn(0f, 1f)
        }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Natural100)
                .border(1.dp, Natural90, RoundedCornerShape(16.dp))
                .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Natural95),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = mission.category.label,
                    modifier = Modifier.size(26.dp),
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = mission.category.label,
                        style = LocalAppTypography.current.bodySmall.medium,
                        color = Natural60,
                        modifier = Modifier.weight(1f),
                    )

                    when {
                        mission.isClaimable -> Unit

                        mission.isCompleted -> {
                            MissionCompletedBadge(accentColor = accentColor)
                        }

                        else -> {
                            MissionRewardBadge(
                                rewardPoints = mission.rewardPoints,
                                accentColor = accentColor,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = mission.title,
                    style = LocalAppTypography.current.titleSmall.bold,
                    color = Natural10,
                )
            }
        }

        when {
            mission.isClaimable -> {
                Spacer(modifier = Modifier.height(20.dp))

                MissionClaimButton(
                    isClaiming = isClaiming,
                    accentColor = accentColor,
                    backgroundColor = claimButtonBackground,
                    onClick = onClaim,
                )
            }

            mission.isCompleted -> {
                // 완료된 미션은 진행률 표시가 필요 없음
            }

            else -> {
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "진행률",
                        style = LocalAppTypography.current.bodySmall.medium,
                        color = Natural60,
                    )

                    Text(
                        text = "${mission.currentProgress} / ${mission.conditionTarget}",
                        style = LocalAppTypography.current.bodySmall.medium,
                        color = Natural60,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(Natural90),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth(progress)
                                .height(8.dp)
                                .clip(RoundedCornerShape(percent = 50))
                                .background(accentColor),
                    )
                }
            }
        }
    }
}

@Composable
private fun MissionRewardBadge(
    rewardPoints: Int,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(percent = 50))
                .background(accentColor.copy(alpha = 0.12f))
                .padding(horizontal = 14.dp, vertical = 6.dp),
    ) {
        Text(
            text = "${rewardPoints}P",
            style = LocalAppTypography.current.bodySmall.bold,
            color = accentColor,
        )
    }
}

@Composable
private fun MissionCompletedBadge(
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(percent = 50))
                .background(accentColor.copy(alpha = 0.12f))
                .padding(horizontal = 14.dp, vertical = 6.dp),
    ) {
        Text(
            text = "완료",
            style = LocalAppTypography.current.bodySmall.bold,
            color = accentColor,
        )
    }
}

private val AttendanceClaimBackground = Color(0xFFFEF5E7)
private val TradeClaimBackground = Color(0xFFE7EAFD)

@Composable
private fun MissionClaimButton(
    isClaiming: Boolean,
    accentColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(backgroundColor)
                .clickable(enabled = !isClaiming, onClick = onClick)
                .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (isClaiming) {
            CircularProgressIndicator(
                color = accentColor,
                strokeWidth = 2.dp,
                modifier = Modifier.size(18.dp),
            )
        } else {
            Text(
                text = "완료하기",
                style = LocalAppTypography.current.bodyLarge.bold,
                color = accentColor,
            )
        }
    }
}

@Preview(
    name = "Mission List Item States",
    showBackground = true,
    widthDp = 412,
)
@Composable
private fun MissionListItemPreview() {
    TourfolioTheme(
        dynamicColor = false,
    ) {
        Column(
            modifier =
                Modifier
                    .background(Natural95)
                    .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // 진행 중
            MissionListItem(
                mission =
                    Mission(
                        id = 1L,
                        category = MissionCategory.VISIT,
                        title = "강릉 바다 3곳 방문하기",
                        rewardPoints = 300,
                        currentProgress = 1,
                        conditionTarget = 3,
                        isCompleted = false,
                    ),
            )

            // 목표 달성 + 보상 수령 가능 (완료하기 버튼)
            MissionListItem(
                mission =
                    Mission(
                        id = 2L,
                        category = MissionCategory.ATTENDANCE,
                        title = "2일 연속 출석체크",
                        rewardPoints = 100,
                        currentProgress = 2,
                        conditionTarget = 2,
                        isCompleted = false,
                        isClaimable = true,
                    ),
            )

            // 보상 수령 요청 중 (로딩)
            MissionListItem(
                mission =
                    Mission(
                        id = 3L,
                        category = MissionCategory.COLLECTION,
                        title = "카드 3개 수집하기",
                        rewardPoints = 300,
                        currentProgress = 3,
                        conditionTarget = 3,
                        isCompleted = false,
                        isClaimable = true,
                    ),
                isClaiming = true,
            )

            // 보상 수령 완료
            MissionListItem(
                mission =
                    Mission(
                        id = 4L,
                        category = MissionCategory.COLLECTION,
                        title = "첫 발도장",
                        rewardPoints = 100,
                        currentProgress = 1,
                        conditionTarget = 1,
                        isCompleted = true,
                    ),
            )
        }
    }
}
