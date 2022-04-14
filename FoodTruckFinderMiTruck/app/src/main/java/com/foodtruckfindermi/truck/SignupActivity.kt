package com.foodtruckfindermi.truck

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_signup.*
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

            if(TextUtils.isEmpty(nameEdit.text)) {
                nameEdit.background = AppCompatResources.getDrawable(this, R.drawable.required_text_background)
                Snackbar.make(signupRoot, "Field is Required!", Snackbar.LENGTH_SHORT).show()
            } else {
                if(TextUtils.isEmpty(emailEdit.text)){
                    emailEdit.background = AppCompatResources.getDrawable(this, R.drawable.required_text_background)
                    Snackbar.make(signupRoot, "Field is Required!", Snackbar.LENGTH_SHORT).show()
                } else {
                    if(TextUtils.isEmpty(passwordEdit.text)) {
                        AppCompatResources.getDrawable(this, R.drawable.required_text_background)
                        Snackbar.make(signupRoot, "Field is Required!", Snackbar.LENGTH_SHORT).show()
                    } else {
                        runBlocking {

                            val (_request, _response, result) = Fuel.post("http://foodtruckfindermi.com/truck-signup", listOf("name" to nameEdit.text, "email" to emailEdit.text, "password" to passwordEdit.text, "food" to selected_food))
                                .awaitStringResponseResult()

                            result.fold({ data ->
                                if (data.equals("true")) {
                                    startIntent(emailEdit.text.toString())
                                } else if (data.equals("false")) {
                                    Log.i("http", "false")
                                }}, { error -> Log.e("http", "${error}")})

                        }
                    }
                }
            }






                }
            }



    fun startIntent(email : String) {
        val intent = Intent(this, TruckActivity::class.java)
        intent.putExtra("email", email)
        startActivity(intent)
    }
}