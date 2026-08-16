@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.feature.explore.ExploreHubTrendingSpotUiModel
import com.hdb.tourfolio.feature.explore.model.TagType
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural60

@Composable
fun SearchHomeSections(
    recommendedTags: List<TagType>,
    popularKeywords: List<String>,
    recommendedSpots: List<ExploreHubTrendingSpotUiModel>,
    onTagClick: (TagType) -> Unit,
    onKeywordClick: (String) -> Unit,
    onSpotClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        SearchSectionTitle(title = "태그별 추천")

        Spacer(modifier = Modifier.height(16.dp))

        RecommendedTagSection(
            tags = recommendedTags,
            onTagClick = onTagClick,
        )

        Spacer(modifier = Modifier.height(40.dp))

        SearchSectionTitle(title = "인기 검색어")

        Spacer(modifier = Modifier.height(18.dp))

        PopularKeywordSection(
            keywords = popularKeywords,
            onKeywordClick = onKeywordClick,
        )

        Spacer(modifier = Modifier.height(40.dp))

        SearchSectionTitle(title = "추천 관광지")

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            recommendedSpots.forEach { item ->
                SearchRecommendedSpotCard(
                    item = item,
                    onClick = onSpotClick,
                )
            }
        }
    }
}

@Composable
private fun RecommendedTagSection(
    tags: List<TagType>,
    onTagClick: (TagType) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        tags.forEach { tag ->
            Box(
                modifier =
                    Modifier
                        .background(
                            color = Color(0xFFF8F8F8),
                            shape = RoundedCornerShape(20.dp),
                        )
                        .clickable {
                            onTagClick(tag)
                        }
                        .padding(
                            horizontal = 14.dp,
                            vertical = 9.dp,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "#${tag.displayName}",
                    style =
                        LocalAppTypography.current.bodySmall.bold.copy(
                            color = Color(0xFF9B9B9B),
                        ),
                )
            }
        }
    }
}

@Composable
private fun PopularKeywordSection(
    keywords: List<String>,
    onKeywordClick: (String) -> Unit,
) {
    val middleIndex = (keywords.size + 1) / 2
    val leftKeywords = keywords.take(middleIndex)
    val rightKeywords = keywords.drop(middleIndex)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(30.dp),
    ) {
        PopularKeywordColumn(
            keywords = leftKeywords,
            startRank = 1,
            onKeywordClick = onKeywordClick,
            modifier = Modifier.weight(1f),
        )

        PopularKeywordColumn(
            keywords = rightKeywords,
            startRank = middleIndex + 1,
            onKeywordClick = onKeywordClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun PopularKeywordColumn(
    keywords: List<String>,
    startRank: Int,
    onKeywordClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        keywords.forEachIndexed { index, keyword ->
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            onKeywordClick(keyword)
                        },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${startRank + index}",
                    style =
                        LocalAppTypography.current.bodyLarge.bold.copy(
                            color = Natural10,
                        ),
                    modifier = Modifier.width(30.dp),
                )

                Text(
                    text = keyword,
                    style =
                        LocalAppTypography.current.bodyLarge.medium.copy(
                            color = Natural60,
                        ),
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun SearchSectionTitle(title: String) {
    Text(
        text = title,
        style =
            LocalAppTypography.current.titleMedium.bold.copy(
                color = Natural10,
            ),
    )
}
