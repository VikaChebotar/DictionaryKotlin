package com.mydictionary.presentation.views.learn

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

/**
 * Created by Viktoria_Chebotar on 3/9/2018.
 */
class LearnCardPagerAadapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    var list = listOf<UserWordInfoPresentation>()

    override fun getItem(position: Int) = LearnCardItemFragment.getInstance(list[position])

    override fun getCount() = list.size

    override fun getItemPosition(obj: Any): Int {
        return POSITION_NONE;
    }
}