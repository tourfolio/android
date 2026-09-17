@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.mypage.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.hdb.tourfolio.feature.card.presentation.components.isPreciseLocationGranted
import com.hdb.tourfolio.feature.card.presentation.components.markLocationPermissionRequested
import com.hdb.tourfolio.feature.card.presentation.components.shouldShowLocationPermissionFlow
import com.hdb.tourfolio.ui.components.CommonInfoModal

enum class MyPagePermission { NOTIFICATION, LOCATION }

@Composable
fun rememberMyPagePermissionRequest(): (MyPagePermission) -> Unit {
    val context = LocalContext.current
    var message by rememberSaveable { mutableStateOf<String?>(null) }
    var settingsPermission by rememberSaveable { mutableStateOf<MyPagePermission?>(null) }
    val activity = context.permissionActivity()

    fun showSettings(permission: MyPagePermission) {
        settingsPermission = permission
        message =
            if (permission == MyPagePermission.LOCATION) {
                "기기 설정에서 위치 권한을 직접 허용해 주세요.\n포토카드 수집을 위해 정확한 위치 사용도 켜 주세요."
            } else {
                "기기 설정에서 알림 권한을 직접 허용해 주세요."
            }
    }

    val locationLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (!isPreciseLocationGranted(context) && !shouldShowLocationPermissionFlow(context)) {
                showSettings(MyPagePermission.LOCATION)
            }
        }
    val notificationLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted && activity != null &&
                !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.POST_NOTIFICATIONS)
            ) {
                showSettings(MyPagePermission.NOTIFICATION)
            }
        }

    message?.let { text ->
        CommonInfoModal(
            message = text,
            onDismissRequest = { message = null },
            onSettingsClick =
                settingsPermission?.let { permission ->
                    {
                        val intent =
                            if (permission == MyPagePermission.NOTIFICATION) {
                                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            } else {
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}"))
                            }
                        context.startActivity(intent)
                        message = null
                    }
                },
        )
    }

    return { permission ->
        settingsPermission = null
        val granted =
            when (permission) {
                MyPagePermission.LOCATION -> isPreciseLocationGranted(context)
                MyPagePermission.NOTIFICATION -> NotificationManagerCompat.from(context).areNotificationsEnabled()
            }
        if (granted) {
            message = "이미 허용된 권한입니다."
        } else if (permission == MyPagePermission.LOCATION) {
            if (shouldShowLocationPermissionFlow(context) && activity != null) {
                markLocationPermissionRequested(context)
                locationLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            } else {
                showSettings(permission)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && activity != null) {
            val preferences = context.getSharedPreferences("notification_permission_prefs", Context.MODE_PRIVATE)
            val requested = preferences.getBoolean("requested", false)
            val runtimeGranted =
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            val canRequestAgain =
                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.POST_NOTIFICATIONS)
            if (runtimeGranted || (requested && !canRequestAgain)) {
                showSettings(permission)
            } else {
                preferences.edit().putBoolean("requested", true).apply()
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            showSettings(permission)
        }
    }
}

private tailrec fun Context.permissionActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.permissionActivity()
        else -> null
    }
