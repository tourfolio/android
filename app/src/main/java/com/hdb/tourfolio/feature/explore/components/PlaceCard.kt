@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.TourfolioTheme

private val PlaceCardAccent = Color(0xFFE96B4B)

/**
 * 오른쪽 아래에 원형 버튼을 위한 오목한 공간이 있는 카드 모양입니다.
 */
private class PlaceCardCutoutShape(
    private val cornerRadiusDp: Float = 12f,
    private val cutoutRadiusDp: Float = 47f,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val cornerRadius =
            with(density) {
                cornerRadiusDp.dp.toPx()
            }

        val cutoutRadius =
            with(density) {
                cutoutRadiusDp.dp.toPx()
            }

        val width = size.width
        val height = size.height

        val path =
            Path().apply {
                /*
                 * 왼쪽 위
                 */
                moveTo(cornerRadius, 0f)

                /*
                 * 위쪽 → 오른쪽 위
                 */
                lineTo(width - cornerRadius, 0f)

                quadraticTo(
                    x1 = width,
                    y1 = 0f,
                    x2 = width,
                    y2 = cornerRadius,
                )

                /*
                 * 오른쪽 변을 따라 내려옵니다.
                 * 여기서부터 원형 버튼을 위한 오목한 영역이 시작됩니다.
                 */
                lineTo(
                    x = width,
                    y = height - cutoutRadius * 1.65f,
                )

                /*
                 * 오른쪽 아래를 안쪽으로 둥글게 파냅니다.
                 *
                 * 첫 번째 곡선:
                 * 오른쪽 변에서 카드 안쪽 방향으로 진입
                 */
                cubicTo(
                    x1 = width,
                    y1 = height - cutoutRadius * 1.25f,
                    x2 = width - cutoutRadius * 0.15f,
                    y2 = height - cutoutRadius * 1.05f,
                    x3 = width - cutoutRadius * 0.48f,
                    y3 = height - cutoutRadius,
                )

                /*
                 * 두 번째 곡선:
                 * 원형 버튼 아래쪽을 감싸면서 카드 하단으로 연결
                 */
                cubicTo(
                    x1 = width - cutoutRadius * 0.95f,
                    y1 = height - cutoutRadius * 0.85f,
                    x2 = width - cutoutRadius * 1.08f,
                    y2 = height - cutoutRadius * 0.4f,
                    x3 = width - cutoutRadius * 1.08f,
                    y3 = height,
                )

                /*
                 * 카드 하단
                 */
                lineTo(cornerRadius, height)

                /*
                 * 왼쪽 아래 모서리
                 */
                quadraticTo(
                    x1 = 0f,
                    y1 = height,
                    x2 = 0f,
                    y2 = height - cornerRadius,
                )

                /*
                 * 왼쪽 변
                 */
                lineTo(0f, cornerRadius)

                /*
                 * 왼쪽 위 모서리
                 */
                quadraticTo(
                    x1 = 0f,
                    y1 = 0f,
                    x2 = cornerRadius,
                    y2 = 0f,
                )

                close()
            }

        return Outline.Generic(path)
    }
}

@Composable
fun PlaceCard(
    title: String,
    places: Int,
    @DrawableRes imageRes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val cardShape = PlaceCardCutoutShape()

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(150.dp)
                .clickable(onClick = onClick),
    ) {
        /*
         * 우측에 버튼이 배치될 공간을 남깁니다.
         * 카드 자체의 우측 하단은 PlaceCardCutoutShape에 의해
         * 안쪽으로 오목하게 처리됩니다.
         */
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(end = 34.dp)
                    .clip(cardShape),
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            /*
             * 글자가 위치하는 왼쪽과 아래쪽을 중심으로
             * 어두운 그라데이션을 적용합니다.
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
                                            Color.Black.copy(alpha = 0.4f),
                                        ),
                                ),
                        ),
            )

            Column(
                modifier =
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(
                            start = 20.dp,
                            end = 70.dp,
                            bottom = 18.dp,
                        ),
            ) {
                Text(
                    text = title,
                    style =
                        LocalAppTypography.current.bodyLarge.bold.copy(
                            color = Natural100,
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter =
                            painterResource(
                                id = R.drawable.ic_location,
                            ),
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        colorFilter =
                            ColorFilter.tint(
                                PlaceCardAccent,
                            ),
                    )

                    Spacer(modifier = Modifier.width(7.dp))

                    Text(
                        text = "$places places",
                        style =
                            LocalAppTypography.current.bodyLarge.medium.copy(
                                color = Natural100.copy(alpha = 0.94f),
                            ),
                        maxLines = 1,
                    )
                }
            }
        }

        /*
         * 카드의 오목한 영역에 들어가는 원형 버튼입니다.
         */
        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .offset(
                        x = (-25).dp,
                        y = (8).dp,
                    )
                    .size(52.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(PlaceCardAccent)
                    .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter =
                    painterResource(
                        id = R.drawable.ic_chevron_right_white,
                    ),
                contentDescription = "상세 보기",
                modifier = Modifier.size(20.dp),
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
            title = "서울로 떠나는 역사탐방",
            places = 10,
            imageRes = R.drawable.bg_seoul_demo,
            modifier =
                Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 20.dp,
                ),
        )
    }
}