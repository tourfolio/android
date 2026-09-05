@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.mission.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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

@Composable
fun MissionListItem(
    mission: Mission,
    modifier: Modifier = Modifier,
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
                Text(
                    text = mission.category.label,
                    style = LocalAppTypography.current.bodySmall.medium,
                    color = Natural60,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = mission.title,
                    style = LocalAppTypography.current.titleSmall.bold,
                    color = Natural10,
                )
            }

            MissionRewardBadge(
                rewardPoints = mission.rewardPoints,
                accentColor = accentColor,
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

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
