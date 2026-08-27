@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hdb.tourfolio.feature.explore.presentation.model.ExploreHubTrendingSpotUiModel
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural60

@Composable
fun SearchRecommendedSpotCard(
    item: ExploreHubTrendingSpotUiModel,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable {
                    onClick(item.id)
                },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.title,
            modifier =
                Modifier
                    .size(96.dp)
                    .clip(
                        RoundedCornerShape(10.dp),
                    ),
            contentScale = ContentScale.Crop,
        )

        Spacer(
            modifier = Modifier.width(12.dp),
        )

        Column {
            Text(
                text = item.title,
                style =
                    LocalAppTypography.current.bodyLarge.bold.copy(
                        color = Natural10,
                    ),
            )

            Spacer(
                modifier = Modifier.height(5.dp),
            )

            Text(
                text = item.location,
                style =
                    LocalAppTypography.current.bodySmall.medium.copy(
                        color = Natural60,
                    ),
            )
        }
    }
}
