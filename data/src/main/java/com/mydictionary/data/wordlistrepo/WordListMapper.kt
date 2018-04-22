package com.mydictionary.data.wordlistrepo

import com.mydictionary.data.wordlistrepo.pojo.WordListDto
import com.mydictionary.domain.entity.WordList

/**
 * Created by Viktoria Chebotar on 22.04.18.
 */
object WordListMapper {
    fun mapWordList(wordListDto: WordListDto) =
            WordList(wordListDto.name, wordListDto.type, wordListDto.list)

    fun mapListOfWordList(list: List<WordListDto>) = list.map { mapWordList(it) }
}