@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.trade.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.ui.theme.Blue
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural50
import com.hdb.tourfolio.ui.theme.Natural99
import com.hdb.tourfolio.ui.theme.Red

@Composable
fun RankedStockCard(
    title: String,
    priceText: String,
    changeText: String,
    changeType: PriceChangeType,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val changeColor =
        when (changeType) {
            PriceChangeType.RISE -> Red
            PriceChangeType.FALL -> Blue
            PriceChangeType.UNCHANGED -> Natural50
        }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Natural99)
                .clickable(onClick = onClick)
                .padding(horizontal = 22.dp, vertical = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = LocalAppTypography.current.bodyLarge.bold,
                color = Natural10,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = priceText,
                style = LocalAppTypography.current.bodyLarge.bold,
                color = Natural10,
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = changeText,
            style = LocalAppTypography.current.bodySmall.bold,
            color = changeColor,
            modifier = Modifier.align(Alignment.End),
        )
    }
}
