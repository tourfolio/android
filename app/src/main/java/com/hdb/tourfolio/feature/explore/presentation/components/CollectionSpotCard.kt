@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.ui.components.SpotImageOverlay
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100

@Composable
fun CollectionSpotCard(
    title: String,
    imageUrl: String,
    hasImage: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SpotImageOverlay(
        hasImage = hasImage,
        model = imageUrl,
        contentDescription = title,
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick,
                ),
    ) {
        Text(
            text =
            title,
            style =
                LocalAppTypography
                    .current
                    .bodySmall
                    .bold
                    .copy(
                        color =
                        Natural100,
                    ),
            modifier =
                Modifier
                    .padding(
                        horizontal = 16.dp,
                        vertical = 14.dp,
                    ),
        )
    }
}
