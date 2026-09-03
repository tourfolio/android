@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100

@Composable
fun CollectionSpotCard(
    title: String,
    imageUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(
                    0.92f,
                )
                .clip(
                    RoundedCornerShape(
                        10.dp,
                    ),
                )
                .clickable(
                    onClick = onClick,
                ),
    ) {
        AsyncImage(
            model =
            imageUrl,
            contentDescription =
            title,
            modifier =
                Modifier.matchParentSize(),
            contentScale =
                ContentScale.Crop,
        )

        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .background(
                        brush =
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        Color.Transparent,
                                        Color.Transparent,
                                        Color.Black.copy(
                                            alpha = 0.72f,
                                        ),
                                    ),
                            ),
                    ),
        )

        Text(
            text =
            title,
            style =
                LocalAppTypography
                    .current
                    .titleSmall
                    .bold
                    .copy(
                        color =
                        Natural100,
                    ),
            modifier =
                Modifier
                    .align(
                        Alignment.BottomStart,
                    )
                    .padding(
                        horizontal = 16.dp,
                        vertical = 14.dp,
                    ),
        )
    }
}
