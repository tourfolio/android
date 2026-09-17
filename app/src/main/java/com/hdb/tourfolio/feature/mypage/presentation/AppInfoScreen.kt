@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.mypage.presentation

import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.ui.components.CommonBackHeader
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural100
import com.hdb.tourfolio.ui.theme.Natural90

enum class AppInfoDocument(
    val title: String,
    @RawRes val contentRes: Int,
) {
    LOCATION("위치정보이용 안내", R.raw.location_notice),
    TERMS("이용약관", R.raw.terms_of_service),
    PRIVACY("개인정보처리방침", R.raw.privacy_policy),
}

@Composable
fun AppInfoScreen(
    document: AppInfoDocument,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val resources = LocalContext.current.resources
    val paragraphs =
        remember(document, resources) {
            resources.openRawResource(document.contentRes).bufferedReader(Charsets.UTF_8).use { it.readText() }
                .trim().split(Regex("\\r?\\n\\s*\\r?\\n"))
        }
    val typography = LocalAppTypography.current
    Column(modifier.fillMaxSize().background(Natural100)) {
        CommonBackHeader(title = document.title, onBackClick = onBackClick)
        SelectionContainer(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(22.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                itemsIndexed(paragraphs) { index, paragraph ->
                    if (paragraph.startsWith("|")) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            paragraph.lines().filterNot { it.contains("---") }.forEachIndexed { rowIndex, row ->
                                val cells = row.trim().trim('|').split('|').map { it.trim() }
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    cells.forEachIndexed { cellIndex, cell ->
                                        Text(
                                            text = cell,
                                            modifier = Modifier.weight(if (cellIndex == 0) 1f else 3f),
                                            style = if (rowIndex == 0) typography.bodyLarge.bold else typography.bodyLarge.medium,
                                            color = Natural10,
                                        )
                                    }
                                }
                                HorizontalDivider(color = Natural90)
                            }
                        }
                    } else {
                        val heading = index == 0 || paragraph.startsWith("**") || paragraph.startsWith("1. ")
                        Text(
                            text = paragraph.removeSurrounding("**"),
                            style = if (heading) typography.bodyLarge.bold else typography.bodyLarge.medium,
                            color = Natural10,
                            fontSize = if (index == 0) 22.sp else 16.sp,
                            lineHeight = if (index == 0) 32.sp else 26.sp,
                        )
                    }
                }
            }
        }
    }
}
