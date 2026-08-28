package com.hdb.tourfolio.domain.mypage.repository

import com.hdb.tourfolio.domain.mypage.model.MyPage

interface MyPageRepository {
    suspend fun getMyPage(): MyPage

    suspend fun updateNickname(
        nickname: String,
    ): String

    suspend fun deleteAccount()
}
