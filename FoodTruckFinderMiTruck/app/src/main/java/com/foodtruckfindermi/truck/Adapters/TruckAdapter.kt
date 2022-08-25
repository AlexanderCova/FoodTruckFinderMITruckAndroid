package com.foodtruckfindermi.truck.Adapters

import android.app.Activity
import android.content.ContentResolver
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.*
import com.foodtruckfindermi.truck.R.*

import com.foodtruckfindermi.truck.DataClasses.Truck


class TruckAdapter(private val context : Activity, private val arrayList : ArrayList<Truck>) :
    ArrayAdapter<Truck>(context, layout.truck_list_item, arrayList) {



        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            val inflater : LayoutInflater = LayoutInflater.from(context)
            val view : View = inflater.inflate((layout.truck_list_item), null)

            val nameLabel = view.findViewById<TextView>(id.listTruckName)
            val profilePic = view.findViewById<ImageView>(id.listProfilePic)
            val ratingLabel = view.findViewById<TextView>(id.listRating)
            val foodTypeLabel = view.findViewById<TextView>(id.listFoodType)

            nameLabel.text = arrayList[position].name

            val bytes = Base64.decode(arrayList[position].profilePic, Base64.DEFAULT)
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            profilePic.setImageBitmap(bmp)

            ratingLabel.text = arrayList[position].rating + " Stars"
            foodTypeLabel.text = arrayList[position].foodType

            return view

        }
    }