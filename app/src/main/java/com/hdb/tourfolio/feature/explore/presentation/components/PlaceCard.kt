@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.ui.components.SpotImageOverlay
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.TourfolioTheme

@Composable
fun PlaceCard(
    title: String,
    places: Int,
    imageUrl: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    SpotImageOverlay(
        hasImage = imageUrl.isNotBlank(),
        model = imageUrl,
        contentDescription = title,
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick,
                ),
    ) {
        Box(
            modifier =
                Modifier.fillMaxWidth(),
        ) {
            /*
             * 컬렉션 정보
             */
            Column(
                modifier =
                    Modifier
                        .padding(
                            start = 16.dp,
                            end = 52.dp,
                            top = 16.dp,
                            bottom = 16.dp,
                        ),
            ) {
                Text(
                    text = title,
                    style =
                        LocalAppTypography.current
                            .bodyLarge
                            .bold,
                    color = Natural100,
                )

                Text(
                    text = "$places places",
                    style =
                        LocalAppTypography.current
                            .bodySmall
                            .medium,
                    color = Natural100,
                    modifier =
                        Modifier.padding(
                            top = 6.dp,
                        ),
                )
            }

            /*
             * 컬렉션 상세 이동 화살표
             */
            Image(
                painter =
                    painterResource(
                        id = R.drawable.ic_chevron_right_white,
                    ),
                contentDescription = "컬렉션 상세 보기",
                modifier =
                    Modifier
                        .align(
                            Alignment.CenterEnd,
                        )
                        .padding(
                            end = 16.dp,
                        )
                        .size(
                            24.dp,
                        ),
            )
        }
    }
}

@Preview(
    name = "Place Card Preview",
    showBackground = true,
    widthDp = 412,
)
@Composable
private fun PlaceCardPreview() {
    TourfolioTheme {
        PlaceCard(
            title =
                "대한민국 유네스코 세계문화유산",
            places =
                26,
            imageUrl =
                "",
            modifier =
                Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 20.dp,
                ),
        )
    }
}