package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bitla.ts.R
import com.bitla.ts.databinding.CustomTabBinding
import com.bitla.ts.domain.pojo.booking.Tabs
import com.bitla.ts.domain.pojo.service_details_response.StageDetail
import com.bitla.ts.presentation.view.fragments.boarding.BoardingPointFragment
import com.bitla.ts.presentation.view.fragments.boarding.DroppingFragment
import timber.log.Timber
import java.io.Serializable

class BoardingDroppingAdapter(
    var context: Context,
    var tabList: MutableList<Tabs>,
    fm: FragmentManager,
    var boardingList: MutableList<StageDetail>,
    var droppingList: MutableList<StageDetail>,
    var viewpagerPickup: ViewPager,
) : FragmentStatePagerAdapter(fm) {
    companion object;

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {

                Timber.d("boardingList ${boardingList.size}")

                // BoardingPointFragment(boardingList,viewpagerPickup)
                val bundle = Bundle()
                bundle.putSerializable("boardingPointsList", boardingList as Serializable)
                bundle.putSerializable("droppingPointsList", droppingList as Serializable)
                BoardingPointFragment.newInstance(bundle, viewpagerPickup)

            }
            1 -> {
                //DroppingFragment(droppingList)
                val bundle = Bundle()
                bundle.putSerializable("droppingPointsList", droppingList as Serializable)
                DroppingFragment.newInstance(bundle)
                return DroppingFragment()
            }
            else -> {
                return BoardingPointFragment()
            }
        }
    }

    override fun getCount(): Int {
        return tabList.size
    }

    override fun getPageTitle(position: Int): CharSequence {

        return when (position) {
            0 -> context.getString(R.string.boarding_point)

            1 -> context.getString(R.string.dropping_point)

            else -> {
                return context.getString(R.string.boarding_point)
            }
        }
    }

    // custom tabs
    @SuppressLint("LongLogTag")
    fun getTabView(position: Int): View {
        val binding: CustomTabBinding =
            CustomTabBinding.inflate(LayoutInflater.from(context))
        binding.tvTab.text = tabList[position].title
        binding.selectedBPDP.text = tabList[position].selectedPoint
        return binding.root
    }

}