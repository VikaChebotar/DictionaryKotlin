package com.mydictionary.domain.usecases.base

import com.mydictionary.domain.entity.Result
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single


interface UseCase<R> {
    fun execute(): Flowable<R>
}

interface UseCaseWithParameter<P, R> {
    fun execute(parameter: P): Flowable<R>
}

interface SingleUseCase<T> {
    fun execute(): Single<T>
}

interface SingleUseCaseWithParameter<P, R> {
    fun execute(parameter: P): Single<R>
}

interface CompletableUseCase {
    fun execute(): Completable
}

interface CompletableUseCaseWithParameter<P> {
    fun execute(parameter: P): Completable
}

interface SuspendUseCase {
    suspend fun execute()
}

interface SuspendUseCaseWithResultAndParameter<P, out R : Any> {
    suspend fun execute(parameter: P): Result<R>
}

interface SuspendUseCaseWithResult<out R : Any> {
    suspend fun execute(): Result<R>
}

interface SuspendUseCaseWithParameter<P> {
    suspend fun execute(parameter: P)
}
