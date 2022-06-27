package com.foodtruckfindermi.truck


import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.foodtruckfindermi.truck.Adapters.UserPagerAdapter


class TruckActivity : AppCompatActivity() {

    private lateinit var mViewPager: ViewPager
    private lateinit var dashboardBtn : ImageButton
    private lateinit var eventBtn : ImageButton
    private lateinit var mPagerAdapter: UserPagerAdapter

    private lateinit var mPagerViewAdapter: UserPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_truck)

        mViewPager = findViewById(R.id.mViewPager)
        dashboardBtn = findViewById(R.id.dashboardBtn)
        eventBtn = findViewById(R.id.eventBtn)

        dashboardBtn.setOnClickListener {
            mViewPager.currentItem = 0
        }

        eventBtn.setOnClickListener {
            mViewPager.currentItem = 1
        }


        mPagerViewAdapter = UserPagerAdapter(supportFragmentManager)
        mViewPager.adapter = mPagerViewAdapter
        mViewPager.offscreenPageLimit = 2


        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                changeTabs(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        mViewPager.currentItem = 0
        dashboardBtn.setBackgroundResource(R.drawable.selected_tab_overlay)

    }

    private fun changeTabs(position: Int) {
        if (position == 0) {
            dashboardBtn.setBackgroundResource(R.drawable.selected_tab_overlay)
            eventBtn.setBackgroundResource(0)
        }
        if (position == 1) {
            dashboardBtn.setBackgroundResource(0)
            eventBtn.setBackgroundResource(R.drawable.selected_tab_overlay)

        }
    }
}