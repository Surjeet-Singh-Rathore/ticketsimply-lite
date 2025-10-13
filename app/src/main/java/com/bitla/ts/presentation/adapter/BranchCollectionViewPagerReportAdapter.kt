package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.bitla.ts.R
import com.bitla.ts.databinding.SlideTabBinding
import com.bitla.ts.domain.pojo.booking.Tabs
import com.bitla.ts.presentation.view.fragments.BranchCollectionBookingReportsFragment
import com.bitla.ts.presentation.view.fragments.BranchCollectionCancellationReportsFragment
import com.bitla.ts.presentation.view.fragments.FragmentReports

class BranchCollectionViewPagerReportAdapter(
    var context: Context,
    var tabList: MutableList<Tabs>,
    fm: FragmentManager,
    var reqBody: String
) : FragmentStatePagerAdapter(fm) {
    companion object {
        val tag: String = BranchCollectionViewPagerReportAdapter::class.java.simpleName
    }

    override fun getItem(position: Int): Fragment {
//

        return when (position) {
            0 -> {
                BranchCollectionBookingReportsFragment(reqBody)
            }
            1 -> {
                BranchCollectionCancellationReportsFragment(reqBody)
            }
            else -> {
                return FragmentReports()
            }
        }
    }

    override fun getCount(): Int {

        return tabList.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> context.getString(R.string.booking)
            1 -> context.getString(R.string.cancellation)
            else -> {
                return context.getString(R.string.booking)
            }
        }
    }

    fun getTabView(position: Int): View {
        val binding = SlideTabBinding.inflate(LayoutInflater.from(context), null, false)

        //val view : View = LayoutInflater.from(context).inflate(R.layout.slide_tab,null)
        val tvTab: TextView = binding.tvTab
        tvTab.text = tabList[position].title

        return binding.root
    }

}