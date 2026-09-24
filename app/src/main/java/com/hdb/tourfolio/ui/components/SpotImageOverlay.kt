@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/** The photo alone determines the bounds; decoration never resizes or crops it. */
@Composable
fun SpotImageOverlay(
    hasImage: Boolean,
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(modifier = modifier) {
        SpotImage(
            hasImage = hasImage,
            model = model,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxWidth(),
        )
        Box(
            modifier =
                Modifier.matchParentSize().background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.4f to Color.Black.copy(alpha = 0.12f),
                        1f to Color.Black.copy(alpha = 0.8f),
                    ),
                ),
        ) {
            // Very wide photos can be shorter than their captions. Scroll the overlay
            // within the photo instead of stretching the frame or hiding the tags.
            Column(
                modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().verticalScroll(rememberScrollState()),
                content = content,
            )
        }
    }
}
