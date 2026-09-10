package com.hdb.tourfolio.feature.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Primary99
import java.util.Locale

@Composable
fun PointBalanceCard(
    pointBalance: Long,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = Primary99,
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(
                    horizontal = 20.dp,
                    vertical = 18.dp,
                ),
    ) {
        Text(
            text = "보유 포인트",
            style =
                LocalAppTypography.current.bodyLarge.medium.copy(
                    color = Natural60,
                ),
        )

        Spacer(
            modifier = Modifier.height(8.dp),
        )

        Text(
            text =
                String.format(
                    Locale.KOREA,
                    "%,dP",
                    pointBalance,
                ),
            style =
                LocalAppTypography.current.titleSmall.bold.copy(
                    color = Natural10,
                ),
        )
    }
}