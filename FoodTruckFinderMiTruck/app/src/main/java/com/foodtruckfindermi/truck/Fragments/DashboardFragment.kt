package com.foodtruckfindermi.truck.Fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.foodtruckfindermi.truck.DataClasses.Review
import com.foodtruckfindermi.truck.Adapters.ReviewAdapter
import com.foodtruckfindermi.truck.R
import com.foodtruckfindermi.truck.SettingsActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File

class DashboardFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var lat: Double = 0.0
    var lon: Double = 0.0
    private lateinit var reviewBodyArray : Array<String>
    private lateinit var reviewAuthorArray: Array<String>
    private lateinit var reviewDateArray: Array<String>

    private var reviewArrayList : ArrayList<Review> = ArrayList<Review>()

    private lateinit var bmp : Bitmap


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val openButton = openButton

        val reviewList = reviewList
        val reviewExpandButton = reviewExpandButton
        val settingsExpandButton = settingsExpandButton
        val settingsLayout = settingLayout

        val activity = requireActivity()

        val file = File(activity.filesDir, "records.txt").readLines()
        val email = file[0]

        var name = ""

        runBlocking {
            val (_,_,nameResult) = Fuel.get("http://foodtruckfindermi.com/get-name", listOf("email" to email)).awaitStringResponseResult()

            nameResult.fold(
                {data -> name = data},
                {error -> Log.e("HTTP", "$error")}
            )

            val (_,_,result) = Fuel.get("http://foodtruckfindermi.com/get-truck-info", listOf("name" to name)).awaitStringResponseResult()

            result.fold(
                {data ->
                    val jsonString = """
                        {
                            "Truck": $data
                        }
                    """.trimIndent()

                    val truckJsonObject = JSONObject(jsonString)
                    val truckObject = truckJsonObject.getJSONArray("Truck")

                    val isOpen = truckObject.getJSONObject(0).getString("isopen")

                    if (isOpen == "1") {
                        openButton.text = activity.getString(R.string.close_truck_hint)
                    } else {
                        openButton.text = activity.getString(R.string.open_truck_hint)
                    }

                    val bytes = Base64.decode(truckObject.getJSONObject(0).getString("profile"), Base64.DEFAULT)
                    val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    imageView2.setImageBitmap(bmp)


                },
                {error -> Log.e("HTTP", "$error")}
            )
        }

        var isReviewExpanded = false
        var isSettingsExpanded = false

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)


        openButton.setOnClickListener {
            openTruck(email, fusedLocationClient)
        }

        settingsButton.setOnClickListener {
            val intent = Intent(requireActivity(), SettingsActivity::class.java)
            activity.startActivity(intent)
        }

        saveChangesButton.setOnClickListener {
            runBlocking {
                val (_, _, result) = Fuel.post("http://foodtruckfindermi.com/update-profile",
                    listOf(
                        "truckemail" to email,
                        "city" to cityEditText.text,
                        "website" to websiteEditText.text,
                        "foodtype" to foodTypeEditText.text
                        )
                ).awaitStringResponseResult()

            }
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
                    val jsonString = """
                        {
                            "Reviews": $data
                        }
                    """.trimIndent()

                    val reviewJsonObject = JSONObject(jsonString)
                    val reviewObject = reviewJsonObject.getJSONArray("Reviews")

                    for (i in 0 until(reviewObject.length())) {

                        val review = Review(
                            reviewObject.getJSONObject(i).getString("author"),
                            reviewObject.getJSONObject(i).getString("body"),
                            reviewObject.getJSONObject(i).getString("date")
                        )
                        reviewArrayList.add(review)
                    }

                    reviewList.adapter = ReviewAdapter(activity, reviewArrayList)

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

        val REQUEST_CODE = 100

        val profilePickerButton = picProfile
        val submitButton = submitProfileButton
        val image = imageView2

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


            runBlocking {
                val (_, _, result) = Fuel.post("http://foodtruckfindermi.com/upload-pfp", listOf("image" to serverString, "email" to email)).awaitStringResponseResult()
            }

        }


    }






    private fun openTruck(email: String?, fusedLocationClient: FusedLocationProviderClient) {
        val activity = requireActivity()

        if (ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            }
            return
        }
        val cancellationToken = CancellationTokenSource().token

        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationToken)
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                }
            }

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










    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val activity = requireActivity()

        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.ACCESS_FINE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(activity, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(activity, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val REQUEST_CODE = 100
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            val image = imageView2
            data?.data // handle chosen image
            bmp = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, data?.data)
            image.setImageBitmap(bmp)
        }
    }

}