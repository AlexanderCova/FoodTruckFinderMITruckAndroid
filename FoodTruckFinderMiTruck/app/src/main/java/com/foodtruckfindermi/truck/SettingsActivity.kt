package com.foodtruckfindermi.truck

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Base64.encodeToString
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.android.synthetic.*
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var bmp : Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val REQUEST_CODE = 100

        val profilePickerButton = findViewById<Button>(R.id.picProfile)
        val submitButton = findViewById<Button>(R.id.submitProfileButton)
        val image = findViewById<ImageView>(R.id.imageView2)

        val email = intent.getStringExtra("email")

        profilePickerButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE)
            submitButton.isClickable = true
        }

        submitButton.setOnClickListener {
            val stream = ByteArrayOutputStream()
            val bitmap = (image.drawable as BitmapDrawable).bitmap
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
            val bytes = stream.toByteArray()
            val serverString = encodeToString(bytes, Base64.DEFAULT)
            Log.i("image", serverString)

            runBlocking {
                val (_, _, result) = Fuel.post("http://foodtruckfindermi.com/upload-pfp", listOf("image" to serverString, "email" to email)).awaitStringResponseResult()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val REQUEST_CODE = 100
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            val image = findViewById<ImageView>(R.id.imageView2)
            data?.data // handle chosen image
            bmp = MediaStore.Images.Media.getBitmap(this.contentResolver, data?.data)
            image.setImageBitmap(bmp)
        }
    }
}