@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.explore.presentation.model.ExploreSearchSpotUiModel
import com.hdb.tourfolio.ui.components.SpotImage
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
            modifier.fillMaxWidth().clickable {
                onClick(item.id)
            },
        verticalAlignment =
            Alignment.Top,
    ) {
        SpotImage(
            hasImage = item.hasImage,
            model = item.imageUrl,
            contentDescription = item.title,
            modifier =
                Modifier
                    .size(
                        width = 120.dp,
                        height = 120.dp,
                    ),
            matchImageAspectRatio = false,
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
                modifier = Modifier.height(4.dp),
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
                modifier = Modifier.height(10.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
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
