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
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.presentation.view.fragments.boarding.summaryfragments.Amenities
import com.bitla.ts.presentation.view.fragments.boarding.summaryfragments.Cancellation
import com.bitla.ts.presentation.view.fragments.boarding.summaryfragments.NewSummary
import com.bitla.ts.utils.sharedPref.PreferenceUtils

class ServiceSummaryTabsAdapter(
    var context: Context,
    var tabList: MutableList<Tabs>,
    fm: FragmentManager,
    val privilegeResponseModel: PrivilegeResponseModel?
) : FragmentStatePagerAdapter(fm) {
    //    companion object {
//        val tag: String = ::class.java.simpleName
//    }
    override fun getItem(position: Int): Fragment {
//        Timber.d("$tag getItem","${tabList[position].title}")

        if(privilegeResponseModel?.isChileApp == true){
            return when (position) {
                0 -> {
                    NewSummary()
                }
                1 -> {
                    Cancellation()
                }
                else -> {
                    return NewSummary()
                }
            }
        }else{
            return when (position) {
                0 -> {
//                    Summary()
                    NewSummary()
                }
                1 -> {
                    Amenities()
                }
                2 -> {
                    Cancellation()
                }
                else -> {
                    return NewSummary()
                }
            }
        }

    }

    override fun getCount(): Int {
        return tabList.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        if (privilegeResponseModel?.isChileApp == true) {
            return when (position) {
                0 -> context.getString(R.string.summary)
                1 -> context.getString(R.string.cancellation)
                else -> {
                    return context.getString(R.string.summary)
                }
            }
        } else {
            return when (position) {
                0 -> context.getString(R.string.summary)
                1 -> context.getString(R.string.amenities)
                2 -> context.getString(R.string.cancellation)
                else -> {
                    return context.getString(R.string.summary)
                }
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