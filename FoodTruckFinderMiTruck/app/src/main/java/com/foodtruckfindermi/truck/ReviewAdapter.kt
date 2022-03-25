package com.example.youfood

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ReviewAdapter(private val context: Activity, private val arrayList: ArrayList<Review>) : ArrayAdapter<Review>(context,
                    R.layout.list_item,arrayList) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = inflater.inflate((R.layout.list_item), null)

        val authorLabel : TextView = view.findViewById(R.id.authorLabel)
        val bodyLabel : TextView = view.findViewById(R.id.bodyLabel)
        val dateLabel : TextView = view.findViewById(R.id.dateLabel)

        authorLabel.text = arrayList[position].author
        bodyLabel.text = arrayList[position].body
        dateLabel.text = arrayList[position].date



        return view
    }
}