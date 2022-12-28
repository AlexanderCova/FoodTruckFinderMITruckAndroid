package com.foodtruckfindermi.truck


import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.foodtruckfindermi.truck.Adapters.UserPagerAdapter
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.File


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

        FirebaseApp.initializeApp(this)
        val db = Firebase.firestore
        val file = File(filesDir, "records.txt").readLines()
        val email = file[0]

        dashboardBtn.setOnClickListener {
            mViewPager.currentItem = 0
        }

        eventBtn.setOnClickListener {
            mViewPager.currentItem = 1
        }
        val truckDoc = db.collection("Users").document(email)
        truckDoc.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if(document != null) {
                    if (document.exists()) {
                        return@addOnCompleteListener
                    } else {
                        val userData = hashMapOf(
                            "groups" to listOf<String>(),
                            "name" to email
                        )

                        truckDoc.set(userData)
                    }
                }
            } else {
                Log.d("TAG", "Error: ", task.exception)
            }
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