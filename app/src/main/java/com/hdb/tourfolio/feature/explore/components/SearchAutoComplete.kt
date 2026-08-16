@file:Suppress("ktlint:standard:function-naming")

package com.hdb.tourfolio.feature.explore.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.hdb.tourfolio.R
import com.hdb.tourfolio.ui.theme.LocalAppTypography
import com.hdb.tourfolio.ui.theme.Natural10
import com.hdb.tourfolio.ui.theme.Natural90
import com.hdb.tourfolio.ui.theme.Primary

@Composable
fun SearchAutocompleteContent(
    query: String,
    keywords: List<String>,
    onKeywordClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxSize(),
        contentPadding =
            PaddingValues(
                start = 22.dp,
                top = 0.dp,
                end = 22.dp,
                bottom = 32.dp,
            ),
    ) {
        items(
            items = keywords,
            key = { keyword ->
                keyword
            },
        ) { keyword ->
            SearchAutocompleteItem(
                query = query,
                keyword = keyword,
                onClick = {
                    onKeywordClick(keyword)
                },
            )

            HorizontalDivider(
                color = Natural90,
            )
        }
    }
}

@Composable
private fun SearchAutocompleteItem(
    query: String,
    keyword: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(
                    vertical = 18.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter =
                painterResource(
                    id = R.drawable.ic_search_gray,
                ),
            contentDescription = null,
            modifier = Modifier.size(22.dp),
        )

        Spacer(
            modifier = Modifier.width(12.dp),
        )

        Text(
            text =
                buildHighlightedKeyword(
                    keyword = keyword,
                    query = query,
                ),
            style =
                LocalAppTypography.current.bodyLarge.medium.copy(
                    color = Natural10,
                ),
        )
    }
}

private fun buildHighlightedKeyword(
    keyword: String,
    query: String,
): AnnotatedString {
    val normalizedQuery =
        query
            .trim()
            .removePrefix("#")

    if (normalizedQuery.isBlank()) {
        return AnnotatedString(keyword)
    }

    return buildAnnotatedString {
        var currentIndex = 0

        while (currentIndex < keyword.length) {
            val matchedIndex =
                keyword.indexOf(
                    string = normalizedQuery,
                    startIndex = currentIndex,
                    ignoreCase = true,
                )

            if (matchedIndex < 0) {
                append(
                    keyword.substring(
                        startIndex = currentIndex,
                    ),
                )
                break
            }

            append(
                keyword.substring(
                    startIndex = currentIndex,
                    endIndex = matchedIndex,
                ),
            )

            val matchedEndIndex =
                matchedIndex + normalizedQuery.length

            withStyle(
                style =
                    SpanStyle(
                        color = Primary,
                        fontWeight = FontWeight.Bold,
                    ),
            ) {
                append(
                    keyword.substring(
                        startIndex = matchedIndex,
                        endIndex = matchedEndIndex,
                    ),
                )
            }

            currentIndex = matchedEndIndex
        }
    }
}
