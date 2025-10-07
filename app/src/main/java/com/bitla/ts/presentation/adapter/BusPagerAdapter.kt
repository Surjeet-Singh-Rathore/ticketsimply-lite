package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.bitla.ts.R
import com.bitla.ts.databinding.SlideTabBinding
import com.bitla.ts.domain.pojo.booking.Tabs
import com.bitla.ts.presentation.view.dashboard.FragmentBookingAgent
import com.bitla.ts.presentation.view.dashboard.MyBookingsFragment
import com.bitla.ts.presentation.view.fragments.FragmentBooking
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import timber.log.Timber
import toast
import java.util.Locale

class BusPagerAdapter(
    var context: Context,
    var tabList: MutableList<Tabs>,
    var fm: FragmentManager,
    var allowBookingForAllotedServices: Boolean,
    var availableRoutesCount: Int?
) : FragmentStatePagerAdapter(fm) {
    companion object {
        val tag: String = BusPagerAdapter::class.java.simpleName
    }

    private val fragmentTags = SparseArray<String>()

    @SuppressLint("LogNotTimber")
    override fun getItem(position: Int): Fragment {


        return when (position) {
            0 -> {
                if (allowBookingForAllotedServices)
                    FragmentBookingAgent()
                else
                    FragmentBooking()
            }

            1 -> {
                MyBookingsFragment()
            }

            else -> {
                return FragmentBooking()
            }
        }
    }

    override fun getCount(): Int {
        return tabList.size
    }


    override fun getPageTitle(position: Int): CharSequence {
        Timber.d("getPageTitle ${PreferenceUtils.getlang()}")
        return when (position) {
            0 -> if (allowBookingForAllotedServices)
                if (availableRoutesCount != null && availableRoutesCount != 0) "${
                    context.getString(
                        R.string.services
                    )
                }($availableRoutesCount)" else context.getString(R.string.services)
            else {
                    context.getString(R.string.Book_Tickets)
            }

            1 ->{
                    context.getString(R.string.my_bookings)
            }

            else -> {
                return context.getString(R.string.bookings)
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

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        val tag = fragment.tag
        fragmentTags.put(position, tag)
        return fragment
    }

    fun getFragment(position: Int): Fragment? {
        val tag = fragmentTags.get(position)
        return tag?.let { fm.findFragmentByTag(it) }
    }
}