package com.foodtruckfindermi.truck

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.android.synthetic.main.activity_create_event.*
import kotlinx.coroutines.runBlocking

class CreateEventActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        val submitEventButton = findViewById<Button>(R.id.submitEventButton)
        val backButton = findViewById<ImageButton>(R.id.createEventBackButton)
        val eventNameEdit = findViewById<EditText>(R.id.eventNameEditText)
        val eventDateEdit = findViewById<EditText>(R.id.eventDateEditText)
        val eventDescEdit = findViewById<EditText>(R.id.eventDescEditText)
        val eventCityEdit = findViewById<EditText>(R.id.eventCityEditText)

        submitEventButton.setOnClickListener {
            runBlocking {
                val (request, response, result) = Fuel.post("http://foodtruckfindermi.com/create-event",
                    listOf("name" to eventNameEdit.text, "date" to eventDateEdit.text, "desc" to eventDescEdit.text, "city" to eventCityEdit.text))
                    .awaitStringResponseResult()
            }
        }
        backButton.setOnClickListener{
            val intent = Intent(this, EventsActivity::class.java)
            startActivity(intent)
        }





    }

}