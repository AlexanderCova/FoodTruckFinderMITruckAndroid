package com.foodtruckfindermi.truck

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.github.kittinunf.fuel.Fuel

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEdit = findViewById<EditText>(R.id.emailLoginEdit)
        val passwordEdit = findViewById<EditText>(R.id.passwordEditLogin)
        val loginButton = findViewById<Button>(R.id.loginButtonConfirm)

        loginButton.setOnClickListener {
            Fuel.get("http://foodtruckfindermi.com/truck-login?email=Truck1@gmail.com&password=Truck1")
                .response { _request, _response, result ->

                    Log.i("http", "sent request")

                    val (bytes) = result
                    if (bytes != null) {
                        var loginResult = "call ${String(bytes)}"

                        if (loginResult.equals("call true")) {
                            Log.i("true", "true")
                            val intent = Intent(this, TruckActivity::class.java)
                            startActivity(intent)
                        } else{
                            Log.i("false", "false")
                        }
                    } else {
                        Log.i("http", "false")
                    }
                }
        }
    }
}