package com.foodtruckfindermi.truck

import android.content.Intent
import android.icu.util.TimeUnit
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import kotlinx.coroutines.runBlocking
import pl.droidsonroids.gif.GifImageView
import java.io.File
import kotlin.time.DurationUnit

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)



        val file = File(filesDir,"records.txt")
        if (file.exists()){
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )

            // HERE WE ARE TAKING THE REFERENCE OF OUR IMAGE
            // SO THAT WE CAN PERFORM ANIMATION USING THAT IMAGE
            val backgroundImage: ImageView = findViewById(R.id.splashIcon)
            val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_anim)
            backgroundImage.startAnimation(slideAnimation)

            // we used the postDelayed(Runnable, time) method
            // to send a message with a delayed time.
            Handler().postDelayed({
                val intent = Intent(this, TruckActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )

            // HERE WE ARE TAKING THE REFERENCE OF OUR IMAGE
            // SO THAT WE CAN PERFORM ANIMATION USING THAT IMAGE
            val backgroundImage: ImageView = findViewById(R.id.splashIcon)
            val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_anim)
            backgroundImage.startAnimation(slideAnimation)

            // we used the postDelayed(Runnable, time) method
            // to send a message with a delayed time.
            Handler().postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000)


        }

    }
}