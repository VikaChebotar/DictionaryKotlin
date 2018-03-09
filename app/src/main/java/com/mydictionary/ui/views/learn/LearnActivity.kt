package com.mydictionary.ui.views.learn

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mydictionary.R
import com.mydictionary.data.pojo.WordDetails
import kotlinx.android.synthetic.main.learn_activity.*


/**
 * Created by Viktoria_Chebotar on 3/9/2018.
 */
class LearnActivity : AppCompatActivity() {
  //  val cardsAdapter = LearnCardAdapter(this)
    val space by lazy { resources.getDimension(R.dimen.cards_view_pager_margin).toInt() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.learn_activity);
        setSupportActionBar(toolbar)

//        favWordsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        favWordsList.adapter = cardsAdapter
//        PagerSnapHelper().attachToRecyclerView(favWordsList)
//        favWordsList.itemAnimator = LearnCardItemAnimator()
//        cardsAdapter.addTickets(listOf(WordDetails("1"), WordDetails("2"), WordDetails("3")))
//        favWordsList.addItemDecoration(object : RecyclerView.ItemDecoration() {
//            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
//                outRect.set(space, space, space, space);
//            }
//        })

        val adapter = LearnCardPagerAadapter(supportFragmentManager)
        adapter.list.add(WordDetails("1"))
        adapter.list.add(WordDetails("1"))
        adapter.list.add(WordDetails("1"))
        adapter.list.add(WordDetails("1"))
        adapter.list.add(WordDetails("1"))
        favWordsList.adapter = adapter
        favWordsList.setPadding(space, space, space, space)
        favWordsList.clipToPadding = false
        favWordsList.pageMargin = space/3
    }
}