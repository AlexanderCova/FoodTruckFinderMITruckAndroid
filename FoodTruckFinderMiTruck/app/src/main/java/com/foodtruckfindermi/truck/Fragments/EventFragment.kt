package com.foodtruckfindermi.truck.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import com.foodtruckfindermi.truck.Adapters.EventAdapter
import com.foodtruckfindermi.truck.CreateEventActivity
import com.foodtruckfindermi.truck.DataClasses.Event
import com.foodtruckfindermi.truck.R
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.android.synthetic.main.fragment_event.*
import kotlinx.coroutines.runBlocking


class EventFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity().intent

        val email = activity.getStringExtra("email")
        val eventList = eventList
        val createEventButton = createEventButton


        createEventButton.setOnClickListener {
            val intent = Intent(requireActivity(), CreateEventActivity::class.java)
            startActivity(intent)
        }




        runBlocking {
            val (_, _, result) = Fuel.get("http://foodtruckfindermi.com/event-query")
                .awaitStringResponseResult()

            result.fold(
                { data ->
                    var eventArray = data.split("^")
                    val eventNameArray = eventArray[0].split("`").drop(1)
                    val eventDescArray = eventArray[1].split("`").drop(1)
                    val eventDateArray = eventArray[2].split("`").drop(1)
                    val searchView = searchView

                    var eventArrayList = ArrayList<Event>()

                    for (i in eventNameArray.indices) {
                        val event = Event(eventNameArray[i], eventDescArray[i], eventDateArray[i])
                        eventArrayList.add(event)
                    }
                    eventList.adapter = EventAdapter(requireActivity(), eventArrayList)

                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {

                            val newEventList = ArrayList<Event>()
                            searchView.clearFocus()
                            for (i in eventNameArray.indices) {
                                if (eventNameArray[i].contains(query.toString())) {
                                    newEventList.add(Event(eventNameArray[i], eventDescArray[i], eventDateArray[i]))
                                    eventList.adapter = EventAdapter(requireActivity(), newEventList)
                                }
                            }
                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            val newEventList = ArrayList<Event>()

                            for (i in eventNameArray.indices) {
                                if (eventNameArray[i].contains(newText.toString())) {
                                    newEventList.add(Event(eventNameArray[i], eventDescArray[i], eventDateArray[i]))
                                    eventList.adapter = EventAdapter(requireActivity(), newEventList)
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