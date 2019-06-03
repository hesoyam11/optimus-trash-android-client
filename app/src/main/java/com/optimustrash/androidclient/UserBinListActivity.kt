package com.optimustrash.androidclient

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

var userBinListRequestUrl : String? = null

class UserBinListActivity : AppCompatActivity() {
    private lateinit var errorTextView: TextView
    private lateinit var addBinButton: Button
    private lateinit var currentPageTextView: TextView
    private lateinit var totalItemsTextView: TextView
    private lateinit var nextPageButton: Button
    private lateinit var previousPageButton: Button
    private lateinit var binListLinearLayout: LinearLayout
    private var currentPageNumber = 1
    private var nextPageUrl: String? = ""
    private var previousPageUrl: String? = ""

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_bin_list)

        errorTextView = findViewById(R.id.error_msg)
        addBinButton = findViewById(R.id.add_bin_button)
        currentPageTextView = findViewById(R.id.current_page)
        totalItemsTextView = findViewById(R.id.total_items)
        nextPageButton = findViewById(R.id.next_page)
        previousPageButton = findViewById(R.id.previous_page)
        binListLinearLayout = findViewById(R.id.bin_list)

        addBinButton.setOnClickListener {
            val i = Intent(this, BinFormActivity::class.java)
            startActivity(i)
        }

        fun fetchUserBinList() {
            val o = getUserBinList()
                .map{ Gson().fromJson(it, BinList::class.java)}
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

            o.subscribe({ it ->
                binListLinearLayout.removeAllViews()
                for (binItem in it.results) {
                    val binItemView = layoutInflater.inflate(R.layout.user_bin_item, binListLinearLayout, false)
                    val idTextView = binItemView.findViewById<TextView>(
                        R.id.bin_id
                    )
                    val latitudeTextView = binItemView.findViewById<TextView>(
                        R.id.latitude
                    )
                    val longitudeTextView = binItemView.findViewById<TextView>(
                        R.id.longitude
                    )
                    val binDetailButton = binItemView.findViewById<Button>(
                        R.id.bin_detail_button
                    )
                    idTextView.text = "${idTextView.text}: ${binItem.id}"
                    latitudeTextView.text = "${latitudeTextView.text}: ${binItem.latitude}"
                    longitudeTextView.text = "${longitudeTextView.text}: ${binItem.longitude}"
                    binDetailButton.setOnClickListener {
                        Log.e("buttonMsg", "I am ${binItem.id} button!")
                        val i = Intent(this, BinDetailActivity::class.java)
                        i.putExtra("binId", binItem.id.toString())
                        startActivity(i)
                    }
                    binListLinearLayout.addView(binItemView)
                }
                currentPageTextView.text = "Current Page: ${currentPageNumber}"
                totalItemsTextView.text = "Total Items: ${it.count}"
                nextPageUrl = it.next
                previousPageUrl = it.previous

            }, {
                errorTextView.text = it.message
            })
        }

        fetchUserBinList()

        nextPageButton.setOnClickListener {
            if (nextPageUrl != null) {
                userBinListRequestUrl = nextPageUrl
                errorTextView.text = ""
                currentPageNumber++
                fetchUserBinList()
            }
            else {
                errorTextView.text = "This is already the last page."
            }
        }
        previousPageButton.setOnClickListener {
            if (previousPageUrl != null) {
                userBinListRequestUrl = previousPageUrl
                errorTextView.text = ""
                currentPageNumber--
                fetchUserBinList()
            }
            else {
                errorTextView.text = "This is already the first page."
            }
        }
    }
}

class BinList(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: ArrayList<BinItem>
)

class BinItem(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val currentWeight: Double,
    val maxWeight: Double,
    val fullness: Double
)
