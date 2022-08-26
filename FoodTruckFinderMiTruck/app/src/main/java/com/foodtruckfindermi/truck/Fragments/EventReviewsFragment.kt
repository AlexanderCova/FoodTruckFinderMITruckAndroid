package com.foodtruckfindermi.truck.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foodtruckfindermi.truck.Adapters.ReviewAdapter
import com.foodtruckfindermi.truck.DataClasses.Review
import com.foodtruckfindermi.truck.EventInfoActivity
import com.foodtruckfindermi.truck.R
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.android.synthetic.main.fragment_event_reviews.*
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.io.File


class EventReviewsFragment : Fragment() {

    private var reviewArrayList : ArrayList<Review> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_reviews, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val reviewList = eventReviewList
        val submitReviewButton = submitEventReviewButton
        val bodyReviewTextEdit = eventBodyTextEdit


        val file = File(requireActivity().filesDir, "records.txt").readLines()
        val email = file[0]
        val eventName = (activity as EventInfoActivity).eventName


        runBlocking {
            val (_, _, result) = Fuel.get("http://foodtruckfindermi.com/event-review-query?eventName=${eventName}")
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

                    reviewList.adapter = ReviewAdapter(requireActivity(), reviewArrayList)

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
                { error -> Log.e("http", "$error") })
        }


        submitReviewButton.setOnClickListener {
            runBlocking {
                val (_, _, result) = Fuel.post(
                    "http://foodtruckfindermi.com/create-event-review",
                    listOf(
                        "author" to email,
                        "body" to bodyReviewTextEdit.text,
                        "eventName" to eventName
                    )
                ).awaitStringResponseResult()

                result.fold(
                    { data ->
                        if (data == "true") {
                            bodyReviewTextEdit.text.clear()
                        }
                    },
                    { error -> Log.e("http", "$error") }
                )

            }
        }
    }
}