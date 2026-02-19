package com.example.stayease.domain.usecase
import com.example.stayease.core.result.AppResult
import com.example.stayease.domain.model.Stay
import com.example.stayease.domain.repository.StayRepository
import javax.inject.Inject
class GetStayDetailsUseCase @Inject constructor(private val repo: StayRepository) {
  suspend operator fun invoke(id: Long): AppResult<Stay> = repo.getStayDetails(id)
}
