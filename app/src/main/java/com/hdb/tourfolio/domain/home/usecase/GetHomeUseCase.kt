package com.hdb.tourfolio.domain.home.usecase

import com.hdb.tourfolio.domain.home.model.Home
import com.hdb.tourfolio.domain.home.repository.HomeRepository
import javax.inject.Inject

class GetHomeUseCase
    @Inject
    constructor(
        private val homeRepository: HomeRepository,
    ) {
        suspend operator fun invoke(): Home = homeRepository.getHome()
    }
