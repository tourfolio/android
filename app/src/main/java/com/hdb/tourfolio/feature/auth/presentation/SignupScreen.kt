@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdb.tourfolio.R
import com.hdb.tourfolio.ui.components.CommonBackHeader
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural80
import com.hdb.tourfolio.ui.theme.Natural90
import com.hdb.tourfolio.ui.theme.Natural99
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.Red
import com.hdb.tourfolio.ui.theme.TourfolioTheme

@Composable
fun SignupScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onTermsDetailClick: (SignupTermsType) -> Unit = {},
    agreedTermsType: SignupTermsType? = null,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }

    var ageConfirmed by remember { mutableStateOf(false) }
    var termsAgreed by remember { mutableStateOf(false) }
    var privacyAgreed by remember { mutableStateOf(false) }
    var locationAgreed by remember { mutableStateOf(false) }

    LaunchedEffect(agreedTermsType) {
        when (agreedTermsType) {
            SignupTermsType.AGE -> ageConfirmed = true
            SignupTermsType.TERMS -> termsAgreed = true
            SignupTermsType.PRIVACY -> privacyAgreed = true
            SignupTermsType.LOCATION -> locationAgreed = true
            null -> Unit
        }
    }

    SignupScreenContent(
        modifier = modifier,
        email = email,
        onEmailChange = { email = it },
        password = password,
        onPasswordChange = { password = it },
        passwordConfirm = passwordConfirm,
        onPasswordConfirmChange = { passwordConfirm = it },
        nickname = nickname,
        onNicknameChange = { nickname = it },
        ageConfirmed = ageConfirmed,
        onAgeConfirmedToggle = { ageConfirmed = !ageConfirmed },
        termsAgreed = termsAgreed,
        onTermsAgreedToggle = { termsAgreed = !termsAgreed },
        privacyAgreed = privacyAgreed,
        onPrivacyAgreedToggle = { privacyAgreed = !privacyAgreed },
        locationAgreed = locationAgreed,
        onLocationAgreedToggle = { locationAgreed = !locationAgreed },
        onAllAgreedToggle = {
            val next = !(ageConfirmed && termsAgreed && privacyAgreed && locationAgreed)
            ageConfirmed = next
            termsAgreed = next
            privacyAgreed = next
            locationAgreed = next
        },
        onTermsDetailClick = { onTermsDetailClick(SignupTermsType.TERMS) },
        onPrivacyDetailClick = { onTermsDetailClick(SignupTermsType.PRIVACY) },
        onLocationDetailClick = { onTermsDetailClick(SignupTermsType.LOCATION) },
        uiState = uiState.signupState,
        onBackClick = onBackClick,
        onSignupClick = {
            viewModel.processIntent(AuthIntent.Signup(email, password, nickname))
        },
    )
}

@Composable
private fun SignupScreenContent(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordConfirm: String,
    onPasswordConfirmChange: (String) -> Unit,
    nickname: String,
    onNicknameChange: (String) -> Unit,
    ageConfirmed: Boolean,
    onAgeConfirmedToggle: () -> Unit,
    termsAgreed: Boolean,
    onTermsAgreedToggle: () -> Unit,
    privacyAgreed: Boolean,
    onPrivacyAgreedToggle: () -> Unit,
    locationAgreed: Boolean,
    onLocationAgreedToggle: () -> Unit,
    onAllAgreedToggle: () -> Unit,
    onTermsDetailClick: () -> Unit,
    onPrivacyDetailClick: () -> Unit,
    onLocationDetailClick: () -> Unit,
    uiState: AuthRequestState,
    onBackClick: () -> Unit,
    onSignupClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val allAgreed = ageConfirmed && termsAgreed && privacyAgreed && locationAgreed
    val requiredAgreed = ageConfirmed && termsAgreed && privacyAgreed
    val canSubmit =
        email.isNotBlank() &&
            password.isNotBlank() &&
            password == passwordConfirm &&
            nickname.isNotBlank() &&
            requiredAgreed

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Natural100),
    ) {
        CommonBackHeader(title = "회원가입", onBackClick = onBackClick)

        Box(
            modifier = Modifier.weight(1f),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
            ) {
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

                    AuthPasswordGroup(
                        label = "비밀번호",
                        password = password,
                        onPasswordChange = onPasswordChange,
                        passwordPlaceholder = "비밀번호를 입력해주세요.",
                        passwordConfirm = passwordConfirm,
                        onPasswordConfirmChange = onPasswordConfirmChange,
                        passwordConfirmPlaceholder = "비밀번호를 다시 입력해주세요.",
                        showVisibilityToggle = false,
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    AuthLabeledField(
                        label = "닉네임",
                        value = nickname,
                        onValueChange = onNicknameChange,
                        placeholder = "닉네임을 입력해주세요.",
                        keyboardType = KeyboardType.Text,
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Natural99),
                )

                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    TermsSection(
                        allAgreed = allAgreed,
                        onAllAgreedToggle = onAllAgreedToggle,
                        ageConfirmed = ageConfirmed,
                        onAgeConfirmedToggle = onAgeConfirmedToggle,
                        termsAgreed = termsAgreed,
                        onTermsAgreedToggle = onTermsAgreedToggle,
                        onTermsDetailClick = onTermsDetailClick,
                        privacyAgreed = privacyAgreed,
                        onPrivacyAgreedToggle = onPrivacyAgreedToggle,
                        onPrivacyDetailClick = onPrivacyDetailClick,
                        locationAgreed = locationAgreed,
                        onLocationAgreedToggle = onLocationAgreedToggle,
                        onLocationDetailClick = onLocationDetailClick,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AuthResult(
                        uiState = uiState,
                        successMessage = { "${it.nickname}님, 회원가입이 완료되었습니다." },
                    )

                    Spacer(modifier = Modifier.height(28.dp))
                }
            }

            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(28.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Natural100.copy(alpha = 0f), Natural100),
                            ),
                        ),
            )
        }

        Column {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.06f),
                                    ),
                            ),
                        ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthButton(
                label = "가입하기",
                containerColor = Primary,
                onClick = onSignupClick,
                height = 44.dp,
                enabled = canSubmit,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TermsSection(
    allAgreed: Boolean,
    onAllAgreedToggle: () -> Unit,
    ageConfirmed: Boolean,
    onAgeConfirmedToggle: () -> Unit,
    termsAgreed: Boolean,
    onTermsAgreedToggle: () -> Unit,
    onTermsDetailClick: () -> Unit,
    privacyAgreed: Boolean,
    onPrivacyAgreedToggle: () -> Unit,
    onPrivacyDetailClick: () -> Unit,
    locationAgreed: Boolean,
    onLocationAgreedToggle: () -> Unit,
    onLocationDetailClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onAllAgreedToggle),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AgreementCheckbox(checked = allAgreed)

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "약관 모두 동의",
                style = LocalAppTypography.current.titleSmall.bold,
                color = Natural10,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Natural90, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            AgreementItem(
                checked = ageConfirmed,
                label = "만 14세 이상입니다",
                required = true,
                onToggle = onAgeConfirmedToggle,
                onDetailClick = null,
            )

            AgreementItem(
                checked = termsAgreed,
                label = "이용약관 동의",
                required = true,
                onToggle = onTermsAgreedToggle,
                onDetailClick = onTermsDetailClick,
            )

            AgreementItem(
                checked = privacyAgreed,
                label = "개인정보처리방침 동의",
                required = true,
                onToggle = onPrivacyAgreedToggle,
                onDetailClick = onPrivacyDetailClick,
            )

            AgreementItem(
                checked = locationAgreed,
                label = "위치정보 이용 동의",
                required = false,
                onToggle = onLocationAgreedToggle,
                onDetailClick = onLocationDetailClick,
            )
        }
    }
}

@Composable
private fun AgreementItem(
    checked: Boolean,
    label: String,
    required: Boolean,
    onToggle: () -> Unit,
    onDetailClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AgreementCheckbox(checked = checked, size = 18.dp)

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = label,
            style = LocalAppTypography.current.bodySmall.medium,
            color = Natural60,
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = if (required) "(필수)" else "(선택)",
            style = LocalAppTypography.current.bodySmall.bold,
            color = if (required) Red else Natural60,
        )

        Spacer(modifier = Modifier.weight(1f))

        if (onDetailClick != null) {
            Text(
                text = "내용 보기",
                style = LocalAppTypography.current.bodySmall.medium,
                color = Natural80,
                modifier = Modifier.clickable(onClick = onDetailClick),
            )
        }
    }
}

@Composable
private fun AgreementCheckbox(
    checked: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
) {
    if (checked) {
        Image(
            painter = painterResource(id = R.drawable.ic_checkbox),
            contentDescription = null,
            modifier = modifier.size(size),
        )
    } else {
        Box(
            modifier =
                modifier
                    .size(size)
                    .clip(RoundedCornerShape(6.dp))
                    .border(1.5.dp, Natural90, RoundedCornerShape(6.dp)),
        )
    }
}

@Preview(
    name = "Signup Screen Preview",
    showBackground = true,
    widthDp = 412,
    heightDp = 900,
)
@Composable
private fun SignupScreenPreview() {
    TourfolioTheme(dynamicColor = false) {
        SignupScreenContent(
            email = "user@example.com",
            onEmailChange = {},
            password = "",
            onPasswordChange = {},
            passwordConfirm = "",
            onPasswordConfirmChange = {},
            nickname = "투어폴리오유저",
            onNicknameChange = {},
            ageConfirmed = true,
            onAgeConfirmedToggle = {},
            termsAgreed = true,
            onTermsAgreedToggle = {},
            privacyAgreed = true,
            onPrivacyAgreedToggle = {},
            locationAgreed = true,
            onLocationAgreedToggle = {},
            onAllAgreedToggle = {},
            onTermsDetailClick = {},
            onPrivacyDetailClick = {},
            onLocationDetailClick = {},
            uiState = AuthRequestState.Idle,
            onBackClick = {},
            onSignupClick = {},
        )
    }
}
