package com.hdb.tourfolio.domain.mypage.usecase

import com.hdb.tourfolio.domain.mypage.model.MyPage
import com.hdb.tourfolio.domain.mypage.repository.MyPageRepository
import javax.inject.Inject

class GetMyPageUseCase
    @Inject
    constructor(
        private val myPageRepository: MyPageRepository,
    ) {
        suspend operator fun invoke(): MyPage = myPageRepository.getMyPage()
    }
