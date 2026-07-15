@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.trade.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural70
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.TourfolioTheme

@Composable
fun TradeCategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier =
            modifier
                .height(31.dp)
                .widthIn(min = 40.dp)
                .clickable(onClick = onClick),
        shape = RoundedCornerShape(26.dp),
        color = Natural100,
        border =
            BorderStroke(
                width = 1.dp,
                color =
                    if (selected) {
                        Primary
                    } else {
                        Natural70
                    },
            ),
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 13.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = LocalAppTypography.current.labelLarge.bold,
                color =
                    if (selected) {
                        Primary
                    } else {
                        Natural70
                    },
            )
        }
    }
}

@Preview(
    name = "Trade Category Chips",
    showBackground = true,
)
@Composable
private fun TradeCategoryChipPreview() {
    TourfolioTheme(dynamicColor = false) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement =
                androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
        ) {
            TradeCategoryChip(
                label = "전체",
                selected = true,
                onClick = {},
            )

            TradeCategoryChip(
                label = "역사",
                selected = false,
                onClick = {},
            )
        }
    }
}
