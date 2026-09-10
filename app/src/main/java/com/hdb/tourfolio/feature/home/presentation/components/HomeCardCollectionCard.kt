@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural90
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.Primary99

@Composable
fun HomeCardCollectionCard(
    ownedCount: Int,
    totalCount: Int,
    collectionRate: Double,
    modifier: Modifier = Modifier,
) {
    val progress =
        (
            collectionRate / 100.0
        )
            .toFloat()
            .coerceIn(
                0f,
                1f,
            )

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = Primary99,
                    shape =
                        RoundedCornerShape(
                            14.dp,
                        ),
                )
                .padding(
                    start = 20.dp,
                    top = 18.dp,
                    end = 20.dp,
                    bottom = 18.dp,
                ),
    ) {
        Image(
            painter =
                painterResource(
                    id =
                        R.drawable.img_home_card_collection,
                ),
            contentDescription =
                "관광지 카드 수집",
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(
                        220.dp,
                    ),
            contentScale =
                ContentScale.Fit,
        )

        Spacer(
            modifier =
                Modifier.height(
                    12.dp,
                ),
        )

        Text(
            text = "관광지 카드 수집",
            style =
                LocalAppTypography
                    .current
                    .titleMedium
                    .bold
                    .copy(
                        color = Natural10,
                    ),
        )

        Spacer(
            modifier =
                Modifier.height(
                    4.dp,
                ),
        )

        Text(
            text =
                "관광지를 직접 방문하여 카드를 모아보세요!",
            style =
                LocalAppTypography
                    .current
                    .bodySmall
                    .medium
                    .copy(
                        color = Natural60,
                    ),
        )

        Spacer(
            modifier =
                Modifier.height(
                    20.dp,
                ),
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.Bottom,
            horizontalArrangement =
                Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = "보유 카드",
                    style =
                        LocalAppTypography
                            .current
                            .bodySmall
                            .medium
                            .copy(
                                color = Natural60,
                            ),
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            7.dp,
                        ),
                )

                Text(
                    text =
                        "$ownedCount / $totalCount",
                    style =
                        LocalAppTypography
                            .current
                            .titleMedium
                            .bold
                            .copy(
                                color = Natural10,
                            ),
                )
            }

            Text(
                text =
                    "${collectionRate.toInt()}%",
                style =
                    LocalAppTypography
                        .current
                        .titleMedium
                        .bold
                        .copy(
                            color = Primary,
                        ),
            )
        }

        Spacer(
            modifier =
                Modifier.height(
                    12.dp,
                ),
        )

        /*
         * Progress Bar
         */
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(
                        6.dp,
                    )
                    .background(
                        color = Natural90,
                        shape =
                            RoundedCornerShape(
                                50,
                            ),
                    ),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(
                            progress,
                        )
                        .height(
                            6.dp,
                        )
                        .background(
                            color = Primary,
                            shape =
                                RoundedCornerShape(
                                    50,
                                ),
                        ),
            )
        }

        Spacer(
            modifier =
                Modifier.height(
                    20.dp,
                ),
        )

        /*
         * 아직 획득하지 않은 카드 안내
         */
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        color =
                            Primary.copy(
                                alpha = 0.10f,
                            ),
                        shape =
                            RoundedCornerShape(
                                10.dp,
                            ),
                    )
                    .padding(
                        horizontal = 14.dp,
                        vertical = 14.dp,
                    ),
            contentAlignment =
                Alignment.Center,
        ) {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.spacedBy(
                        8.dp,
                    ),
            ) {
                Image(
                    painter =
                        painterResource(
                            id = R.drawable.ic_card,
                        ),
                    contentDescription = null,
                    modifier =
                        Modifier.size(
                            18.dp,
                        ),
                )

                Text(
                    text =
                        "아직 방문하지 않은 관광지 카드가 기다리고 있어요",
                    style =
                        LocalAppTypography
                            .current
                            .bodySmall
                            .medium
                            .copy(
                                color = Natural10,
                            ),
                )

                Image(
                    painter =
                        painterResource(
                            id = R.drawable.ic_card,
                        ),
                    contentDescription = null,
                    modifier =
                        Modifier.size(
                            18.dp,
                        ),
                )
            }
        }
    }
}
