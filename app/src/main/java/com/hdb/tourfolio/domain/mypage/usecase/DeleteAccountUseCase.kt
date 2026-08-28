package com.hdb.tourfolio.domain.mypage.usecase

import com.hdb.tourfolio.domain.mypage.repository.MyPageRepository
import javax.inject.Inject

class DeleteAccountUseCase
@Inject
constructor(
    private val myPageRepository: MyPageRepository,
) {
    suspend operator fun invoke() {
        myPageRepository.deleteAccount()
    }
}