package com.mydictionary.domain.usecases

import com.mydictionary.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShowUserUseCase @Inject constructor(
    val userRepository: UserRepository) {

    suspend fun execute() = userRepository.getUser()
}