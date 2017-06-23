package com.mydictionary.ui.views.home

import android.support.v4.app.Fragment

/**
 * Created by Viktoria_Chebotar on 6/1/2017.
 */
class HomeFragment : Fragment() {
//    val presenter by lazy { HomePresenterImpl(DictionaryApp.getInstance(context).repository) }
//
//    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
//        val view = inflater?.inflate(R.layout.home_activity, container, false)
//        return view
//    }
//
//    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        presenter.onStart(this)
//        searchField.setOnClickListener {
//
//            val searchFragment = SearchActivity();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                val moveTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
//                moveTransition.duration=500
//                searchFragment.sharedElementEnterTransition = moveTransition
//
//                val slide = Slide(Gravity.BOTTOM)
//                slide.addTarget(R.id.searchContent)
//                slide.duration = 500
//                searchFragment.enterTransition=slide
//                setAllowEnterTransitionOverlap(true)
//                setAllowReturnTransitionOverlap(true)
//
//                val fade = Fade()
//                fade.duration = 500
//                exitTransition = fade
//                reenterTransition = fade
//            }
//            activity.supportFragmentManager.
//                    beginTransaction().
//                    addSharedElement(appBarLayout, ViewCompat.getTransitionName(appBarLayout)).
//                    addSharedElement(searchField, ViewCompat.getTransitionName(searchField)).
//                    replace(R.id.fragmentContainer,searchFragment ).
//                    addToBackStack(SearchActivity::class.java.simpleName).
//                    commit()
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        presenter.onStop()
//    }
//
//    override fun showProgress(progress: Boolean) {
//        progressBar.visibility = if (progress) View.VISIBLE else View.GONE
//        homeLayout.visibility = if (progress) View.GONE else View.VISIBLE
//    }
//
//    override fun showWordOfTheDay(word: WordInfo) {
//        wordOfTheDayCard.bind(word)
//    }
//
//    override fun showError(message: String) {
//        Snackbar.make(view!!, message, Snackbar.LENGTH_LONG).show()
//        homeLayout.visibility = View.GONE
//    }
}