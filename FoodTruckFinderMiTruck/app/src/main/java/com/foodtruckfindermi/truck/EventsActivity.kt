package com.foodtruckfindermi.truck

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class EventsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        val dashboardTabButton = findViewById<Button>(R.id.eventDashboardTabButton)
        val email = intent.getStringExtra("email")

        dashboardTabButton.setOnClickListener {
            val intent = Intent(this, TruckActivity::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }
    }
}