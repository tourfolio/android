package com.hdb.tourfolio.domain.home.repository

import com.hdb.tourfolio.domain.home.model.Home

interface HomeRepository {
    suspend fun getHome(): Home
}