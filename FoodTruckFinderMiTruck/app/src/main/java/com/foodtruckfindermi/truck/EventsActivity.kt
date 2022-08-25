package com.foodtruckfindermi.truck

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.SearchView
import com.foodtruckfindermi.truck.Adapters.EventAdapter
import com.foodtruckfindermi.truck.DataClasses.Event
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.coroutines.runBlocking

class EventsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        val dashboardTabButton = findViewById<ImageButton>(R.id.dashboardTabButton)
        val email = intent.getStringExtra("email")
        val eventList = findViewById<ListView>(R.id.eventList)
        val createEventButton = findViewById<Button>(R.id.createEventButton)

        dashboardTabButton.setOnClickListener {
            val intent = Intent(this, TruckActivity::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }

        createEventButton.setOnClickListener {
            val intent = Intent(this, CreateEventActivity::class.java)
            startActivity(intent)
        }




        runBlocking {
            val (request, response, result) = Fuel.get("http://foodtruckfindermi.com/event-query")
                .awaitStringResponseResult()

            result.fold(
                { data ->
                    var eventArray = data.split("^")
                    val eventNameArray = eventArray[0].split("`").drop(1)
                    val eventDescArray = eventArray[1].split("`").drop(1)
                    val eventDateArray = eventArray[2].split("`").drop(1)
                    val searchView = findViewById<SearchView>(R.id.eventSearchView)

                    var eventArrayList = ArrayList<Event>()

                    for (i in eventNameArray.indices) {
                        val event = Event(eventNameArray[i], eventDescArray[i], eventDateArray[i])
                        eventArrayList.add(event)
                    }
                    eventList.adapter = EventAdapter(this@EventsActivity, eventArrayList)

                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {

                            val newEventList = ArrayList<Event>()
                            searchView.clearFocus()
                            for (i in eventNameArray.indices) {
                                if (eventNameArray[i].contains(query.toString())) {
                                    newEventList.add(Event(eventNameArray[i], eventDescArray[i], eventDateArray[i]))
                                    eventList.adapter = EventAdapter(this@EventsActivity, newEventList)
                                }
                            }
                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            val newEventList = ArrayList<Event>()

                            for (i in eventNameArray.indices) {
                                if (eventNameArray[i].contains(newText.toString())) {
                                    newEventList.add(Event(eventNameArray[i], eventDescArray[i], eventDateArray[i]))
                                    eventList.adapter = EventAdapter(this@EventsActivity, newEventList)
                                }
                            }
                            return false
                        }
                    })


                },
                { error -> Log.e("http", error.toString())}
            )
        }

    }
}