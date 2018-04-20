package com.mydictionary.presentation.views.search

import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.RecognizerIntent.*
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.Transition
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import com.mydictionary.R
import com.mydictionary.commons.*
import com.mydictionary.presentation.Data
import com.mydictionary.presentation.DataState
import com.mydictionary.presentation.DictionaryApp
import com.mydictionary.presentation.viewmodel.search.SearchResult
import com.mydictionary.presentation.viewmodel.search.SearchViewModel
import com.mydictionary.presentation.views.word.WordInfoActivity
import kotlinx.android.synthetic.main.search_activity.*
import java.util.*
import javax.inject.Inject


/**
 * Created by Viktoria_Chebotar on 6/20/2017.
 */
class SearchActivity : AppCompatActivity(), SearchEditText.VoiceButtonListener,
    SearchEditText.ContentChangedListener {
    private var shouldAnimateList = true
    private var isRestarted = false
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val viewModel by lazy {getViewModel<SearchViewModel>(viewModelFactory)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isRestarted = savedInstanceState != null
        setContentView(R.layout.search_activity)
        DictionaryApp.component.inject(this)
        fixSharedElementTransitionForStatusBar()
        searchField.contentChangedListener = this
        searchField.voiceRecognitionListener = this
        with(searchRecyclerView) {
            layoutManager = LinearLayoutManager(context) as RecyclerView.LayoutManager?
            val dividerDecoration = DividerItemDecoration(
                context, ContextCompat.getDrawable(context, R.drawable.divider_with_padding)!!
            )
            addItemDecoration(dividerDecoration)
            adapter = SearchResultsAdapter({ value -> onItemClick(value) })
            isNestedScrollingEnabled = false
        }
        if (intent.extras.getBoolean(VOICE_SEARCH_EXTRA)) {
            startVoiceRecognition()
        }
        viewModel.searchResultList.observe(this, android.arch.lifecycle.Observer {
            showSearchResult(it)
        })
    }

    private fun onItemClick(searchWord: String) {
        WordInfoActivity.startActivity(this, searchWord)
    }

    override fun onDestroy() {
        super.onDestroy()
        searchField.contentChangedListener = null
        searchField.voiceRecognitionListener = null
    }

    fun showError(message: String) {
        Snackbar.make(searchScrollContent, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showSearchResult(data: Data<SearchResult>?) {
        when (data?.dataState) {
            DataState.SUCCESS -> {
                data.data?.let {
                    if (shouldAnimateList) animateList()
                    (searchRecyclerView.adapter as SearchResultsAdapter).setList(
                        it.list,
                        it.isHistory
                    )
                }
            }
            DataState.ERROR -> {
                showError(data.message ?: getString(R.string.default_error))
            }
            DataState.LOADING -> {
            }
        }
    }

    private fun animateList() {
        searchScrollContent.visibility = View.VISIBLE
        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        searchScrollContent.startAnimation(animation)
        shouldAnimateList = false
    }

    override fun startVoiceRecognition() {
        val intent = Intent(ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(EXTRA_LANGUAGE_MODEL, LANGUAGE_MODEL_WEB_SEARCH)
        intent.putExtra(EXTRA_PROMPT, getString(R.string.speak_now))
        intent.putExtra(EXTRA_LANGUAGE, Locale.US)
        intent.putExtra(
            EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
            VOICE_SEARCH_PAUSE_MILLIS
        );
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

    override fun onSearchLetterEntered(msg: String) {
        viewModel.onSearchLetterEntered(msg)
    }

    override fun onSearchCleared() {
        viewModel.onSearchCleared()

    }

    override fun onSearchClosed() {
        if (isRestarted) finish() else
            supportFinishAfterTransition()
    }

    private fun fixSharedElementTransitionForStatusBar() {
        // Postpone the transition until the window's decor view has
        // finished its layout.
        postponeEnterTransition()

        val decor = window.decorView
        decor.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                decor.viewTreeObserver.removeOnPreDrawListener(this)
                startPostponedEnterTransition()
                return true
            }
        })
        window.sharedElementEnterTransition.addListener(object : Transition.TransitionListener {
            override fun onTransitionEnd(transition: Transition?) {
                searchField.showKeyboard()
                window.sharedElementEnterTransition.removeListener(this)
            }

            override fun onTransitionResume(transition: Transition?) {
            }

            override fun onTransitionPause(transition: Transition?) {
            }

            override fun onTransitionCancel(transition: Transition?) {
            }

            override fun onTransitionStart(transition: Transition?) {
            }

        })
    }
}