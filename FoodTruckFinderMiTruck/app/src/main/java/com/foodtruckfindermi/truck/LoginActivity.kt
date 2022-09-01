package com.foodtruckfindermi.truck

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.runBlocking
import java.io.File

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEdit = findViewById<EditText>(R.id.emailLoginEdit)
        val passwordEdit = findViewById<EditText>(R.id.passwordEditLogin)
        val loginButton = findViewById<Button>(R.id.loginButtonConfirm)
        val backButton = findViewById<Button>(R.id.loginBackButton)

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            runBlocking {
                val (_request, _response, result) = Fuel.get("http://foodtruckfindermi.com/truck-login?email=${emailEdit.text}&password=${passwordEdit.text}")
                .awaitStringResponseResult()

                result.fold({data ->
                    if (data == "true") {
                        val file = File(filesDir,"records.txt")
                        if (file.exists()) {
                            val record = emailEdit.text.toString() + "\n" + passwordEdit.text.toString()

                            openFileOutput("records.txt", Context.MODE_PRIVATE).use {
                                it.write(record.toByteArray())
                            }
                        } else {
                            file.createNewFile()
                            val record = emailEdit.text.toString() + "\n" + passwordEdit.text.toString()

                            openFileOutput("records.txt", Context.MODE_PRIVATE).use {
                                it.write(record.toByteArray())
                            }
                        }

                        startIntent()
                    } else if (data == "false") {
                        val snackbar = Snackbar.make(
                            it, "Incorrect Credentials",
                            Snackbar.LENGTH_SHORT
                        ).setAction("Signup", View.OnClickListener(){
                            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
                            startActivity(intent)
                        } ).show()
                    }
                }, {error -> Log.e("http", "${error}")})


                }
            }
        }

    fun startIntent() {
        val intent = Intent(this, TruckActivity::class.java)
        startActivity(intent)
    }


    }
