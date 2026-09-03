package com.hdb.tourfolio.feature.explore.presentation

import com.hdb.tourfolio.core.mvi.MviEffect
import com.hdb.tourfolio.core.mvi.MviIntent
import com.hdb.tourfolio.core.mvi.MviState
import com.hdb.tourfolio.core.mvi.MviViewModel
import com.hdb.tourfolio.domain.explore.usecase.GetExploreCollectionDetailUseCase
import com.hdb.tourfolio.feature.explore.presentation.model.ExploreCollectionDetailUiModel
import com.hdb.tourfolio.feature.explore.presentation.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

sealed interface CollectionDetailUiState {
    data object Loading : CollectionDetailUiState

    data class Success(
        val detail: ExploreCollectionDetailUiModel,
    ) : CollectionDetailUiState

    data class Error(
        val message: String,
    ) : CollectionDetailUiState
}

sealed interface CollectionDetailIntent : MviIntent {
    data class FetchCollectionDetail(
        val collectionId: Long,
    ) : CollectionDetailIntent
}

data class CollectionDetailState(
    val detail: CollectionDetailUiState =
        CollectionDetailUiState.Loading,
) : MviState

sealed interface CollectionDetailEffect : MviEffect

@HiltViewModel
class CollectionDetailViewModel
    @Inject
    constructor(
        private val getExploreCollectionDetailUseCase: GetExploreCollectionDetailUseCase,
    ) : MviViewModel<
            CollectionDetailIntent,
            CollectionDetailState,
            CollectionDetailEffect,
            >(
            CollectionDetailState(),
        ) {
        override suspend fun handleIntent(intent: CollectionDetailIntent) {
            when (intent) {
                is CollectionDetailIntent.FetchCollectionDetail ->
                    fetchCollectionDetail(
                        collectionId =
                            intent.collectionId,
                    )
            }
        }

        private suspend fun fetchCollectionDetail(collectionId: Long) {
            setState {
                copy(
                    detail =
                        CollectionDetailUiState.Loading,
                )
            }

            val result =
                try {
                    CollectionDetailUiState.Success(
                        detail =
                            getExploreCollectionDetailUseCase(
                                collectionId,
                            ).toUiModel(),
                    )
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    CollectionDetailUiState.Error(
                        message =
                            e.message
                                ?: "컬렉션 정보를 불러오지 못했습니다.",
                    )
                }

            setState {
                copy(
                    detail = result,
                )
            }
        }
    }
