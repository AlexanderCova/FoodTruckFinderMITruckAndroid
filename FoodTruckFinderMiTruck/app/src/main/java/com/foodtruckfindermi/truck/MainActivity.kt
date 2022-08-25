package com.foodtruckfindermi.truck

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.coroutines.runBlocking
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val file = File(filesDir,"records.txt")
        if (file.exists()){
            val intent = Intent(this, TruckActivity::class.java)
            startActivity(intent)
        }


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<Button>(R.id.loginButton)
        val signupButton = findViewById<Button>(R.id.signupButton)

        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        signupButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

    }

    public fun getName(email : String) : String {
        var name = ""

        runBlocking {
            val (_, _, result) = Fuel.get(
                "http://foodtruckfindermi.com/get-name",
                listOf("email" to email)
            ).awaitStringResponseResult()

            result.fold(
                { data ->
                    name = data
                },
                {error -> Log.e("HTTP", "$error")})
        }

        return name
    }
}