package com.mydictionary.ui.views.learn

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mydictionary.R


/**
 * Created by Viktoria_Chebotar on 3/9/2018.
 */
class LearnCardItemFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.learn_card_item, container, false)
        view.setOnClickListener {  }
        return view
    }
}