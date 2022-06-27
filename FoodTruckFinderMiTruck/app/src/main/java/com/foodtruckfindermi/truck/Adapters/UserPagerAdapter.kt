package com.foodtruckfindermi.truck.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.foodtruckfindermi.truck.Fragments.DashboardFragment
import com.foodtruckfindermi.truck.Fragments.EventFragment

class UserPagerAdapter (fm: FragmentManager?) :
    FragmentPagerAdapter(fm!!){

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> {
                DashboardFragment()
            }
            1 -> {
                EventFragment()
            }

            else -> {
                DashboardFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

}