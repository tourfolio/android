@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.trade.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R

@Composable
fun TradeHeader(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 18.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Image(
            painter =
                painterResource(
                    id = R.drawable.ic_tourfolio_logo_black,
                ),
            contentDescription = "Tourfolio",
            modifier =
                Modifier
                    .width(128.dp)
                    .height(37.dp),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(22.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter =
                    painterResource(
                        id = R.drawable.ic_bell_black,
                    ),
                contentDescription = "알림",
                modifier =
                    Modifier
                        .size(24.dp)
                        .clickable {
                            onNotificationClick()
                        },
            )
        }
    }
}
