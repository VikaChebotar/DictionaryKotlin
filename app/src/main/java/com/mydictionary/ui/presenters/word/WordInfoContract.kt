package com.mydictionary.ui.presenters.word

import android.os.Bundle
import com.mydictionary.data.entity.Definition
import com.mydictionary.ui.presenters.BasePresenter
import com.mydictionary.ui.presenters.BaseView

/**
 * Created by Viktoria_Chebotar on 6/30/2017.
 */

interface WordInfoPresenter : BasePresenter<WordInfoView> {
    fun onSeeAllDefinitionsBtnClicked(definitionsCount: Int)
    fun onSeeAllExamplesBtnClicked(examplesCount: Int)
}

interface WordInfoView : BaseView {
    fun getExtras(): Bundle
    fun initToolbar(word: String)
    fun showPronunciation(value: String)
    fun showDefinitions(value: List<Definition>, showSeeAllBtn: Boolean)
    fun setSeeAllDefinitionsBtnText(textRes: Int)
    fun showExamples(value: List<String>, showSeeAllBtn: Boolean)
    fun setSeeAllExamplesBtnText(textRes: Int)
}