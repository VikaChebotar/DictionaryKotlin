package com.mydictionary.domain.usecases

import com.mydictionary.domain.repository.UserRepository
import com.mydictionary.domain.usecases.base.SuspendUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignOutUseCase @Inject constructor(
    val userRepository: UserRepository) : SuspendUseCase {

    override suspend fun execute() = userRepository.signOut()
}