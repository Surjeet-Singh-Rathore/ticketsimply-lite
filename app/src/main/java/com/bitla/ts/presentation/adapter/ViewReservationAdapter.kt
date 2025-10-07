package com.bitla.ts.presentation.adapter

import android.content.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bitla.ts.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.booking.*
import com.bitla.ts.presentation.view.dashboard.NewReservationFragments.*
import com.bitla.ts.presentation.view.dashboard.ViewReservationFragments.*
import gone
import visible

class ViewReservationAdapter(
    var context: Context,
    var tabList: MutableList<Tabs>,
    var country: String,
    var groupByPnrPickupChart: Boolean,
    private var tripSheetCollectionOptionsInTSAppReservationChart: Boolean?,
    fm: FragmentActivity
) :  FragmentStateAdapter(fm,) {

//    override fun getItem(position: Int): Fragment {
//
//        return when (tabList[position].title) {
//
//            context.getString(R.string.passenger_list) -> {
//
////                if(country.equals("indonesia", true)) {
////                    PickupPassengerList()
////                } else {
////                    PassengerListFragment()
////                }
//
//                PickupPassengerList()
//
//                /* if(!country.equals("india", true)) {
//                     PickupPassengerList()
//                 } else {
//                     PassengerListFragment()
//                 }*/
//
////                PassengerListFragment()
//            }
//            context.getString(R.string.bulk_cancel) -> {
//                BulkCancelFragment()
//            }
//            context.getString(R.string.shift_passengers) -> {
//                ShiftPassengersFragment()
//            }
//            context.getString(R.string.collection) -> {
//                CollectionFragment()
//            }
//
//            else -> {
//                return ReservationChartFragment()
//            }
//        }
//    }

//    override fun getCount(): Int {
//        return tabList.size
//    }

//    override fun getPageTitle(position: Int): CharSequence {
//        return when (tabList[position].title) {
//            context.getString(R.string.passenger_list) -> {
//                context.getString(R.string.passenger_list)
//            }
//            context.getString(R.string.bulk_cancel) -> {
//                context.getString(R.string.bulk_cancel)
//            }
//            context.getString(R.string.shift_passengers) -> {
//                context.getString(R.string.shift_passengers)
//            }
//            context.getString(R.string.collection) -> {
//                context.getString(R.string.collection)
//            }
//
//
////            0 -> "Passenger list"
////
////            1 -> "Bulk Cancel"
////            2 -> "Shift Passengers"
////            3 -> "Collection"
//
//            else -> {
//                return context.getString(R.string.passenger_list)
//            }
//        }
//    }

    // custom tabs
    fun getTabView(position: Int): View {

        val binding = SlideTabBinding.inflate(LayoutInflater.from(context), null, false)

        //val view : View = LayoutInflater.from(context).inflate(R.layout.slide_tab,null)
        val tvTab: TextView = binding.tvTab
        tvTab.text = tabList[position].title
        val ivTabIcon: ImageView = binding.ivTabIcon

        if (country.equals("india", ignoreCase = true)) {
            val drawableResId = when (position) {
                0 -> R.drawable.ic_pickup_list_dashboard
                1 -> R.drawable.ic_pickup_cancel_dashboard
                2 -> R.drawable.ic_pickup_shift_passenger_dashboard
                3 -> R.drawable.ic_pickup_collection_dashboard
                else -> 0
            }
            ivTabIcon.setIconAndShow(drawableResId)
        } else {
            ivTabIcon.gone()
        }

        return binding.root
    }

    private fun ImageView.setIconAndShow(drawableResId: Int) {
        this.setImageResource(drawableResId)
        this.visible()
    }

    override fun getItemCount(): Int {
        return  tabList.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (tabList[position].title) {

            context.getString(R.string.passenger_list) -> {

               /* if(country.equals("india", true)) {
                    PassengerListFragment()
                } else {
                    PickupPassengerList()
                }*/

                if(country.equals("india", true)) {
                    if(groupByPnrPickupChart) {
                        PickupPassengerList()
                    } else {
                        PassengerListFragment()
                    }
                } else {
                    PickupPassengerList()
                }

            }
            context.getString(R.string.bulk_cancel) -> {
                BulkCancelFragment()
            }
            context.getString(R.string.shift_passengers) -> {
                ShiftPassengersFragment()
            }
            context.getString(R.string.collection) -> {
                val isTripCollection= tripSheetCollectionOptionsInTSAppReservationChart == true &&
                        country.equals("india", true)
                CollectionFragment(isTripCollection)
            }

            else -> {
                return ReservationChartFragment()
            }
        }
    }

}