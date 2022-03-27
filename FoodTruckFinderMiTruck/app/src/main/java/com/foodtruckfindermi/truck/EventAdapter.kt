package com.foodtruckfindermi.truck

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.youfood.Review

class EventAdapter(private val context: Activity, private val arrayList: ArrayList<Event>) : ArrayAdapter<Event>(context,
    R.layout.list_item,arrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = inflater.inflate((R.layout.event_list_item), null)

        val nameLabel : TextView = view.findViewById(R.id.eventNameLabel)
        val descLabel : TextView = view.findViewById(R.id.eventDescLabel)
        val dateLabel : TextView = view.findViewById(R.id.eventDateLabel)

        nameLabel.text = arrayList[position].name
        descLabel.text = arrayList[position].desc
        dateLabel.text = arrayList[position].date



        return view
    }
}