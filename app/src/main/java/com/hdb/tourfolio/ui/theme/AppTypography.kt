package com.hdb.tourfolio.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// LocalAppTypography.current.titleSmall.bold 형태로 사용.
data class HeavyBoldStyle(
    val heavy: TextStyle,
    val bold: TextStyle,
)

data class BoldMediumStyle(
    val bold: TextStyle,
    val medium: TextStyle,
)

data class HeavyBoldMediumStyle(
    val heavy: TextStyle,
    val bold: TextStyle,
    val medium: TextStyle,
)

data class AppTypography(
    val headlineLarge: HeavyBoldStyle,
    val titleLarge: TextStyle,
    val titleMedium: BoldMediumStyle,
    val titleSmall: HeavyBoldMediumStyle,
    val bodyLarge: HeavyBoldMediumStyle,
    val bodySmall: HeavyBoldMediumStyle,
    val labelLarge: HeavyBoldMediumStyle,
    val labelSmall: HeavyBoldMediumStyle,
)

val DefaultAppTypography =
    AppTypography(
        headlineLarge =
            HeavyBoldStyle(
                heavy =
                    TextStyle(
                        fontFamily = SuitFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 30.sp,
                        lineHeight = 30.sp,
                        letterSpacing = 0.sp,
                    ),
                bold =
                    TextStyle(
                        fontFamily = SuitFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        lineHeight = 30.sp,
                        letterSpacing = 0.sp,
                    ),
            ),
        titleLarge =
            TextStyle(
                fontFamily = SuitFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.sp,
            ),
        titleMedium =
            BoldMediumStyle(
                bold =
                    TextStyle(
                        fontFamily = SuitFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        lineHeight = 20.sp,
                        letterSpacing = 0.sp,
                    ),
                medium =
                    TextStyle(
                        fontFamily = SuitFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        lineHeight = 20.sp,
                        letterSpacing = 0.sp,
                    ),
            ),
        titleSmall =
            HeavyBoldMediumStyle(
                heavy =
                    TextStyle(
                        fontFamily = SuitFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        lineHeight = 18.sp,
                        letterSpacing = 0.sp,
                    ),
                bold =
                    TextStyle(
                        fontFamily = SuitFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        lineHeight = 18.sp,
                        letterSpacing = 0.sp,
                    ),
                medium =
                    TextStyle(
                        fontFamily = SuitFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        lineHeight = 18.sp,
                        letterSpacing = 0.sp,
                    ),
            ),
        bodyLarge =
            HeavyBoldMediumStyle(
                heavy =
                    TextStyle(
                        fontFamily = SuitFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 0.sp,
                    ),
                bold =
                    TextStyle(
                        fontFamily = SuitFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 0.sp,
                    ),
                medium =
                    TextStyle(
                        fontFamily = SuitFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 0.sp,
                    ),
            ),
        bodySmall =
            HeavyBoldMediumStyle(
                heavy =
                    TextStyle(
                        fontFamily = SuitFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        letterSpacing = 0.sp,
                    ),
                bold =
                    TextStyle(
                        fontFamily = SuitFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        letterSpacing = 0.sp,
                    ),
                medium =
                    TextStyle(
                        fontFamily = SuitFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        letterSpacing = 0.sp,
                    ),
            ),
        labelLarge =
            HeavyBoldMediumStyle(
                heavy =
                    TextStyle(
                        fontFamily = SuitFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        letterSpacing = 0.sp,
                    ),
                bold =
                    TextStyle(
                        fontFamily = SuitFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        letterSpacing = 0.sp,
                    ),
                medium =
                    TextStyle(
                        fontFamily = SuitFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        letterSpacing = 0.sp,
                    ),
            ),
        labelSmall =
            HeavyBoldMediumStyle(
                heavy =
                    TextStyle(
                        fontFamily = SuitFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        lineHeight = 10.sp,
                        letterSpacing = 0.sp,
                    ),
                bold =
                    TextStyle(
                        fontFamily = SuitFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        lineHeight = 10.sp,
                        letterSpacing = 0.sp,
                    ),
                medium =
                    TextStyle(
                        fontFamily = SuitFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp,
                        lineHeight = 10.sp,
                        letterSpacing = 0.sp,
                    ),
            ),
    )

val LocalAppTypography = staticCompositionLocalOf { DefaultAppTypography }
