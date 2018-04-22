package com.mydictionary.data.userwordrepo

import com.mydictionary.data.userwordrepo.pojo.UserWordDto
import com.mydictionary.domain.entity.UserWord

/**
 * Created by Viktoria Chebotar on 22.04.18.
 */
object UserWordMapper {
    fun mapUserWord(userWordDto: UserWordDto) =
            UserWord(userWordDto.word, userWordDto.favSenses)
}