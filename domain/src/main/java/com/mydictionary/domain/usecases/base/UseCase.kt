package com.mydictionary.domain.usecases.base

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single


interface UseCase<R> {
    fun execute(): Observable<R>
}

interface UseCaseWithParameter<P, R> {
    fun execute(parameter: P): Observable<R>
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