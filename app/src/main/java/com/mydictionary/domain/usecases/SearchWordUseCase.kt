package com.mydictionary.domain.usecases

import com.mydictionary.commons.AUTOCOMPLETE_DELAY
import com.mydictionary.commons.MIN_WORD_LENGTH_TO_SEARCH
import com.mydictionary.domain.repository.WordRepository
import com.mydictionary.domain.usecases.base.UseCaseWithParameter
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchWordUseCase @Inject constructor(val wordRepository: WordRepository) :
        UseCaseWithParameter<PublishProcessor<String>, List<String>> {

    override fun execute(publisher: PublishProcessor<String>): Flowable<List<String>> {
        return publisher
                .onBackpressureDrop()
                .filter { searchPhrase: String -> !searchPhrase.isEmpty() && searchPhrase.length >= MIN_WORD_LENGTH_TO_SEARCH }
                .debounce(AUTOCOMPLETE_DELAY, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .switchMap {
                    wordRepository.searchWord(it).toFlowable()
                            .onErrorReturn { emptyList() }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }


}