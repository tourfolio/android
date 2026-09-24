package com.hdb.tourfolio

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.hdb.tourfolio.feature.explore.presentation.components.TourSpotCard
import com.hdb.tourfolio.ui.components.SpotImage
import com.hdb.tourfolio.ui.components.SpotImageOverlay
import com.hdb.tourfolio.ui.theme.TourfolioTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File
import kotlin.math.abs

class SpotImageLayoutTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun imageBoundsFollowLandscapePortraitAndPanoramaModels() {
        val model = mutableStateOf(bitmap(240, 120))
        compose.setContent {
            TourfolioTheme {
                SpotImage(
                    hasImage = true,
                    model = model.value,
                    contentDescription = "photo",
                    modifier = Modifier.width(80.dp),
                )
            }
        }
        awaitRatio("photo", 2f)
        compose.runOnIdle { model.value = bitmap(120, 300) }
        awaitRatio("photo", 0.4f)
        compose.runOnIdle { model.value = bitmap(600, 60) }
        awaitRatio("photo", 10f)
    }

    @Test
    fun fixedThumbnailShowsWholeLandscapeImageWithLetterboxing() {
        compose.setContent {
            TourfolioTheme {
                SpotImage(
                    hasImage = true,
                    model = bitmap(240, 120),
                    contentDescription = "thumbnail",
                    modifier = Modifier.size(120.dp).background(Color.White),
                    matchImageAspectRatio = false,
                )
            }
        }
        val photo = compose.onNodeWithContentDescription("thumbnail")
        compose.waitUntil(timeoutMillis = 10_000) {
            runCatching { photo.fetchSemanticsNode() }.isSuccess
        }
        val pixels = photo.captureToImage().toPixelMap()
        assertEquals(pixels.width, pixels.height)
        assertEquals(Color.White, pixels[pixels.width / 2, 0])
        assertEquals(Color.Red, pixels[pixels.width / 2, pixels.height / 2])
        assertEquals(Color.White, pixels[pixels.width / 2, pixels.height - 1])
    }

    @Test
    fun shortPhotoKeepsItsBoundsWhenOverlayContentNeedsScrolling() {
        compose.setContent {
            TourfolioTheme {
                SpotImageOverlay(
                    hasImage = true,
                    model = bitmap(600, 120),
                    contentDescription = "panorama",
                    modifier = Modifier.width(300.dp).testTag("frame"),
                ) {
                    Text("긴 설명", modifier = Modifier.height(160.dp))
                    Text("마지막 태그")
                }
            }
        }
        awaitRatio("panorama", 5f)
        val imageBounds = compose.onNodeWithContentDescription("panorama").fetchSemanticsNode().boundsInRoot
        val frameBounds = compose.onNodeWithTag("frame").fetchSemanticsNode().boundsInRoot
        assertEquals(imageBounds.height, frameBounds.height, 1f)
        compose.onNodeWithText("마지막 태그").performScrollTo().assertIsDisplayed()
        awaitRatio("panorama", 5f)
    }

    @Test
    fun homeRecommendationKeepsPhotoRatioAndOverlaysCaptionAndTags() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val file = File.createTempFile("recommendation", ".png", context.cacheDir)
        try {
            file.outputStream().use { bitmap(300, 200).compress(Bitmap.CompressFormat.PNG, 100, it) }
            compose.setContent {
                TourfolioTheme {
                    TourSpotCard(
                        id = 1L,
                        title = "원본 사진",
                        content = "관광지 설명",
                        tags = listOf("여행"),
                        imageUrl = file.absolutePath,
                        hasImage = true,
                        onClick = {},
                        modifier = Modifier.width(300.dp),
                    )
                }
            }
            awaitRatio("원본 사진", 1.5f)
            val photo = compose.onNodeWithContentDescription("원본 사진", useUnmergedTree = true)
            val caption = compose.onNodeWithText("원본 사진", useUnmergedTree = true)
            val imageBounds = photo.fetchSemanticsNode().boundsInRoot
            val captionBounds = caption.fetchSemanticsNode().boundsInRoot
            val tagBounds = compose.onNodeWithText("#여행", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
            assertTrue(captionBounds.top >= imageBounds.top && captionBounds.bottom <= imageBounds.bottom)
            assertTrue(tagBounds.top >= imageBounds.top && tagBounds.bottom <= imageBounds.bottom)

            val pixels = photo.captureToImage().toPixelMap()
            // The top retains its source color, while a separate gradient darkens the bottom.
            assertEquals(Color.Red.red, pixels[0, 0].red, 0.02f)
            assertTrue(pixels[0, pixels.height - 1].red < 0.4f)
        } finally {
            file.delete()
        }
    }

    private fun awaitRatio(
        description: String,
        expected: Float,
    ) {
        compose.waitUntil(timeoutMillis = 10_000) {
            runCatching {
                val bounds = compose.onNodeWithContentDescription(description, useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
                abs(bounds.width / bounds.height - expected) < 0.03f
            }.getOrDefault(false)
        }
    }

    private fun bitmap(
        width: Int,
        height: Int,
    ): Bitmap =
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            eraseColor(android.graphics.Color.RED)
        }
}
