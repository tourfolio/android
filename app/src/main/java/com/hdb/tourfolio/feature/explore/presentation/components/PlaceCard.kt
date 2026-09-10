@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hdb.tourfolio.R
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.TourfolioTheme

private val PlaceCardHeight = 124.dp
private val PlaceCardButtonSize = 45.dp
private val PlaceCardButtonEndInset = 2.dp
private val PlaceCardButtonVerticalOffset = 6.dp

private class PlaceCardCutoutShape(
    private val cornerRadiusDp: Float = 12f,
    private val buttonRadiusDp: Float = PlaceCardButtonSize.value / 2f,
    private val buttonEndInsetDp: Float = PlaceCardButtonEndInset.value,
    private val buttonVerticalOffsetDp: Float = PlaceCardButtonVerticalOffset.value,
    private val cutoutGapDp: Float = 6f,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val cornerRadiusPx =
            with(density) {
                cornerRadiusDp.dp.toPx()
            }

        val cutoutRadiusPx =
            with(density) {
                (buttonRadiusDp + cutoutGapDp).dp.toPx()
            }

        val cutoutCenter =
            Offset(
                x =
                    size.width -
                        with(density) {
                            (buttonEndInsetDp + buttonRadiusDp).dp.toPx()
                        },
                y =
                    size.height -
                        with(density) {
                            (buttonRadiusDp - buttonVerticalOffsetDp).dp.toPx()
                        },
            )

        val roundedRect =
            Path().apply {
                addOutline(
                    Outline.Rounded(
                        RoundRect(
                            rect =
                                Rect(
                                    Offset.Zero,
                                    size,
                                ),
                            cornerRadius =
                                CornerRadius(
                                    cornerRadiusPx,
                                ),
                        ),
                    ),
                )
            }

        val cutout =
            Path().apply {
                addOval(
                    Rect(
                        center =
                        cutoutCenter,
                        radius =
                        cutoutRadiusPx,
                    ),
                )
            }

        val bottomEndCorner =
            Path().apply {
                addRect(
                    Rect(
                        left =
                            cutoutCenter.x,
                        top =
                            cutoutCenter.y,
                        right =
                            size.width,
                        bottom =
                            size.height,
                    ),
                )
            }

        val completeCutout =
            Path().apply {
                op(
                    cutout,
                    bottomEndCorner,
                    PathOperation.Union,
                )
            }

        val result =
            Path().apply {
                op(
                    roundedRect,
                    completeCutout,
                    PathOperation.Difference,
                )
            }

        return Outline.Generic(
            result,
        )
    }
}

@Composable
fun PlaceCard(
    title: String,
    places: Int,
    imageUrl: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val cardShape =
        PlaceCardCutoutShape()

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(
                    PlaceCardHeight,
                )
                .clickable(
                    onClick =
                    onClick,
                ),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .clip(
                        cardShape,
                    ),
        ) {
            /*
             * API thumbnailUrl 이미지
             */
            AsyncImage(
                model = imageUrl,
                contentDescription =
                title,
                modifier =
                    Modifier.fillMaxSize(),
                contentScale =
                    ContentScale.Crop,
            )

            /*
             * 좌 → 우 그라데이션
             * 텍스트 가독성 확보
             */
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            brush =
                                Brush.horizontalGradient(
                                    colors =
                                        listOf(
                                            Color.Black.copy(alpha = 0.56f),
                                            Color.Black.copy(alpha = 0.28f),
                                            Color.Black.copy(alpha = 0.06f),
                                        ),
                                ),
                        ),
            )

            /*
             * 아래쪽 그라데이션
             */
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
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.45f),
                                        ),
                                ),
                        ),
            )

            /*
             * 제목 + 관광지 개수
             */
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(
                        6.dp,
                    ),
                modifier =
                    Modifier
                        .align(
                            Alignment.BottomStart,
                        )
                        .padding(
                            start = 16.dp,
                            end = 76.dp,
                            bottom = 16.dp,
                        ),
            ) {
                Text(
                    text =
                    title,
                    style =
                        LocalAppTypography
                            .current
                            .bodyLarge
                            .bold
                            .copy(
                                color =
                                Natural100,
                            ),
                    maxLines =
                    1,
                    overflow =
                        TextOverflow.Ellipsis,
                )

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            5.dp,
                        ),
                    verticalAlignment =
                        Alignment.CenterVertically,
                ) {
                    Image(
                        painter =
                            painterResource(
                                id =
                                    R.drawable.ic_location,
                            ),
                        contentDescription =
                        null,
                        modifier =
                            Modifier.size(
                                22.dp,
                            ),
                    )

                    Text(
                        text =
                            "$places places",
                        style =
                            LocalAppTypography
                                .current
                                .bodyLarge
                                .medium
                                .copy(
                                    color =
                                        Natural100.copy(
                                            alpha = 0.94f,
                                        ),
                                ),
                        maxLines =
                        1,
                    )
                }
            }
        }

        /*
         * 우측 하단 상세 이동 버튼
         */
        Box(
            modifier =
                Modifier
                    .align(
                        Alignment.BottomEnd,
                    )
                    .offset(
                        x =
                            -PlaceCardButtonEndInset,
                        y =
                        PlaceCardButtonVerticalOffset,
                    )
                    .size(
                        PlaceCardButtonSize,
                    )
                    .clip(
                        androidx.compose.foundation.shape.CircleShape,
                    )
                    .background(
                        Primary,
                    )
                    .clickable(
                        onClick =
                        onClick,
                    ),
            contentAlignment =
                Alignment.Center,
        ) {
            Image(
                painter =
                    painterResource(
                        id =
                            R.drawable.ic_chevron_right_white,
                    ),
                contentDescription =
                    "상세 보기",
                modifier =
                    Modifier.size(
                        16.dp,
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
