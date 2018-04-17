package com.mydictionary.presentation.views.learn

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import com.mydictionary.R
import com.mydictionary.data.pojo.SortingOption
import kotlinx.android.synthetic.main.learn_sort_options.*


/**
 * Created by Viktoria_Chebotar on 3/29/2018.
 */

class LearnSortOptionsDialog : BottomSheetDialogFragment(), View.OnClickListener {

    var listener: SortingDialogListener? = null

    companion object {
        fun getInstance(selectedSortingOption: SortingOption): LearnSortOptionsDialog {
            val dialog = LearnSortOptionsDialog()
            val extras = Bundle()
            extras.putSerializable("sortingOption", selectedSortingOption)
            dialog.arguments = extras
            return dialog
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.learn_sort_options, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sortByDate.isChecked = false
        sortByName.isChecked = false
        sortRandomly.isChecked = false
        when (arguments?.get("sortingOption")) {
            SortingOption.BY_DATE -> sortByDate.isChecked = true
            SortingOption.BY_NAME -> sortByName.isChecked = true
            SortingOption.RANDOMLY -> sortRandomly.isChecked = true
        }
        sortByDate.setOnClickListener(this)
        sortByName.setOnClickListener(this)
        sortRandomly.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sortByDate -> listener?.onSortItemSelected(SortingOption.BY_DATE)
            R.id.sortByName -> listener?.onSortItemSelected(SortingOption.BY_NAME)
            R.id.sortRandomly -> listener?.onSortItemSelected(SortingOption.RANDOMLY)
        }
        (v as CheckedTextView).isChecked = true
        dismiss()
    }
}

interface SortingDialogListener {
    fun onSortItemSelected(item: SortingOption)
}
