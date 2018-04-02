package com.mydictionary.ui.views.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.RecognizerIntent.*
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.mydictionary.R
import com.mydictionary.commons.VOICE_RECOGNITION_CODE
import com.mydictionary.commons.VOICE_SEARCH_EXTRA
import com.mydictionary.commons.VOICE_SEARCH_PAUSE_MILLIS
import com.mydictionary.ui.DictionaryApp
import com.mydictionary.ui.presenters.search.SearchPresenterImpl
import com.mydictionary.ui.presenters.search.SearchView
import com.mydictionary.ui.views.word.WordInfoActivity
import kotlinx.android.synthetic.main.search_activity.*
import java.util.*


/**
 * Created by Viktoria_Chebotar on 6/20/2017.
 */
class SearchActivity : AppCompatActivity(), SearchView, SearchEditText.VoiceButtonListener {

    val presenter by lazy { SearchPresenterImpl(DictionaryApp.getInstance(this).repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_activity)
        searchField.contentChangedListener = presenter
        searchField.voiceRecognitionListener = this
        searchField.requestFocus()
        with(searchRecyclerView) {
            layoutManager = LinearLayoutManager(context) as RecyclerView.LayoutManager?
            val dividerDecoration = DividerItemDecoration(context, ContextCompat.getDrawable(context,
                    R.drawable.divider_with_padding)!!)
            addItemDecoration(dividerDecoration)
            adapter = SearchResultsAdapter({ value -> onItemClick(value) })
            isNestedScrollingEnabled = false
        }
        presenter.onStart(this)
        if (intent.extras.getBoolean(VOICE_SEARCH_EXTRA)) {
            startVoiceRecognition()
        }
    }

    private fun onItemClick(searchWord: String) {
        WordInfoActivity.startActivity(this, searchWord)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onStop()
        searchField.contentChangedListener = null
        searchField.voiceRecognitionListener = null
    }

    override fun showProgress(progress: Boolean) {

    }

    override fun showError(message: String) {
        Snackbar.make(searchScrollContent!!, message, Snackbar.LENGTH_LONG).show()
    }

    override fun showHistoryWords(list: List<String>) {
        (searchRecyclerView.adapter as SearchResultsAdapter).setList(list, true)
        moreHistoryBtn.visibility = if (list.isNotEmpty()) View.VISIBLE else View.GONE
    }

    override fun showSearchResult(list: List<String>) {
        (searchRecyclerView.adapter as SearchResultsAdapter).setList(list, false)
        moreHistoryBtn.visibility = View.GONE
    }

    override fun finishView() {
        onBackPressed()
    }

    override fun startVoiceRecognition() {
        val intent = Intent(ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(EXTRA_LANGUAGE_MODEL, LANGUAGE_MODEL_WEB_SEARCH)
        intent.putExtra(EXTRA_PROMPT, getString(R.string.speak_now))
        intent.putExtra(EXTRA_LANGUAGE, Locale.US)
        intent.putExtra(EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, VOICE_SEARCH_PAUSE_MILLIS);
        startActivityForResult(intent, VOICE_RECOGNITION_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VOICE_RECOGNITION_CODE && resultCode == Activity.RESULT_OK) {
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (matches?.isNotEmpty() == true) {
                searchField.setText(matches[0])
                searchField.setSelection(matches[0].length)
            }
        }
    }
    override fun getContext() = this
}