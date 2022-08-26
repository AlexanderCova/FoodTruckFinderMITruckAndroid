package com.foodtruckfindermi.truck.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foodtruckfindermi.truck.Adapters.TruckAdapter
import com.foodtruckfindermi.truck.DataClasses.Truck
import com.foodtruckfindermi.truck.EventInfoActivity
import com.foodtruckfindermi.truck.R
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.android.synthetic.main.fragment_event_trucks.*
import kotlinx.coroutines.runBlocking
import org.json.JSONObject


class EventTrucksFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_trucks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = (activity as EventInfoActivity).eventName


        runBlocking {
            val (_, _, result) = Fuel.get("http://foodtruckfindermi.com/get-event-info", listOf("name" to name))
                .awaitStringResponseResult()

            result.fold(
                { data ->
                    val jsonString = """
                        {
                            "Event": $data
                        }
                    """.trimIndent()




                    val eventJsonObject = JSONObject(jsonString)
                    val eventObject = eventJsonObject.getJSONArray("Event")


                    val truckArrayString = eventObject.getJSONObject(0).getString("trucks")
                    val truckArray = truckArrayString.split("`").drop(1).toMutableList()







                    val truckArrayList = getTruckArray(truckArray)



                    eventTruckList.adapter = TruckAdapter(requireActivity(), truckArrayList)


                },
                { error -> Log.e("http", "$error")}
            )
        }

    }


    private fun getTruckArray(nameList : MutableList<String>): ArrayList<Truck> {
        var truckArrayList = ArrayList<Truck>()

        runBlocking {
            val (_, _, result) = Fuel.get("http://foodtruckfindermi.com/truck-query")
                .awaitStringResponseResult()

            result.fold(
                { data ->
                    val json_string = """
                        {
                            "Trucks": $data
                            
                        }
                    """.trimIndent()


                    truckArrayList = ArrayList<Truck>()

                    val truckJsonObject = JSONObject(json_string)
                    val truckObject = truckJsonObject.getJSONArray("Trucks")

                    for (i in 0 until (truckObject.length())) {
                        if (truckObject.getJSONObject(i).getString("truckname") in nameList) {

                            val truck = Truck(
                                truckObject.getJSONObject(i).getString("truckname"),
                                truckObject.getJSONObject(i).getString("profile"),
                                truckObject.getJSONObject(i).getString("rating"),
                                truckObject.getJSONObject(i).getString("foodtype")
                            )

                            truckArrayList.add(truck)
                        }
                    }
                    return@fold truckArrayList
                },
                { error -> Log.e("http", "$error") }
            )
        }

        return truckArrayList

    }
}