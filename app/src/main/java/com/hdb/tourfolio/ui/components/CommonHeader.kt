@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100

enum class CommonHeaderType {
    DEFAULT,
    SEARCH,
}

@Composable
fun CommonHeader(
    type: CommonHeaderType = CommonHeaderType.DEFAULT,
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val isSearchHeader =
        type == CommonHeaderType.SEARCH

    val contentColor =
        if (isSearchHeader) {
            Natural100
        } else {
            Natural10
        }

    Row(
        modifier =
            modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Tourfolio",
            style =
                LocalAppTypography
                    .current
                    .headlineLarge
                    .heavy
                    .copy(
                        color = contentColor,
                    ),
        )

        when (type) {
            CommonHeaderType.DEFAULT -> {
                Row(
                    verticalAlignment =
                        Alignment.CenterVertically,
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            4.dp,
                        ),
                ) {
                    CommonHeaderIconButton(
                        iconRes =
                            R.drawable.ic_user_black,
                        contentDescription =
                            "내 정보",
                        onClick =
                            onProfileClick,
                    )

                    CommonHeaderIconButton(
                        iconRes =
                            R.drawable.ic_bell_black,
                        contentDescription =
                            "알림",
                        onClick =
                            onNotificationClick,
                    )
                }
            }

            CommonHeaderType.SEARCH -> {
                CommonHeaderIconButton(
                    iconRes =
                        R.drawable.ic_search,
                    contentDescription =
                        "탐색",
                    onClick =
                        onSearchClick,
                    iconSize = 30.dp,
                )
            }
        }
    }
}

@Composable
private fun CommonHeaderIconButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: androidx.compose.ui.unit.Dp = 25.dp,
) {
    Box(
        modifier =
            modifier
                .size(
                    40.dp,
                )
                .clickable(
                    onClick = onClick,
                ),
        contentAlignment =
            Alignment.Center,
    ) {
        Image(
            painter =
                painterResource(
                    id = iconRes,
                ),
            contentDescription =
                contentDescription,
            modifier =
                Modifier.size(
                    iconSize,
                ),
        )
    }
}