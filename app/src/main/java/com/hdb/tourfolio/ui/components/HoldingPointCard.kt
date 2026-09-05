@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural50
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Primary99
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HoldingPointCard(
    point: Long,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Primary99)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(onClick = onClick)
                    } else {
                        Modifier
                    },
                )
                .padding(
                    horizontal = 22.dp,
                    vertical = 20.dp,
                ),
    ) {
        LabelWithChevron(
            text = "보유 포인트",
            contentDescription = "포인트 내역 보기",
        )

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = "${formatHoldingPoint(point)}P",
            style = LocalAppTypography.current.titleSmall.bold,
            color = Natural10,
        )
    }
}

@Composable
private fun LabelWithChevron(
    text: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = LocalAppTypography.current.bodyLarge.medium,
            color = Natural60,
        )

        Spacer(modifier = Modifier.width(4.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_chevron_right_white),
            contentDescription = contentDescription,
            modifier = Modifier.size(8.dp),
            colorFilter = ColorFilter.tint(Natural50),
        )
    }
}

private fun formatHoldingPoint(value: Long): String =
    NumberFormat
        .getNumberInstance(Locale.KOREA)
        .format(value)
