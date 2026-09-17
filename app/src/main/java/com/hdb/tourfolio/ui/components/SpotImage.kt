@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Primary70

@Composable
fun SpotImage(
    hasImage: Boolean,
    model: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    if (!hasImage || model.isNullOrBlank()) {
        SpotImagePlaceholder(
            modifier = modifier,
        )

        return
    }

    SubcomposeAsyncImage(
        model = secureSpotImageUrl(model),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        loading = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                // 필요하다면 로딩 UI 추가
            }
        },
        error = {
            SpotImagePlaceholder(
                modifier = Modifier.fillMaxSize(),
            )
        },
        success = {
            SubcomposeAsyncImageContent()
        },
    )
}

private fun secureSpotImageUrl(url: String): String {
    // 한국관광공사 이미지의 기존 HTTP 주소는 HTTPS에서도 동일한 파일을 제공한다.
    val legacyPrefix = "http://tong.visitkorea.or.kr/"
    return if (url.startsWith(legacyPrefix, ignoreCase = true)) {
        "https://tong.visitkorea.or.kr/" + url.substring(legacyPrefix.length)
    } else {
        url
    }
}

@Composable
private fun SpotImagePlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier.background(
                color = Color.White,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "이미지 준비중",
            color = Primary70,
            style =
                LocalAppTypography.current.labelSmall.medium,
        )
    }
}
