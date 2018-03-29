package com.mydictionary.ui.views.learn

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.mydictionary.R
import com.mydictionary.data.pojo.SortingOption
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.ui.DictionaryApp
import com.mydictionary.ui.presenters.learn.LearnWordsPresenterImpl
import com.mydictionary.ui.presenters.learn.LearnWordsView
import com.mydictionary.ui.views.word.WordInfoActivity
import kotlinx.android.synthetic.main.learn_activity.*


/**
 * Created by Viktoria_Chebotar on 3/9/2018.
 */
class LearnActivity : AppCompatActivity(), LearnWordsView, LearnCardItemFragment.OnCardItemListener, Listener {
    val presenter by lazy { LearnWordsPresenterImpl(DictionaryApp.getInstance(this).repository) }
    val space by lazy { resources.getDimension(R.dimen.cards_view_pager_margin).toInt() }
    val adapter = LearnCardPagerAadapter(supportFragmentManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.learn_activity);
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        favWordsPager.adapter = adapter
        favWordsPager.pageMargin = space

        presenter.onStart(this)

        favWordsPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                presenter.onItemSelected(position)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.learn_cards_menu, menu)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onStop()
    }

    override fun showProgress(progress: Boolean) {
        progressBar.visibility = if (progress) View.VISIBLE else View.GONE
    }

    override fun showError(message: String) {
        Snackbar.make(favWordsPager, message, Snackbar.LENGTH_LONG).show()
    }

    override fun showPositionText(text: String) {
        positionLabel.text = text
    }

    override fun showFavoriteWords(list: List<WordDetails>) {
        adapter.list = list
        adapter.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_sort -> {
                presenter.onSortMenuClicked()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDetailsClicked(word: WordDetails) {
        WordInfoActivity.startActivity(this, word.word)
    }

    override fun onDeleteClicked(word: WordDetails) {
        presenter.onItemDeleteClicked(word)
    }

    override fun showWordDeletedMessage(oldWordDetails: WordDetails, favMeanings: List<String>, position: Int) {
        Snackbar.make(favWordsPager, getString(R.string.word_removed, oldWordDetails.word), Snackbar.LENGTH_LONG).
                setAction(getString(R.string.undo), { presenter.onUndoDeletionClicked(oldWordDetails, favMeanings, position) }).show();
    }

    override fun getContext() = this

    override fun onSortItemSelected(item: SortingOption) {
        presenter.onSortSelected(item)
    }

    override fun showSortingDialog(selectedSortingOption: SortingOption) {
        if (adapter.list.isEmpty()) return
        val dialog = LearnSortOptionsDialog.getInstance(selectedSortingOption)
        dialog.show(supportFragmentManager, LearnSortOptionsDialog::class.java.name)
        dialog.listener = this
    }

    override fun getSelectedPosition() = favWordsPager.currentItem
}