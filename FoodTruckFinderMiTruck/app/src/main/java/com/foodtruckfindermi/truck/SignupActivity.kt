package com.foodtruckfindermi.truck

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.coroutines.runBlocking

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val signupButton = findViewById<Button>(R.id.signupButtonConfirm)
        val nameEdit = findViewById<EditText>(R.id.truckNameEdit)
        val emailEdit = findViewById<EditText>(R.id.emailEdit)
        val passwordEdit = findViewById<EditText>(R.id.editPassword)

        val food_types = resources.getStringArray(R.array.FoodTypes)
        val food_spinner = findViewById<Spinner>(R.id.foodSpinner)

        var selected_food = ""

        if (food_spinner != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, food_types
            )
            food_spinner.adapter = adapter

            food_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selected_food = food_types[p2]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //TODO(RUN STUFF)
                }
            }
        }





        signupButton.setOnClickListener {
            runBlocking {
                val (_request, _response, result) = Fuel.post("http://foodtruckfindermi.com/truck-signup", listOf("name" to nameEdit.text, "email" to emailEdit.text, "password" to passwordEdit.text, "food" to selected_food))
                    .awaitStringResponseResult()

                result.fold({ data ->
                    if (data.equals("true")) {
                        startIntent()
                    } else if (data.equals("false")) {
                        Log.i("http", "false")
                    }}, { error -> Log.e("http", "${error}")})





                        }
                }
            }



    fun startIntent() {
        val intent = Intent(this, TruckActivity::class.java)
        startActivity(intent)
    }
}