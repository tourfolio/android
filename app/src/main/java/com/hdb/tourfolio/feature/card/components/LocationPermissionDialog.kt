@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.card.components

import android.Manifest
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import com.hdb.tourfolio.R
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.Primary99

@Composable
fun LocationPermissionDialog(
    onDismissRequest: () -> Unit,
    onPreciseLocationGranted: () -> Unit,
    onPreciseLocationDenied: () -> Unit = {},
) {
    val context =
        LocalContext.current

    /*
     * Android 시스템 위치 권한 창을 호출합니다.
     *
     * Android 12 이상에서 정확한 위치 권한을 요청할 때는
     * FINE / COARSE를 함께 요청합니다.
     */
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestMultiplePermissions(),
        ) { permissions ->
            val fineLocationGranted =
                permissions[
                    Manifest.permission.ACCESS_FINE_LOCATION
                ] == true

            if (fineLocationGranted) {
                /*
                 * 정확한 위치 권한 획득 성공
                 *
                 * 다음 단계에서:
                 * - 현재 GPS 위치 획득
                 * - 관광지 좌표 API 호출
                 * - 거리 계산
                 *
                 * 로 이어지면 됩니다.
                 */
                onPreciseLocationGranted()
            } else {
                /*
                 * 권한 거부 또는
                 * 대략적인 위치만 허용된 경우입니다.
                 *
                 * 카드 획득은 200m 기준이므로
                 * 정확한 위치 권한을 획득한 경우에만
                 * 다음 과정으로 진행하도록 합니다.
                 */
                onPreciseLocationDenied()
            }
        }

    /*
     * 이미 정확한 위치 권한이 있는 경우에는
     * 시스템 권한창을 다시 띄우지 않고 바로 진행합니다.
     */
    fun requestLocationPermission() {
        val fineLocationAlreadyGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

        if (fineLocationAlreadyGranted) {
            onPreciseLocationGranted()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
    }

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
                        start = 28.dp,
                        top = 38.dp,
                        end = 28.dp,
                        bottom = 30.dp,
                    ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            /*
             * 상단 위치 아이콘
             */
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
                            id = R.drawable.ic_location,
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
                text =
                    "Tourfolio가 내 위치에 접근하도록\n" +
                            "허용하시겠어요?",
                style =
                    LocalAppTypography.current.titleMedium.bold.copy(
                        color = Natural10,
                    ),
                textAlign = TextAlign.Center,
            )

            Spacer(
                modifier = Modifier.height(24.dp),
            )

            /*
             * 설명
             */
            Text(
                text =
                    "관광지 방문을 인증해 카드를 획득하려면\n" +
                            "위치 정보가 필요해요",
                style =
                    LocalAppTypography.current.bodyLarge.medium.copy(
                        color = Natural60,
                    ),
                textAlign = TextAlign.Center,
            )

            Spacer(
                modifier = Modifier.height(52.dp),
            )

            /*
             * 위치 확인하기
             *
             * 클릭하면 Android 시스템 위치 권한창이 나타납니다.
             */
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .background(
                            color = Primary,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .clickable {
                            requestLocationPermission()
                        },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "위치 확인하기",
                    style =
                        LocalAppTypography.current.bodyLarge.bold.copy(
                            color = Natural100,
                        ),
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp),
            )

            /*
             * 취소
             */
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clickable(
                            onClick = onDismissRequest,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "취소",
                    style =
                        LocalAppTypography.current.bodyLarge.medium.copy(
                            color = Natural60,
                        ),
                )
            }
        }
    }
}