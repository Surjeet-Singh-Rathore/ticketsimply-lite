package com.bitla.ts.app.base

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.bitla.ts.databinding.SlideTabBinding
import com.bitla.ts.domain.pojo.booking.Tabs

class BasePagerAdapter(
    var context: Context,
    var tabList: MutableList<Tabs>,
    fm: FragmentManager,
    var fragmentTab1: Fragment,
    var fragmentTab2: Fragment,
    var titleTabLeft: String,
    var titleTabRight: String
) : FragmentStatePagerAdapter(fm) {
    companion object {
        val tag: String = BasePagerAdapter::class.java.simpleName
    }

    override fun getItem(position: Int): Fragment {


        return when (position) {
            0 -> {
                fragmentTab1
            }
            1 -> {
                fragmentTab2
            }
            else -> {
                return fragmentTab1
            }
        }
    }

    override fun getCount(): Int {
        return tabList.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> titleTabLeft
            1 -> titleTabRight
            else -> {
                return titleTabLeft
            }
        }
    }

    // custom tabs
    fun getTabView(position: Int): View {
        val binding = SlideTabBinding.inflate(LayoutInflater.from(context), null, false)

        //val view : View = LayoutInflater.from(context).inflate(R.layout.slide_tab,null)
        val tvTab: TextView = binding.tvTab
        tvTab.text = tabList[position].title

        return binding.root
    }
}