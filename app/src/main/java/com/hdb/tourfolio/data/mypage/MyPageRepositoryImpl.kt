package com.hdb.tourfolio.data.mypage

import com.hdb.tourfolio.data.auth.local.SessionLocalDataSource
import com.hdb.tourfolio.data.mypage.mapper.toDomain
import com.hdb.tourfolio.data.mypage.remote.MyPageApiService
import com.hdb.tourfolio.data.mypage.remote.dto.UpdateNicknameRequestDto
import com.hdb.tourfolio.domain.mypage.model.MyPage
import com.hdb.tourfolio.domain.mypage.model.MyPageException
import com.hdb.tourfolio.domain.mypage.repository.MyPageRepository
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyPageRepositoryImpl
    @Inject
    constructor(
        private val myPageApiService: MyPageApiService,
        private val sessionLocalDataSource: SessionLocalDataSource,
    ) : MyPageRepository {
        override suspend fun getMyPage(): MyPage =
            myPageApiService
                .getMyPage()
                .toDomain()

        override suspend fun updateNickname(nickname: String): String {
            try {
                val response =
                    myPageApiService.updateNickname(
                        UpdateNicknameRequestDto(
                            nickname = nickname,
                        ),
                    )

                return response.nickname
            } catch (e: HttpException) {
                when (e.code()) {
                    400 ->
                        throw MyPageException.InvalidNicknameException()

                    404 ->
                        throw MyPageException.UserNotFoundException()

                    else ->
                        throw e
                }
            }
        }

        override suspend fun deleteAccount() {
            myPageApiService.deleteAccount()

        /*
         * 탈퇴 성공 후 더 이상 현재 세션을 유지하면 안 되므로
         * 로컬 로그인 정보까지 삭제
         */
            sessionLocalDataSource.clear()
        }
    }
