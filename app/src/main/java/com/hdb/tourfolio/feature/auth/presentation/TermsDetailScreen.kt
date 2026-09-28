@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.ui.components.CommonBackHeader
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural60
import com.hdb.tourfolio.ui.theme.Natural90
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.TourfolioTheme

/*
 * 회원가입 약관 상세 - content 는 추후 실제 약관 문구로 채워 넣는다.
 */
enum class SignupTermsType(
    val title: String,
    val content: String,
) {
    AGE(
        title = "만 14세 이상입니다",
        content = "",
    ),
    TERMS(
        title = "이용약관",
        content =
            """
            투어폴리오 이용약관

            시행일: 2026년 8월 20일
            운영주체: 팀해달별

            제1조 (목적)
            본 약관은 팀해달별(이하 "팀")이 운영하는 투어폴리오(Tourfolio) 애플리케이션(이하 "앱")의 이용과 관련하여 팀과 이용자 간의 권리, 의무 및 책임사항을 규정함을 목적으로 합니다.

            제2조 (정의)
            - 서비스: 앱이 제공하는 관광지 가상 투자, 포토카드 수집 등 일체의 기능
            - 이용자: 본 약관에 따라 앱을 이용하는 회원
            - 가상 자산: 앱 내에서만 통용되는 포인트, 보유 종목, 포토카드 등

            제3조 (약관의 효력 및 변경)
            본 약관은 앱 내 게시함으로써 효력이 발생합니다. 팀은 관련 법령을 위배하지 않는 범위에서 약관을 변경할 수 있으며, 변경 시 앱 내 공지 또는 이메일로 사전 안내합니다.

            제4조 (서비스의 내용)
            앱은 한국관광공사 제공 관광 데이터를 기반으로 다음의 서비스를 제공합니다.
            - 관광지 정보 탐색
            - 관광지 기반 가상 투자 시뮬레이션
            - 관광지 방문 인증을 통한 포토카드 수집
            - 미션 및 업적 시스템

            제5조 (회원가입)
            이용자는 팀이 정한 가입 양식에 따라 회원가입을 신청하며, 팀이 이를 승낙함으로써 회원가입이 완료됩니다. 팀은 다음 각 호에 해당하는 경우 승낙을 유보하거나 거절할 수 있습니다.
            - 타인의 정보를 도용한 경우
            - 허위 정보를 기재한 경우
            - 만 14세 미만인 경우

            제6조 (회원 탈퇴 및 자격 상실)
            이용자는 언제든지 앱 내 기능을 통해 탈퇴를 요청할 수 있으며, 탈퇴 시 보유한 모든 가상 자산 및 이용 기록은 삭제됩니다(구체적 절차는 "계정 삭제 요청" 문서에 따름). 팀은 이용자가 다음 각 호에 해당하는 경우 사전 통지 없이 이용을 제한하거나 자격을 상실시킬 수 있습니다.
            - 타인의 계정을 부정 사용한 경우
            - 서비스 운영을 방해한 경우
            - 관련 법령 또는 본 약관을 위반한 경우

            제7조 (회원의 의무)
            이용자는 다음 행위를 하여서는 안 됩니다.
            - 타인의 개인정보 도용
            - 서비스의 정상적 운영을 방해하는 행위
            - 자동화된 수단(매크로 등)을 이용한 부정한 방법의 가상 자산 취득
            - GPS 조작 등 부정한 방법을 통한 포토카드 획득
            - 관련 법령 및 본 약관에서 금지하는 행위

            제8조 (회사의 의무)
            팀은 안정적인 서비스 제공을 위해 노력하며, 이용자의 개인정보를 관련 법령 및 개인정보처리방침에 따라 보호합니다.

            제9조 (서비스 이용시간 및 중단)
            서비스는 연중무휴 제공을 원칙으로 하나, 시스템 점검, 장애, 기타 불가피한 사유로 일시 중단될 수 있습니다.

            제10조 (가상 자산 및 콘텐츠의 성격)
            앱 내 가상 투자 기능 및 포인트, 보유 종목 등 일체의 가상 자산은 실제 금전적 가치를 지니지 않는 게임형 시뮬레이션 콘텐츠입니다. 이는 실제 금융투자상품이 아니며, 현금 또는 이에 준하는 자산으로 환전·환급되지 않습니다. 앱 내 관광지 가격 정보는 실제 부동산 가치나 투자 수익을 보장하지 않습니다.

            제11조 (지식재산권)
            앱 내에서 팀이 제작한 콘텐츠(디자인, 카드 일러스트, 문구 등)에 대한 저작권은 팀에 귀속됩니다. 관광지 이미지 및 정보의 출처와 저작권에 관한 사항은 별도의 "이미지 저작권 안내"에 따릅니다.

            제12조 (면책조항)
            - 팀은 천재지변, 시스템 장애 등 불가항력으로 인한 서비스 중단에 대해 책임을 지지 않습니다.
            - 팀은 이용자가 앱에서 제공하는 관광 정보를 활용하여 실제 여행, 방문 등을 진행하는 과정에서 발생한 손해에 대해 책임을 지지 않습니다.
            - 앱 내 가상 투자 정보는 참고용 콘텐츠이며, 실제 투자 판단의 근거로 사용할 수 없습니다.

            제13조 (분쟁해결)
            본 약관과 관련하여 분쟁이 발생할 경우, 팀과 이용자는 상호 협의를 통해 원만히 해결하도록 노력합니다. 협의가 이루어지지 않을 경우 관련 법령에 따릅니다.

            부칙
            본 약관은 2026년 8월 20일부터 시행합니다.

            © 2026 팀해달별 · 투어폴리오(Tourfolio) · team.haedalbyeol.dev@gmail.com
            """.trimIndent(),
    ),
    PRIVACY(
        title = "개인정보처리방침",
        content =
            """
            투어폴리오 개인정보처리방침

            시행일: 2026년 8월 20일
            운영주체: 팀해달별

            1. 개인정보처리방침 개요
            팀해달별(이하 "팀")이 운영하는 투어폴리오(Tourfolio) 애플리케이션(이하 "앱")은 사용자의 개인정보를 소중히 여기며, 관련 법령을 준수합니다. 본 방침은 앱이 수집하는 정보와 그 활용 방식을 안내합니다.

            2. 수집하는 개인정보 항목 및 목적
            - 이메일 주소, 비밀번호
              수집 목적: 로컬 로그인 계정 생성 및 인증
              보유 기간: 회원 탈퇴 시까지
            - 카카오 계정 정보(닉네임, 프로필 이미지)
              수집 목적: 소셜 로그인(카카오) 인증
              보유 기간: 회원 탈퇴 시까지
            - 위치정보
              수집 목적: 관광지 방문 인증 및 포토카드 획득
              보유 기간: 처리 후 즉시 파기(서버 저장 없음)
            - 앱 이용 기록(투자 내역, 수집 카드, 미션/업적)
              수집 목적: 서비스 제공 및 사용자 경험 유지
              보유 기간: 회원 탈퇴 시까지

            위치정보는 관광지 방문 확인 목적으로만 사용되며, 서버에 전송되거나 저장되지 않습니다. 모든 위치 처리는 사용자 기기 내에서만 이루어집니다.

            3. 개인정보의 제3자 제공
            팀은 사용자의 개인정보를 원칙적으로 외부에 제공하지 않습니다. 단, 아래의 경우는 예외로 합니다.
            - 사용자가 사전에 동의한 경우
            - 법령에 의거하거나 수사기관의 요청이 있는 경우
            소셜 로그인 제공자(카카오)와의 정보 공유는 해당 서비스의 개인정보처리방침을 따릅니다.

            4. 개인정보의 처리 위탁
            수탁자: 카카오(Kakao Corp.)
            위탁 업무: 소셜 로그인 인증 처리

            5. 개인정보 보유 및 파기
            수집된 개인정보는 목적 달성 후 지체 없이 파기합니다. 회원 탈퇴 시 관련 정보는 즉시 삭제되며, 전자적 파일은 복구 불가능한 방법으로 삭제합니다.

            6. 사용자의 권리
            사용자는 언제든지 아래 권리를 행사할 수 있습니다.
            - 개인정보 열람 요청
            - 개인정보 수정 요청
            - 개인정보 삭제 및 회원 탈퇴 요청
            - 개인정보 처리 정지 요청

            7. 앱 서비스 성격 안내
            투어폴리오의 관광지 가상 투자 기능은 실제 금전 거래가 없는 게임형 시뮬레이션입니다. 실제 금융 서비스나 투자 서비스가 아니며, 앱 내 가상 자산은 현금화되지 않습니다.

            8. 개인정보 보호책임자 및 문의
            운영주체: 팀해달별
            이메일: team.haedalbyeol.dev@gmail.com
            개인정보 관련 문의, 열람 청구, 불만 처리 등은 위 이메일로 연락주시면 신속히 처리하겠습니다.

            9. 개인정보처리방침 변경
            본 방침은 법령 또는 서비스 변경에 따라 업데이트될 수 있으며, 변경 시 앱 내 공지 또는 이메일로 안내합니다.
            최초 시행일: 2026년 8월 20일

            © 2026 팀해달별 · 투어폴리오(Tourfolio) · team.haedalbyeol.dev@gmail.com
            """.trimIndent(),
    ),
    LOCATION(
        title = "위치정보 이용 동의",
        content =
            """
            투어폴리오 위치정보 이용 동의

            - 투어폴리오는 관광지 방문 인증 및 포토카드 획득 기능을 위해 위치정보(GPS)를 이용합니다.
            - 위치정보는 이용자의 기기 내에서만 처리되며, 회사의 서버로 전송되거나 저장되지 않습니다.
            - 위치정보는 방문 인증 목적 외 다른 용도로 사용되지 않습니다.
            - 위치정보는 제3자에게 제공되지 않습니다.
            - 위치 권한은 휴대폰 설정에서 언제든지 껐다 켤 수 있으며, 권한을 허용하지 않을 경우 포토카드 수집 기능 이용이 제한될 수 있습니다.
            - 본 동의를 거부할 권리가 있으며, 동의하지 않을 경우 위치 기반 기능(포토카드 수집)의 이용이 제한됩니다.

            © 2026 팀해달별 · 투어폴리오(Tourfolio)
            """.trimIndent(),
    ),
}

@Composable
fun TermsDetailScreen(
    type: SignupTermsType,
    onBackClick: () -> Unit,
    onAgreeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Natural100),
    ) {
        CommonBackHeader(title = type.title, onBackClick = onBackClick)

        Box(
            modifier = Modifier.weight(1f),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Natural90, RoundedCornerShape(12.dp))
                            .padding(16.dp),
                ) {
                    Text(
                        text = type.content,
                        style = LocalAppTypography.current.bodySmall.bold,
                        color = Natural60,
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
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
                label = "동의하기",
                containerColor = Primary,
                onClick = onAgreeClick,
                height = 44.dp,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(
    name = "Terms Detail Screen Preview",
    showBackground = true,
    widthDp = 412,
    heightDp = 800,
)
@Composable
private fun TermsDetailScreenPreview() {
    TourfolioTheme(dynamicColor = false) {
        TermsDetailScreen(
            type = SignupTermsType.TERMS,
            onBackClick = {},
            onAgreeClick = {},
        )
    }
}
