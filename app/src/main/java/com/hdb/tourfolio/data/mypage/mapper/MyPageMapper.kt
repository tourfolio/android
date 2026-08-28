package com.hdb.tourfolio.data.mypage.mapper

import com.hdb.tourfolio.data.mypage.remote.dto.MyPageResponseDto
import com.hdb.tourfolio.domain.mypage.model.MyPage

fun MyPageResponseDto.toDomain(): MyPage =
    MyPage(
        nickname = nickname,
        balance = balance,
        cardCount = cardCount,
        totalProfitRate = totalProfitRate,
    )