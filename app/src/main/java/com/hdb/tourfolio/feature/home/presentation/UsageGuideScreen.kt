@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hdb.tourfolio.ui.components.CommonBackHeader
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural40
import com.hdb.tourfolio.ui.theme.Natural99
import com.hdb.tourfolio.ui.theme.Primary
import com.hdb.tourfolio.ui.theme.Primary100
import com.hdb.tourfolio.ui.theme.Primary40
import com.hdb.tourfolio.ui.theme.Primary95
import com.hdb.tourfolio.ui.theme.Primary99

@Composable
fun UsageGuideScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val typography = LocalAppTypography.current
    Column(modifier.fillMaxSize().background(Natural100)) {
        CommonBackHeader(title = "투어폴리오 이용가이드", onBackClick = onBackClick)
        LazyColumn(
            modifier = Modifier.weight(1f).background(Natural99),
            contentPadding = PaddingValues(start = 22.dp, end = 22.dp, top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(Color(0xFFFFEFEB), Primary99)), RoundedCornerShape(24.dp))
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text("TOURFOLIO GUIDE", style = typography.labelSmall.bold, color = Primary40)
                    Text(
                        "관광지에 투자하고,\n여행의 추억을 수집하세요!",
                        style = typography.titleLarge.copy(lineHeight = 34.sp),
                        color = Natural10,
                    )
                    Text("투어폴리오(Tourfolio)에 오신 것을 환영합니다!", style = typography.bodyLarge.bold, color = Natural10, lineHeight = 23.sp)
                    GuideParagraph("투어폴리오는 대한민국의 다양한 관광지를 탐색하고, 관광 데이터 기반 가상 주식에 투자하며, 실제 방문을 통해 특별한 포토카드를 수집하는 참여형 관광 플랫폼입니다.")
                    GuideParagraph("단순히 여행 정보를 찾아보는 것을 넘어, 평소에도 관광지에 관심을 갖고 새로운 여행의 즐거움을 발견할 수 있도록 만들었습니다.")
                }
            }
            items(guideSections, key = { it.number }) { section ->
                GuideSectionCard(section)
            }
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().background(Primary99, RoundedCornerShape(20.dp)).padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text("처음 이용한다면\n이 순서를 추천해요!", style = typography.titleSmall.bold.copy(lineHeight = 28.sp), color = Natural10)
                    listOf(
                        "출석체크로 포인트 받기",
                        "관광지 탐색하기",
                        "관심 있는 관광지에 가상 투자하기",
                        "실제 방문하고 포토카드 수집하기",
                    ).forEachIndexed { index, text ->
                        GuideListRow(text = text, marker = "${index + 1}")
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    Text("여행하지 않는 날에도,\n여행은 계속됩니다.", style = typography.titleMedium.bold.copy(lineHeight = 30.sp), color = Natural10)
                    GuideParagraph("투어폴리오는 관광지를 단순히 방문할 장소가 아니라, 평소에도 관심을 갖고 지켜보며 직접 경험하고 수집할 수 있는 대상으로 만들어갑니다.")
                    Text("탐색 → 투자 → 방문 → 수집", style = typography.bodyLarge.bold, color = Primary40)
                    GuideParagraph("대한민국의 새로운 여행 경험, 지금 투어폴리오에서 시작해 보세요!")
                }
            }
        }
    }
}

@Composable
private fun GuideSectionCard(section: GuideSection) {
    val typography = LocalAppTypography.current
    Column(
        modifier = Modifier.fillMaxWidth().background(Natural100, RoundedCornerShape(20.dp)).padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "${section.number}  ${section.tab}",
            style = typography.labelLarge.bold,
            color = Primary40,
            modifier = Modifier.background(Primary99, RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 8.dp),
        )
        Text(section.title, style = typography.titleSmall.bold.copy(lineHeight = 28.sp), color = Natural10)
        section.paragraphs.forEach { GuideParagraph(it) }
        section.points.forEachIndexed { index, text ->
            GuideListRow(text = text, marker = if (section.numbered) "${index + 1}" else "•")
        }
        section.closing.forEach { GuideParagraph(it) }
        section.notice?.let { notice ->
            Text(
                text = notice,
                style = typography.bodySmall.medium.copy(lineHeight = 22.sp),
                color = Primary40,
                modifier = Modifier.fillMaxWidth().background(Primary99, RoundedCornerShape(12.dp)).padding(16.dp),
            )
        }
    }
}

@Composable
private fun GuideParagraph(text: String) {
    Text(text = text, style = LocalAppTypography.current.bodyLarge.medium.copy(lineHeight = 23.sp), color = Natural40)
}

@Composable
private fun GuideListRow(text: String, marker: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(marker, style = LocalAppTypography.current.bodyLarge.bold.copy(lineHeight = 26.sp), color = Primary)
        Text(text, modifier = Modifier.weight(1f), style = LocalAppTypography.current.bodyLarge.medium.copy(lineHeight = 26.sp), color = Natural10)
    }
}

private data class GuideSection(
    val number: String,
    val tab: String,
    val title: String,
    val paragraphs: List<String>,
    val points: List<String> = emptyList(),
    val closing: List<String> = emptyList(),
    val numbered: Boolean = false,
    val notice: String? = null,
)

private val guideSections = listOf(
    GuideSection(
        number = "01",
        tab = "탐색",
        title = "관광지를 발견하세요",
        paragraphs = listOf("어디로 떠나야 할지 고민된다면 탐색 탭을 확인해 보세요."),
        points = listOf(
            "추천 관광지와 다양한 관광 콘텐츠를 둘러볼 수 있어요.",
            "지역과 테마별 투어 컬렉션을 통해 새로운 여행지를 발견할 수 있어요.",
            "관광지 이름을 검색하고, 사진과 소개, 주소 등 상세 정보를 확인할 수 있어요.",
        ),
        closing = listOf("관심 있는 관광지를 발견했다면 이제 가상 투자에도 도전해 보세요!"),
    ),
    GuideSection(
        number = "02",
        tab = "투자",
        title = "관광지에 투자하세요",
        paragraphs = listOf(
            "투어폴리오에서는 관광지가 하나의 가상 주식 종목이 됩니다.",
            "관광지별 실제 관광 데이터를 반영해 가격이 형성되며, 종목마다 서로 다른 가격 흐름을 보여줍니다.",
        ),
        points = listOf(
            "종목 탐색: 다양한 관광지의 현재 가격과 변동률을 확인해 보세요.",
            "매수·매도: 보유 포인트로 원하는 관광지 종목을 사고팔 수 있어요.",
            "투자 현황: 보유 종목의 평가금액과 수익률을 확인할 수 있어요.",
        ),
        closing = listOf("어떤 관광지가 주목받고 있을까요? 관심 있는 관광지에 투자하며 새로운 시각으로 대한민국을 살펴보세요."),
        notice = "※ 투어폴리오의 투자는 실제 금융투자가 아닌 가상 포인트 기반의 콘텐츠입니다. 실제 주식이나 현금 거래는 이루어지지 않습니다.",
    ),
    GuideSection(
        number = "03",
        tab = "수집",
        title = "방문하고 수집하세요",
        paragraphs = listOf(
            "투어폴리오의 포토카드는 단순히 화면을 터치하는 것만으로 얻을 수 없습니다.",
            "해당 관광지를 실제로 방문하고 위치 인증을 완료해야 포토카드를 획득할 수 있어요!",
        ),
        points = listOf(
            "수집 탭에서 원하는 관광지 포토카드를 선택하세요.",
            "카드의 관광지 위치와 획득 조건을 확인하세요.",
            "실제 관광지를 방문한 뒤 ‘카드 획득하기’를 눌러주세요.",
            "위치 인증에 성공하면 포토카드가 해금됩니다!",
        ),
        closing = listOf(
            "획득한 카드는 나만의 컬렉션에 쌓이며, 지역·테마·희귀도별로 확인할 수 있습니다.",
            "여행의 발자취를 특별한 디지털 컬렉션으로 남겨보세요.",
        ),
        numbered = true,
    ),
    GuideSection(
        number = "04",
        tab = "업적",
        title = "매일 새로운 목표에 도전하세요",
        paragraphs = listOf("여행을 떠나지 않는 날에도 투어폴리오를 즐길 수 있습니다."),
        points = listOf(
            "출석체크: 매일 출석하고 포인트를 획득하세요.",
            "미션·업적: 다양한 활동 목표에 도전해 보세요.",
            "포인트 리워드: 미션을 달성하고 보상을 받아보세요.",
            "포인트 내역: 적립된 포인트를 확인할 수 있어요.",
        ),
        closing = listOf("차곡차곡 쌓은 포인트로 새로운 관광지 가상 투자에도 도전해 보세요!"),
    ),
    GuideSection(
        number = "05",
        tab = "홈",
        title = "나만의 투어폴리오를 만들어 보세요",
        paragraphs = listOf(
            "홈에서는 나의 투자 현황과 보유 포인트를 한눈에 확인할 수 있습니다.",
            "투어폴리오의 새로운 소식과 다양한 콘텐츠도 홈에서 만나보세요.",
        ),
    ),
)
