package com.foodtruckfindermi.truck

import android.content.Context
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
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.coroutines.runBlocking
import java.io.File

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val signupButton = findViewById<Button>(R.id.signupButtonConfirm)
        val nameEdit = findViewById<TextInputEditText>(R.id.truckNameEdit)
        val emailEdit = findViewById<TextInputEditText>(R.id.emailEdit)
        val passwordEdit = findViewById<TextInputEditText>(R.id.passwordEdit)
        val foodEdit = findViewById<TextInputEditText>(R.id.foodTypeEdit)
        val cityEdit = findViewById<TextInputEditText>(R.id.cityEdit)
        val websiteEdit = findViewById<TextInputEditText>(R.id.websiteEdit)
        val backButton = findViewById<Button>(R.id.signupBackButton)


        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        signupButton.setOnClickListener {

            if (TextUtils.isEmpty(nameEdit.text)) {
                nameEdit.background = AppCompatResources.getDrawable(this, R.drawable.required_text_background)
                Snackbar.make(signupRoot, "Field is Required!", Snackbar.LENGTH_SHORT).show()
            } else {
                if (TextUtils.isEmpty(emailEdit.text)) {
                    emailEdit.background =
                        AppCompatResources.getDrawable(this, R.drawable.required_text_background)
                    Snackbar.make(signupRoot, "Field is Required!", Snackbar.LENGTH_SHORT).show()
                } else {
                    if (TextUtils.isEmpty(passwordEdit.text)) {
                        passwordEdit.background = AppCompatResources.getDrawable(
                            this,
                            R.drawable.required_text_background
                        )
                        Snackbar.make(signupRoot, "Field is Required!", Snackbar.LENGTH_SHORT)
                            .show()
                    } else {
                        if (TextUtils.isEmpty(foodEdit.text)) {
                            foodEdit.background = AppCompatResources.getDrawable(
                                this,
                                R.drawable.required_text_background
                            )
                            Snackbar.make(signupRoot, "Field is Required!", Snackbar.LENGTH_SHORT)
                                .show()
                        } else {

                            runBlocking {

                                val signupList = mutableListOf(
                                    "name" to nameEdit.text,
                                    "email" to emailEdit.text,
                                    "password" to passwordEdit.text,
                                    "food" to foodEdit.text
                                )

                                if (!cityEdit.text!!.isEmpty()) {
                                    signupList.add("city" to cityEdit.text)
                                }
                                if (!websiteEdit.text!!.isEmpty()) {
                                    signupList.add("website" to websiteEdit.text)
                                }



                                val (_, _, result) = Fuel.post(
                                    "http://foodtruckfindermi.com/truck-signup",
                                    signupList
                                ).awaitStringResponseResult()

                                result.fold({ data ->
                                    if (data == "true") {

                                        val file = File(filesDir, "records.txt")
                                        if (file.exists()) {
                                            val record =
                                                emailEdit.text.toString() + "\n" + passwordEdit.text.toString()

                                            openFileOutput(
                                                "records.txt",
                                                Context.MODE_PRIVATE
                                            ).use {
                                                it.write(record.toByteArray())
                                            }
                                        } else {
                                            file.createNewFile()

                                            val record =
                                                emailEdit.text.toString() + "\n" + passwordEdit.text.toString()

                                            openFileOutput(
                                                "records.txt",
                                                Context.MODE_PRIVATE
                                            ).use {
                                                it.write(record.toByteArray())
                                            }
                                        }

                                        startIntent()
                                    } else if (data == "false") {
                                        val snackbar = Snackbar.make(
                                            it, "Email Already Used",
                                            Snackbar.LENGTH_SHORT
                                        ).setAction("Action", null)

                                        snackbar.show()

                                    }
                                }, { error -> Log.e("http", "${error}") })

                            }
                        }
                    }
                }
            }

        }
    }

    fun startIntent() {
        val intent = Intent(this, TruckActivity::class.java)
        startActivity(intent)
    }
}