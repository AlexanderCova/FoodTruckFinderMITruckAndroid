package com.foodtruckfindermi.truck

import android.content.Intent
import android.icu.util.TimeUnit
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.runBlocking
import pl.droidsonroids.gif.GifImageView
import java.io.File
import kotlin.time.DurationUnit

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val gifImageView = findViewById<GifImageView>(R.id.gifImageView)


        val file = File(filesDir,"records.txt")
        if (file.exists()){
            gifImageView.setOnClickListener {

                val intent = Intent(this@SplashScreen, TruckActivity::class.java)
                startActivity(intent)

            }
        } else {
            gifImageView.setOnClickListener {

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

        }

    }
}