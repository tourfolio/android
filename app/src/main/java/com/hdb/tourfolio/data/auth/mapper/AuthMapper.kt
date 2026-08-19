package com.hdb.tourfolio.data.auth.mapper

import com.hdb.tourfolio.data.auth.remote.dto.AuthResponseDto
import com.hdb.tourfolio.domain.auth.model.User

fun AuthResponseDto.toDomain(): User =
    User(
        id = id,
        email = email,
        nickname = nickname,
    )
