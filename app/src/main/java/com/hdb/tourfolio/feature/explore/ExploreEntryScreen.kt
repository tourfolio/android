package com.hdb.tourfolio.feature.explore

import androidx.compose.runtime.Composable

@Composable
fun ExploreEntryScreen(
    introFinished: Boolean,
    onIntroFinished: () -> Unit,
    onIntroTourSpotClick: (Long) -> Unit,
    onCityTravelClick: (Long) -> Unit,
    onTourSpotClick: (Long) -> Unit,
    onSearchClick: () -> Unit,
) {
    if (introFinished) {
        ExploreScreen(
            onCityTravelClick = onCityTravelClick,
            onTourSpotClick = onTourSpotClick,
            onSearchClick = onSearchClick,
        )
    } else {
        ExploreCarouselScreen(
            /*
             * 돋보기 버튼을 누르거나
             * 캐러셀 마지막 페이지에 도착했을 때 실행됩니다.
             */
            onFinished = onIntroFinished,
            /*
             * 인트로 관광지를 클릭했을 때는
             * introFinished를 변경하지 않고 상세 화면으로 이동합니다.
             *
             * 따라서 상세 화면으로 이동하는 과정에서
             * ExploreScreen이 중간에 나타나지 않습니다.
             */
            onTourSpotClick = onIntroTourSpotClick,
        )
    }
}