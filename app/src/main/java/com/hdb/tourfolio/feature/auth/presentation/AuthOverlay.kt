@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdb.tourfolio.R
import com.hdb.tourfolio.domain.auth.model.User
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.Primary70
import com.hdb.tourfolio.ui.theme.TourfolioTheme
import com.kakao.sdk.auth.AuthCodeClient
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause

private val KakaoYellow = Color(0xFFFEE500)

@Composable
fun AuthOverlay(
    modifier: Modifier = Modifier,
    onSignupClick: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AuthOverlayContent(
        modifier = modifier,
        email = email,
        onEmailChange = { email = it },
        password = password,
        onPasswordChange = { password = it },
        uiState = uiState.loginState,
        onLoginClick = {
            viewModel.processIntent(AuthIntent.Login(email, password))
        },
        onSignupClick = onSignupClick,
        onKakaoLoginClick = {
            /*
             * 백엔드가 액세스 토큰이 아닌 인가 코드(code)를 받으므로 UserApiClient(토큰 반환)가 아니라
             * AuthCodeClient(인가 코드 반환)를 사용한다.
             */
            val callback: (String?, Throwable?) -> Unit = { code, error ->
                when {
                    code != null -> {
                        viewModel.processIntent(AuthIntent.LoginWithKakao(code))
                    }

                    error is ClientError && error.reason == ClientErrorCause.Cancelled -> {
                        // 사용자가 로그인 창을 직접 닫은 경우 - 에러로 표시하지 않는다.
                    }

                    else -> {
                        viewModel.processIntent(
                            AuthIntent.KakaoLoginFailed(error?.message ?: "카카오 로그인에 실패했습니다."),
                        )
                    }
                }
            }

            if (AuthCodeClient.instance.isKakaoTalkLoginAvailable(context)) {
                AuthCodeClient.instance.authorizeWithKakaoTalk(context, callback = callback)
            } else {
                AuthCodeClient.instance.authorizeWithKakaoAccount(context, callback = callback)
            }
        },
    )
}

@Composable
private fun AuthOverlayContent(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    uiState: AuthRequestState,
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit,
    onKakaoLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.weight(3f))

        Text(
            text = "Tourfolio",
            style =
                LocalAppTypography.current.headlineLarge.heavy.copy(
                    color = Primary70,
                ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            AuthLabeledField(
                label = "아이디",
                value = email,
                onValueChange = onEmailChange,
                placeholder = "아이디를 입력해주세요.",
                keyboardType = KeyboardType.Email,
            )

            Spacer(modifier = Modifier.height(14.dp))

            AuthPasswordField(
                label = "비밀번호",
                value = password,
                onValueChange = onPasswordChange,
                placeholder = "비밀번호를 입력해주세요.",
            )

            Spacer(modifier = Modifier.height(28.dp))

            AuthButton(
                label = "로그인",
                containerColor = Primary,
                onClick = onLoginClick,
                height = 44.dp,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "회원가입",
                style = LocalAppTypography.current.bodySmall.medium,
                color = Natural60,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onSignupClick),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "또는",
                style = LocalAppTypography.current.bodySmall.medium,
                color = Natural60,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            KakaoLoginButton(
                onClick = onKakaoLoginClick,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthResult(uiState = uiState)
        }

        Spacer(modifier = Modifier.weight(2f))
    }
}

@Composable
private fun KakaoLoginButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .height(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(KakaoYellow)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_kakao),
            contentDescription = null,
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .size(20.dp),
        )

        Text(
            text = "카카오톡으로 로그인",
            style = LocalAppTypography.current.titleSmall.bold,
            color = Natural10,
        )
    }
}

@Preview(
    name = "Auth Overlay Preview",
    showBackground = true,
    widthDp = 412,
    heightDp = 800,
)
@Composable
private fun AuthOverlayPreview() {
    TourfolioTheme(dynamicColor = false) {
        AuthOverlayContent(
            email = "user@example.com",
            onEmailChange = {},
            password = "",
            onPasswordChange = {},
            uiState =
                AuthRequestState.Success(
                    User(
                        id = 1L,
                        email = "user@example.com",
                        nickname = "투어폴리오유저",
                    ),
                ),
            onLoginClick = {},
            onSignupClick = {},
            onKakaoLoginClick = {},
        )
    }
}
