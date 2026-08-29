package com.hdb.tourfolio.data.mypage.remote

import com.hdb.tourfolio.data.common.network.Authenticated
import com.hdb.tourfolio.data.mypage.remote.dto.MyPageResponseDto
import com.hdb.tourfolio.data.mypage.remote.dto.UpdateNicknameRequestDto
import com.hdb.tourfolio.data.mypage.remote.dto.UpdateNicknameResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT

interface MyPageApiService {
    /*
     * 마이페이지 요약 조회
     */
    @Authenticated
    @GET("api/v1/mypage")
    suspend fun getMyPage(): MyPageResponseDto

    /*
     * 회원 탈퇴
     */
    @Authenticated
    @DELETE("api/v1/mypage")
    suspend fun deleteAccount()

    /*
     * 닉네임 수정
     */
    @Authenticated
    @PUT("api/v1/user/profile")
    suspend fun updateNickname(
        @Body request: UpdateNicknameRequestDto,
    ): UpdateNicknameResponseDto
}
