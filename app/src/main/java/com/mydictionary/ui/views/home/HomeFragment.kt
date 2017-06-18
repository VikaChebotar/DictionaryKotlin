package com.mydictionary.ui.views.home

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mydictionary.R
import com.mydictionary.data.entity.WordInfo
import com.mydictionary.ui.DictionaryApp
import com.mydictionary.ui.presenters.HomePresenterImpl
import com.mydictionary.ui.presenters.HomeView
import kotlinx.android.synthetic.main.home_fragment.*

/**
 * Created by Viktoria_Chebotar on 6/1/2017.
 */
class HomeFragment : Fragment(), HomeView {

    val presenter by lazy { HomePresenterImpl(DictionaryApp.getInstance(context).repository) }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.home_fragment, container, false)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onStart(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onStop()
    }

    override fun showProgress(progress: Boolean) {
        progressBar.visibility = if (progress) View.VISIBLE else View.GONE
        homeLayout.visibility = if (progress) View.GONE else View.VISIBLE
    }

    override fun showWordOfTheDay(word: WordInfo) {
        wordOfTheDayCard.bind(word)
    }

    override fun showError(message: String) {
        Snackbar.make(view!!, message, Snackbar.LENGTH_LONG).show()
    }
}