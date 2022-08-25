package com.foodtruckfindermi.truck.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foodtruckfindermi.truck.EventInfoActivity
import com.foodtruckfindermi.truck.R
import kotlinx.android.synthetic.main.fragment_event_info.*


class EventInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventObject = (activity as EventInfoActivity).eventObject

        eventInfoCityLabel.text = eventObject.getJSONObject(0).getString("city")
        eventInfoDateLabel.text = eventObject.getJSONObject(0).getString("date")
        eventInfoDescLabel.text = eventObject.getJSONObject(0).getString("desc")
    }
}