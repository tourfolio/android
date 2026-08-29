@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.mypage.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural90

data class MyPageMenuItem(
    val title: String,
    val onClick: () -> Unit,
)

@Composable
fun MyPageMenuCard(
    title: String,
    @DrawableRes iconRes: Int,
    items: List<MyPageMenuItem>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = Natural100,
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(
                    horizontal = 20.dp,
                    vertical = 20.dp,
                ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.spacedBy(
                    10.dp,
                ),
        ) {
            Image(
                painter =
                    painterResource(
                        id = iconRes,
                    ),
                contentDescription = null,
                modifier =
                    Modifier.size(
                        26.dp,
                    ),
            )

            Text(
                text = title,
                style =
                    LocalAppTypography
                        .current
                        .bodyLarge
                        .bold
                        .copy(
                            color = Natural10,
                        ),
            )
        }

        Spacer(
            modifier =
                Modifier.height(
                    18.dp,
                ),
        )

        Divider(
            color = Natural90,
        )

        items.forEach { item ->
            Text(
                text = item.title,
                style =
                    LocalAppTypography
                        .current
                        .bodyLarge
                        .medium
                        .copy(
                            color = Natural60,
                        ),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable(
                            onClick = item.onClick,
                        )
                        .padding(
                            vertical = 16.dp,
                        ),
            )
        }
    }
}
