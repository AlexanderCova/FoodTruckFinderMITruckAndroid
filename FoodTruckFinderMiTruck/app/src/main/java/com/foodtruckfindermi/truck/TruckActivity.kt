package com.foodtruckfindermi.truck

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.youfood.Review
import com.example.youfood.ReviewAdapter
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationServices.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_truck.*
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.io.File

class TruckActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var lat: Double = 0.0
    var lon: Double = 0.0
    private lateinit var reviewBodyArray : Array<String>
    private lateinit var reviewAuthorArray: Array<String>
    private lateinit var reviewDateArray: Array<String>

    private lateinit var reviewArrayList : ArrayList<Review>

    private lateinit var bmp : Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_truck)

        val openButton = findViewById<Button>(R.id.openButton)

        val reviewList = findViewById<ListView>(R.id.reviewList)
        val eventTabButton = findViewById<ImageButton>(R.id.eventTabButton)
        val reviewExpandButton = findViewById<Button>(R.id.reviewExpandButton)
        val settingsExpandButton = findViewById<Button>(R.id.settingsExpandButton)
        val settingsLayout = findViewById<LinearLayout>(R.id.settingLayout)


        val REQUEST_CODE = 100
        var isReviewExpanded = false
        var isSettingsExpanded = false

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val file = File(filesDir, "records.txt").readLines()
        val email = file[0]

        openButton.setOnClickListener {
            openTruck(email, fusedLocationClient)
        }

        eventTabButton.setOnClickListener {
            val intent = Intent(this, EventsActivity::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }

        settingsExpandButton.setOnClickListener {
            if (!isSettingsExpanded) {
                isSettingsExpanded = true
                settingsLayout.visibility = View.VISIBLE
                settingsExpandButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_keyboard_arrow_up_24, 0)
            } else {
                isSettingsExpanded = false
                settingsLayout.visibility = View.GONE
                settingsExpandButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_keyboard_arrow_down_24, 0)
            }
        }

        reviewExpandButton.setOnClickListener {
            if (!isReviewExpanded) {
                reviewList.visibility = View.VISIBLE
                isReviewExpanded = true
                reviewExpandButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_keyboard_arrow_up_24, 0)
            } else {
                reviewList.visibility = View.GONE
                isReviewExpanded = false
                reviewExpandButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_keyboard_arrow_down_24, 0)

            }

        }


        runBlocking {
            val (request, response, result) = Fuel.get("http://foodtruckfindermi.com/review-query?truck=${email}")
                .awaitStringResponseResult()

            result.fold(
                { data ->
                    var reviewArray = data.split("^")
                    Log.i("Arrays", reviewArray[0].toString())

                    reviewAuthorArray = reviewArray[0].split("`").drop(1).toTypedArray()

                    reviewBodyArray = reviewArray[1].split("`").drop(1).toTypedArray()

                    reviewDateArray = reviewArray[2].split("`").drop(1).toTypedArray()

                    reviewArrayList = ArrayList()

                    for(i in reviewAuthorArray.indices){

                        val review = Review(reviewAuthorArray[i], reviewBodyArray[i], reviewDateArray[i])
                        reviewArrayList.add(review)
                    }

                    reviewList.adapter = ReviewAdapter(this@TruckActivity, reviewArrayList)

                    var totalHeight = 0
                    for (i in 0 until reviewList.adapter.count) {
                        val listItem: View = reviewList.adapter.getView(i, null, reviewList)
                        listItem.measure(0, 0)
                        totalHeight += listItem.measuredHeight + listItem.measuredHeightAndState / 2
                    }
                    val params: ViewGroup.LayoutParams = reviewList.layoutParams
                    params.height = totalHeight + reviewList.dividerHeight * (reviewList.adapter.count - 1)
                    reviewList.layoutParams = params
                    reviewList.requestLayout()

                },
                { error -> Log.e("http", "${error}") })
        }

    }






    private fun openTruck(email: String?, fusedLocationClient: FusedLocationProviderClient) {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            }
            return
        }
        val cancellationToken = CancellationTokenSource().token

        fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, cancellationToken)
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                }
            }

        Log.i("Lat", lat.toString())
        Log.i("Lon", lon.toString())

        runBlocking {
            val (_request, _response, result) = Fuel.post("http://foodtruckfindermi.com/open-truck", listOf("email" to email, "lat" to lat.toString(), "lon" to lon.toString()))
                .awaitStringResponseResult()

            result.fold({ data ->

                if (data == "opened") {
                    val snackbar = Snackbar.make(
                        openButton, "Opened truck at: ${lat} ${lon}",
                        Snackbar.LENGTH_SHORT
                    ).setAction("Action", null)
                    snackbar.show()

                    openButton.text = "Close Truck"


                } else if (data == "closed") {
                    val snackbar = Snackbar.make(
                        openButton, "Truck Closed",
                        Snackbar.LENGTH_SHORT
                    ).setAction("Action", null)
                    snackbar.show()

                    openButton.text = "Open Truck"

                }


            }, {error -> Log.e("http", "${error}")})

        }



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
            val serverString = Base64.encodeToString(bytes, Base64.DEFAULT)
            Log.i("image", serverString)

            runBlocking {
                val (_, _, result) = Fuel.post("http://foodtruckfindermi.com/upload-pfp", listOf("image" to serverString, "email" to email)).awaitStringResponseResult()
            }

        }







    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
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