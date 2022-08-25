package com.foodtruckfindermi.truck.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.foodtruckfindermi.truck.Fragments.EventInfoFragment
import com.foodtruckfindermi.truck.Fragments.EventTrucksFragment
import com.foodtruckfindermi.truck.Fragments.EventReviewsFragment

class EventInfoPagerAdapter (fm: FragmentManager?) :
    FragmentPagerAdapter(fm!!){

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> {
                EventInfoFragment()
            }
            1 -> {
                EventTrucksFragment()
            }
            2 -> {
                EventReviewsFragment()
            }
            else -> {
                EventInfoFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }

}
