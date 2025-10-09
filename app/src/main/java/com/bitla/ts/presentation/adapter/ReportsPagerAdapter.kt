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
import com.bitla.ts.presentation.view.fragments.FragmentFavouriteReports
import com.bitla.ts.presentation.view.fragments.FragmentReports

class ReportsPagerAdapter(
    var context: Context,
    var tabList: MutableList<Tabs>,
    fm: FragmentManager
) : FragmentStatePagerAdapter(fm) {
    companion object {
        val tag: String = ReportsPagerAdapter::class.java.simpleName
    }

    override fun getItem(position: Int): Fragment {
//

        return when (position) {
            0 -> {
                FragmentReports()
            }
            1 -> {
                FragmentFavouriteReports()
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
            0 -> context.getString(R.string.all_reports)
            1 -> context.getString(R.string.favourites)
            else -> {
                return context.getString(R.string.all_reports)
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