@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.TourfolioTheme

@Composable
fun RegionCard(
    id: Long,
    title: String,
    regionName: String,
    @DrawableRes imageRes: Int,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .width(180.dp)
                .height(245.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                    onClick(id)
                },
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        brush =
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.65f),
                                    ),
                            ),
                    ),
        )

        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(
                        start = 18.dp,
                        end = 18.dp,
                        bottom = 20.dp,
                    ),
        ) {
            Text(
                text = title,
                style =
                    LocalAppTypography.current.titleMedium.bold.copy(
                        color = Natural100,
                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = regionName,
                style =
                    LocalAppTypography.current.bodyLarge.medium.copy(
                        color = Natural100.copy(alpha = 0.85f),
                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(
    name = "Region Card Preview",
    showBackground = true,
    widthDp = 220,
    heightDp = 290,
)
@Composable
private fun RegionCardPreview() {
    TourfolioTheme {
        RegionCard(
            id = 6L,
            title = "흰여울길",
            regionName = "부산",
            imageRes = R.drawable.bg_huinnyeoul_demo,
            onClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}
