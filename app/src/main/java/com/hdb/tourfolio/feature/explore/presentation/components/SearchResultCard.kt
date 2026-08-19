@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.explore.presentation.model.ExploreSearchSpotUiModel
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Primary70

@Composable
fun SearchResultCard(
    item: ExploreSearchSpotUiModel,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier.clickable {
                onClick(item.id)
            },
        verticalAlignment =
            Alignment.Top,
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.title,
            modifier =
                Modifier
                    .size(
                        width = 142.dp,
                        height = 142.dp,
                    )
                    .clip(
                        RoundedCornerShape(10.dp),
                    ),
            contentScale = ContentScale.Crop,
        )

        Spacer(
            modifier = Modifier.width(12.dp),
        )

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = item.title,
                style =
                    LocalAppTypography.current.bodyLarge.bold.copy(
                        color = Natural10,
                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(
                modifier = Modifier.height(8.dp),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter =
                        painterResource(
                            id = R.drawable.ic_location,
                        ),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )

                Spacer(
                    modifier = Modifier.width(5.dp),
                )

                Text(
                    text = item.address,
                    style =
                        LocalAppTypography.current.bodySmall.medium.copy(
                            color = Natural60,
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp),
            )

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(6.dp),
            ) {
                item.tags
                    .take(3)
                    .forEach { tag ->
                        SearchResultTag(
                            text = tag,
                        )
                    }
            }
        }
    }
}

@Composable
private fun SearchResultTag(text: String) {
    androidx.compose.foundation.layout.Box(
        modifier =
            Modifier
                .background(
                    color = Primary70,
                    shape = RoundedCornerShape(8.dp),
                )
                .padding(
                    horizontal = 11.dp,
                    vertical = 9.dp,
                ),
    ) {
        Text(
            text = "#$text",
            style =
                LocalAppTypography.current.bodySmall.bold.copy(
                    color = Natural100,
                ),
            maxLines = 1,
        )
    }
}
