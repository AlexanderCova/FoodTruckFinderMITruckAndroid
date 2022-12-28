package com.foodtruckfindermi.truck.Adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.foodtruckfindermi.truck.R
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class MessagesAdapter(private val context : Activity, private val arrayList : ArrayList<String>, private val senderList: ArrayList<DocumentReference>, private val sender: DocumentReference) :
    ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, arrayList) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {



        return runBlocking {



            if (getDocInfo(senderList[position]).id == getDocInfo(sender).id) {

                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view: View = inflater.inflate((R.layout.aligned_right), null)

                view.findViewById<TextView>(R.id.chatMessageRight).text = arrayList[position]

                return@runBlocking view
            } else {

                val inflater: LayoutInflater = LayoutInflater.from(context)
                val view: View = inflater.inflate((R.layout.aligned_left), null)

                view.findViewById<TextView>(R.id.chatMessageLeft).text = arrayList[position]

                return@runBlocking view
            }


        }

    }

    private suspend fun getDocInfo(doc: DocumentReference): DocumentSnapshot {
        return doc.get().await()
    }



}