@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.card.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hdb.tourfolio.R
import com.hdb.tourfolio.feature.card.mock.CardDetailUiModel

@Composable
fun ExpandedImageScreen(
    card: CardDetailUiModel,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    Color.Black,
                ),
    ) {
        /*
         * 카드 이미지
         */
        if (!card.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = card.imageUrl,
                contentDescription = card.title,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = 18.dp,
                            vertical = 70.dp,
                        ),
                contentScale = ContentScale.Fit,
            )
        } else if (card.imageRes != null) {
            Image(
                painter =
                    painterResource(
                        id = card.imageRes,
                    ),
                contentDescription = card.title,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = 18.dp,
                            vertical = 70.dp,
                        ),
                contentScale = ContentScale.Fit,
            )
        }

        /*
         * 닫기 버튼
         */
        Box(
            modifier =
                Modifier
                    .align(
                        Alignment.TopStart,
                    )
                    .padding(
                        start = 20.dp,
                        top = 22.dp,
                    )
                    .size(46.dp)
                    .background(
                        color =
                            Color.Black.copy(
                                alpha = 0.35f,
                            ),
                        shape =
                            RoundedCornerShape(
                                50,
                            ),
                    )
                    .clickable(
                        onClick = onCloseClick,
                    ),
            contentAlignment =
                Alignment.Center,
        ) {
            Image(
                painter =
                    painterResource(
                        id =
                            R.drawable
                                .ic_chevron_right_white,
                    ),
                contentDescription =
                    "확대 이미지 닫기",
                modifier =
                    Modifier
                        .size(24.dp)
                        .rotate(
                            180f,
                        ),
            )
        }
    }
}
