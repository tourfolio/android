@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.card.presentation.components

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hdb.tourfolio.R
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.Primary99

private const val LOCATION_PERMISSION_PREFS =
    "location_permission_prefs"

private const val KEY_LOCATION_PERMISSION_REQUESTED =
    "location_permission_requested"

enum class LocationDialogType {
    PERMISSION_REQUEST,
    PERMISSION_REQUIRED,
}

fun shouldShowLocationPermissionFlow(context: Context): Boolean {
    if (
        isPreciseLocationGranted(
            context = context,
        )
    ) {
        return false
    }

    val hasRequestedBefore =
        hasRequestedLocationPermission(
            context = context,
        )

    if (!hasRequestedBefore) {
        return true
    }

    val activity =
        context.findActivity()
            ?: return false

    return ActivityCompat
        .shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
}

/*
 * 현재 정확한 위치 권한이 이미 허용되어 있는지
 */
fun isPreciseLocationGranted(context: Context): Boolean =
    ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED

@Composable
fun LocationPermissionDialog(
    onDismissRequest: () -> Unit,
    onPreciseLocationGranted: () -> Unit,
    onPreciseLocationDenied: () -> Unit = {},
    onLocationPermissionGranted: () -> Unit = {},
) {
    val context =
        LocalContext.current

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestMultiplePermissions(),
        ) { permissions ->
            val fineLocationGranted =
                permissions[
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ] == true

            if (fineLocationGranted || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                onLocationPermissionGranted()
            }

            if (fineLocationGranted) {
                onPreciseLocationGranted()
            } else {
                onPreciseLocationDenied()
            }
        }

    fun requestLocationPermission() {
        if (
            isPreciseLocationGranted(
                context = context,
            )
        ) {
            onPreciseLocationGranted()
            return
        }

        markLocationPermissionRequested(
            context = context,
        )

        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ),
        )
    }

    LocationGuideDialog(
        iconRes = R.drawable.ic_location,
        title =
            "Tourfolio가 내 위치에 접근하도록\n" +
                "허용하시겠어요?",
        description =
            "관광지 방문을 인증해 카드를 획득하려면\n" +
                "위치 정보가 필요해요",
        notice =
            "※ 해당 위치 정보는 서버에 전송되지 않으며,\n" +
                    "위치 인증 목적으로만 사용된 후 즉시 폐기됩니다.",
        primaryButtonText = "위치 확인하기",
        onPrimaryClick = {
            requestLocationPermission()
        },
        secondaryButtonText = "취소",
        onSecondaryClick = onDismissRequest,
        onDismissRequest = onDismissRequest,
    )
}

/*
 * 반복 거부 등으로 Android 시스템 권한창을 더 이상 요청할 수 없는 경우
 */
@Composable
fun LocationPermissionRequiredDialog(onDismissRequest: () -> Unit) {
    LocationGuideDialog(
        iconRes = R.drawable.ic_info,
        title = "위치 권한이 필요해요",
        description =
                "기기 설정에서 해당 권한 허용을 눌러주세요",
        primaryButtonText = "닫기",
        onPrimaryClick = onDismissRequest,
        onDismissRequest = onDismissRequest,
    )
}

@Composable
private fun LocationGuideDialog(
    iconRes: Int,
    title: String,
    description: String,
    primaryButtonText: String,
    onPrimaryClick: () -> Unit,
    onDismissRequest: () -> Unit,
    notice: String? = null,
    secondaryButtonText: String? = null,
    onSecondaryClick: (() -> Unit)? = null,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties =
            DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        color = Natural100,
                        shape = RoundedCornerShape(24.dp),
                    )
                    .padding(
                        start = 26.dp,
                        top = 38.dp,
                        end = 26.dp,
                        bottom = 30.dp,
                    ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(86.dp)
                        .background(
                            color = Primary99,
                            shape = RoundedCornerShape(16.dp),
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter =
                        painterResource(
                            id = iconRes,
                        ),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                )
            }

            Spacer(
                modifier = Modifier.height(34.dp),
            )

            /*
             * 제목
             */
            Text(
                text = title,
                style =
                    LocalAppTypography.current.titleMedium.bold.copy(
                        color = Natural10,
                    ),
                textAlign = TextAlign.Center,
            )

            Spacer(
                modifier = Modifier.height(24.dp),
            )

            Text(
                text = description,
                style =
                    LocalAppTypography.current.bodyLarge.medium.copy(
                        color = Natural60,
                    ),
                textAlign = TextAlign.Center,
            )

            if (notice != null) {
                Spacer(
                    modifier = Modifier.height(12.dp),
                )

                Text(
                    text = notice,
                    modifier = Modifier.fillMaxWidth(),
                    style =
                        LocalAppTypography.current.bodySmall.medium.copy(
                            color = Natural60,
                        ),
                    textAlign = TextAlign.Start,
                )
            }

            Spacer(
                modifier = Modifier.height(44.dp),
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .background(
                            color = Primary,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .clickable(
                            onClick = onPrimaryClick,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = primaryButtonText,
                    style =
                        LocalAppTypography.current.bodyLarge.bold.copy(
                            color = Natural100,
                        ),
                )
            }

            if (
                secondaryButtonText != null &&
                onSecondaryClick != null
            ) {
                Spacer(
                    modifier = Modifier.height(14.dp),
                )

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clickable(
                                onClick = onSecondaryClick,
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = secondaryButtonText,
                        style =
                            LocalAppTypography.current.bodyLarge.medium.copy(
                                color = Natural60,
                            ),
                    )
                }
            }
        }
    }
}

/*
 * 위치 권한을 한 번이라도 요청했는지 저장
 */
private fun hasRequestedLocationPermission(context: Context): Boolean =
    context
        .getSharedPreferences(
            LOCATION_PERMISSION_PREFS,
            Context.MODE_PRIVATE,
        )
        .getBoolean(
            KEY_LOCATION_PERMISSION_REQUESTED,
            false,
        )

private fun markLocationPermissionRequested(context: Context) {
    context
        .getSharedPreferences(
            LOCATION_PERMISSION_PREFS,
            Context.MODE_PRIVATE,
        )
        .edit()
        .putBoolean(
            KEY_LOCATION_PERMISSION_REQUESTED,
            true,
        )
        .apply()
}

private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity ->
            this

        is ContextWrapper ->
            baseContext.findActivity()

        else ->
            null
    }
