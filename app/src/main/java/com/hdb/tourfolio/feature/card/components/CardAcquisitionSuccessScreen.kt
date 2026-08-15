@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.card.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.TourfolioTheme

@Composable
fun CardAcquisitionSuccessScreen(
    cardName: String,
    rarity: String,
    acquiredAt: String,
    cardId: Long,
    onCollectionClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor =
        Color(0xFF303030)

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    backgroundColor,
                )
                .padding(
                    horizontal = 22.dp,
                ),
        horizontalAlignment =
            Alignment.CenterHorizontally,
    ) {
        Spacer(
            modifier =
                Modifier.weight(
                    0.7f,
                ),
        )

        /*
         * TODO
         *
         * 다음 작업에서 실제 카드 이미지 UI로 교체합니다.
         */
        Box(
            modifier =
                Modifier
                    .width(285.dp)
                    .height(400.dp)
                    .background(
                        color =
                            Color(0xFF555555),
                        shape =
                            RoundedCornerShape(
                                22.dp,
                            ),
                    )
                    .border(
                        width = 2.dp,
                        color =
                            Primary.copy(
                                alpha = 0.8f,
                            ),
                        shape =
                            RoundedCornerShape(
                                22.dp,
                            ),
                    ),
        )

        Spacer(
            modifier =
                Modifier.height(
                    22.dp,
                ),
        )

        /*
         * 희귀도
         */
        Box(
            modifier =
                Modifier
                    .border(
                        width = 1.dp,
                        color = Primary,
                        shape =
                            RoundedCornerShape(
                                50,
                            ),
                    )
                    .padding(
                        horizontal = 18.dp,
                        vertical = 8.dp,
                    ),
            contentAlignment =
                Alignment.Center,
        ) {
            Text(
                text =
                    rarity.uppercase(),
                style =
                    LocalAppTypography
                        .current
                        .bodySmall
                        .bold
                        .copy(
                            color = Primary,
                        ),
            )
        }

        Spacer(
            modifier =
                Modifier.height(
                    18.dp,
                ),
        )

        /*
         * 카드 이름
         */
        Text(
            text =
                "${cardName} 카드를 획득했어요",
            style =
                LocalAppTypography
                    .current
                    .titleMedium
                    .bold
                    .copy(
                        color =
                            Natural100,
                    ),
            textAlign =
                TextAlign.Center,
        )

        Spacer(
            modifier =
                Modifier.height(
                    8.dp,
                ),
        )

        /*
         * 획득 API 응답 정보
         */
        Text(
            text =
                "$acquiredAt · ${cardId}번째 카드",
            style =
                LocalAppTypography
                    .current
                    .bodyLarge
                    .medium
                    .copy(
                        color =
                            Natural60,
                    ),
        )

        Spacer(
            modifier =
                Modifier.weight(
                    1f,
                ),
        )

        /*
         * 컬렉션에서 보기
         */
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(
                        58.dp,
                    )
                    .background(
                        color = Primary,
                        shape =
                            RoundedCornerShape(
                                10.dp,
                            ),
                    )
                    .clickable(
                        onClick =
                            onCollectionClick,
                    ),
            contentAlignment =
                Alignment.Center,
        ) {
            Text(
                text =
                    "컬렉션에서 보기",
                style =
                    LocalAppTypography
                        .current
                        .bodyLarge
                        .bold
                        .copy(
                            color =
                                Natural100,
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
         * 닫기
         */
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(
                        58.dp,
                    )
                    .border(
                        width = 1.dp,
                        color =
                            Natural60,
                        shape =
                            RoundedCornerShape(
                                10.dp,
                            ),
                    )
                    .clickable(
                        onClick =
                            onCloseClick,
                    ),
            contentAlignment =
                Alignment.Center,
        ) {
            Text(
                text =
                    "닫기",
                style =
                    LocalAppTypography
                        .current
                        .bodyLarge
                        .medium
                        .copy(
                            color =
                                Natural60,
                        ),
            )
        }

        Spacer(
            modifier =
                Modifier.height(
                    28.dp,
                ),
        )
    }
}

@Preview(
    name = "Card Acquisition Success",
    showBackground = true,
    widthDp = 393,
    heightDp = 852,
)
@Composable
private fun CardAcquisitionSuccessScreenPreview() {
    TourfolioTheme {
        CardAcquisitionSuccessScreen(
            cardName = "경회루",
            rarity = "LEGEND",
            acquiredAt = "2026.08.11",
            cardId = 3L,
            onCollectionClick = {},
            onCloseClick = {},
        )
    }
}