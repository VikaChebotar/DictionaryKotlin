package com.mydictionary.ui.views.learn

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.mydictionary.R
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.ui.DictionaryApp
import com.mydictionary.ui.presenters.learn.LearnWordsPresenterImpl
import com.mydictionary.ui.presenters.learn.LearnWordsView
import kotlinx.android.synthetic.main.learn_activity.*


/**
 * Created by Viktoria_Chebotar on 3/9/2018.
 */
class LearnActivity : AppCompatActivity(), LearnWordsView {
    val presenter by lazy { LearnWordsPresenterImpl(DictionaryApp.getInstance(this).repository, this) }
    val space by lazy { resources.getDimension(R.dimen.cards_view_pager_margin).toInt() }
    val adapter = LearnCardPagerAadapter(supportFragmentManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.learn_activity);
        setSupportActionBar(toolbar)

        favWordsList.adapter = adapter
        favWordsList.pageMargin = space / 3

        presenter.onStart(this)

        favWordsList.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                presenter.onItemSelected(position)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onStop()
    }

    override fun showProgress(progress: Boolean) {

    }

    override fun showError(message: String) {
    }

    override fun showFavoriteWords(list: List<WordDetails>, needToReset: Boolean) {
        if (needToReset) {
            adapter.list.clear()
            //  scrollListener?.resetState();
        }
        adapter.list.addAll(list)
        adapter.notifyDataSetChanged()
    }
}