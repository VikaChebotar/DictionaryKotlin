package com.mydictionary.domain.usecases

import com.mydictionary.domain.AUTOCOMPLETE_DELAY
import com.mydictionary.domain.MIN_WORD_LENGTH_TO_SEARCH
import com.mydictionary.domain.SEARCH_LIMIT
import com.mydictionary.domain.repository.WordRepository
import com.mydictionary.domain.usecases.base.UseCaseWithParameter
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.processors.PublishProcessor
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class SearchWordUseCase @Inject constructor(val wordRepository: WordRepository,
                                            @Named("executor_thread") val executorThread: Scheduler,
                                            @Named("ui_thread") val uiThread: Scheduler) :
        UseCaseWithParameter<PublishProcessor<String>, List<String>> {

    override fun execute(publisher: PublishProcessor<String>): Flowable<List<String>> {
        return publisher
                .onBackpressureDrop()
                .filter { searchPhrase: String ->
                    !searchPhrase.isEmpty()
                            && searchPhrase.length >= MIN_WORD_LENGTH_TO_SEARCH
                }
                .debounce(AUTOCOMPLETE_DELAY, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .switchMap {
                    wordRepository.searchWord(it, SEARCH_LIMIT).toFlowable()
                            .onErrorReturn { emptyList() }
                }
                .subscribeOn(executorThread)
                .observeOn(uiThread)
    }


}