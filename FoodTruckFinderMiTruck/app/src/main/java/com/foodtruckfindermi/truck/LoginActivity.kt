package com.foodtruckfindermi.truck

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            Fuel.get("https://foodtruckfindermi.com/truck_auth?email=${emailEdit.text}&password=${passwordEdit.text}&type=LOGIN")
                .response { _request, _response, result ->

                    val (bytes) = result
                    if (bytes != null) {
                        var loginResult = "call ${String(bytes)}"

                        if (loginResult.equals("call true")) {
                            val intent = Intent(this, TruckActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
        }
    }
}