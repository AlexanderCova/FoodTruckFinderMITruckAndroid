package com.foodtruckfindermi.truck

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.foodtruckfindermi.truck.Adapters.EventInfoPagerAdapter
import com.foodtruckfindermi.truck.Adapters.UserPagerAdapter
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class EventInfoActivity : AppCompatActivity() {

    private lateinit var mViewPager: ViewPager
    private lateinit var infoButton : Button
    private lateinit var trucksButton : Button
    private lateinit var reviewsButton : Button
    private lateinit var goingButton : Button
    private lateinit var mPagerAdapter: EventInfoPagerAdapter

    private lateinit var mPagerViewAdapter: EventInfoPagerAdapter
    lateinit var eventName : String

    var interested : Boolean = false
    private lateinit var email : String
    private lateinit var truckname : String
    private lateinit var trucksArray : MutableList<String>
    lateinit var eventObject : JSONArray


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_info)

        mViewPager = findViewById(R.id.EventInfoViewPager)
        infoButton = findViewById(R.id.infoTabButton)
        trucksButton = findViewById(R.id.trucksTabButton)
        reviewsButton = findViewById(R.id.reviewsTabButton)
        goingButton = findViewById(R.id.goingButton)
        val backButton = findViewById<ImageButton>(R.id.eventInfoBackButton)


        eventName = intent.getStringExtra("name")!!

        val accountFile = File(filesDir, "records.txt").readLines()
        email = accountFile[0]

        backButton.setOnClickListener {
            onBackPressed()
        }

        loadScreen(eventName, email)


        goingButton.setOnClickListener {
            if (interested) {
                trucksArray.remove(truckname)
                goingButton.text = getString(R.string.not_going_hint)
                var updatedList = ""

                for (i in 0 until(trucksArray.count())) {
                    updatedList += "`" + trucksArray[i]
                }
                runBlocking {
                    val (_, _, _) = Fuel.post("http://foodtruckfindermi.com/update-truck-going", listOf("event" to eventName, "update-truck-list" to updatedList)).awaitStringResponseResult()
                    loadScreen(eventName, email)
                }
            } else {
                trucksArray.add(truckname)
                goingButton.text = getString(R.string.going_hint)

                var updatedList = ""

                for (i in 0 until(trucksArray.count())) {
                    updatedList += "`" + trucksArray[i]
                }

                runBlocking {
                    val (_, _, _) = Fuel.post("http://foodtruckfindermi.com/update-truck-going", listOf("event" to eventName, "update-truck-list" to updatedList)).awaitStringResponseResult()
                    loadScreen(eventName, email)
                }
            }

        }


        infoButton.setOnClickListener {
            mViewPager.currentItem = 0
        }

        trucksButton.setOnClickListener {
            mViewPager.currentItem = 1
        }
        reviewsButton.setOnClickListener {
            mViewPager.currentItem = 2
        }


        mPagerViewAdapter = EventInfoPagerAdapter(supportFragmentManager)
        mViewPager.adapter = mPagerViewAdapter
        mViewPager.offscreenPageLimit = 3

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
        infoButton.setBackgroundColor(ContextCompat.getColor(this, R.color.gold))
        trucksButton.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
        reviewsButton.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))

    }

    private fun changeTabs(position: Int) {
        if (position == 0) {
            infoButton.setBackgroundColor(ContextCompat.getColor(this, R.color.gold))
            trucksButton.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            reviewsButton.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
        }
        if (position == 1) {
            infoButton.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            trucksButton.setBackgroundColor(ContextCompat.getColor(this, R.color.gold))
            reviewsButton.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))

        }
        if (position == 2) {
            infoButton.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            trucksButton.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            reviewsButton.setBackgroundColor(ContextCompat.getColor(this, R.color.gold))

        }
    }

    fun loadScreen(name : String, email : String) {
        val goingLabel = findViewById<TextView>(R.id.goingAmountLabel)
        val nameLabel = findViewById<TextView>(R.id.EventNameLabel)
        val goingButton = findViewById<Button>(R.id.goingButton)

        runBlocking {
            val (_, _, result) = Fuel.get(
                "http://foodtruckfindermi.com/get-event-info",
                listOf("name" to name)
            )
                .awaitStringResponseResult()

            result.fold(
                { data ->
                    val jsonString = """
                        {
                            "Event": $data
                        }
                    """.trimIndent()

                    val eventJsonObject = JSONObject(jsonString)
                    eventObject = eventJsonObject.getJSONArray("Event")

                    val goingArrayString = eventObject.getJSONObject(0).getString("going")
                    val goingArray = goingArrayString.split("`").drop(1).toMutableList()

                    nameLabel.text = eventObject.getJSONObject(0).getString("name")
                    goingLabel.text = goingArray.count().toString()


                    val truckArrayString = eventObject.getJSONObject(0).getString("trucks")
                    trucksArray = truckArrayString.split("`").drop(1).toMutableList()

                    truckname = MainActivity().getName(email)

                    if (truckname in trucksArray) {
                        goingButton.text = getString(R.string.not_going_hint)
                        interested = true
                    } else {
                        goingButton.text = getString(R.string.going_hint)
                        interested = false
                    }


                },
                { error -> Log.e("http", "$error") }
            )
        }
    }
}