package com.hdb.tourfolio.core.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/**
 * 화면이 다시 화면 앞으로 나올 때(ON_RESUME)마다 [onResume]을 호출한다.
 * 예: 종목 상세에서 매수/매도 후 뒤로 돌아왔을 때 최신 데이터를 다시 불러오는 용도.
 * 최초 진입 시 발생하는 첫 ON_RESUME은 ViewModel의 초기 로딩과 중복되므로 건너뛴다.
 */
@Composable
fun RefreshOnResume(onResume: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnResume by rememberUpdatedState(onResume)

    DisposableEffect(lifecycleOwner) {
        var isFirstResume = true
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    if (isFirstResume) {
                        isFirstResume = false
                    } else {
                        currentOnResume()
                    }
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
