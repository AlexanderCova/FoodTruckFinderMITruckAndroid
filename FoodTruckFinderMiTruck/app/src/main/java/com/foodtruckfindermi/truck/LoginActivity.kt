package com.foodtruckfindermi.truck

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.coroutines.runBlocking

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEdit = findViewById<EditText>(R.id.emailLoginEdit)
        val passwordEdit = findViewById<EditText>(R.id.passwordEditLogin)
        val loginButton = findViewById<Button>(R.id.loginButtonConfirm)

        loginButton.setOnClickListener {
            runBlocking {
                val (_request, _response, result) = Fuel.get("http://foodtruckfindermi.com/truck-login?email=${emailEdit.text}&password=${passwordEdit.text}")
                .awaitStringResponseResult()

                result.fold({data ->
                    if (data.equals("true")) {
                        startIntent()

                    } else {
                        Log.i("false", "false")
                    }
                }, {error -> Log.e("http", "${error}")})


                }
            }
        }

    fun startIntent() {
        val emailEdit = findViewById<EditText>(R.id.emailLoginEdit)

        val intent = Intent(this, TruckActivity::class.java)
        intent.putExtra("email", emailEdit.text.toString())
        startActivity(intent)
    }


    }
