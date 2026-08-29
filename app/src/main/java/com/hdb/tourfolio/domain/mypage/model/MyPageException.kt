package com.hdb.tourfolio.domain.mypage.model

sealed class MyPageException(
    message: String,
) : Exception(message) {
    class InvalidNicknameException :
        MyPageException(
            "이미 사용 중이거나 사용할 수 없는 닉네임입니다.",
        )

    class UserNotFoundException :
        MyPageException(
            "회원 정보를 찾을 수 없습니다.",
        )
}
