package com.optimustrash.androidclient

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

var binId = -1

class BinDetailActivity : AppCompatActivity() {
    private lateinit var errorTextView: TextView
    private lateinit var binIdTextView: TextView
    private lateinit var longitudeTextView: TextView
    private lateinit var latitudeTextView: TextView
    private lateinit var currentWeightTextView: TextView
    private lateinit var maxWeightTextView: TextView
    private lateinit var fullnessTextView: TextView
    private lateinit var showTokenButton: Button
    private lateinit var tokenTextView: TextView

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bin_detail)

        errorTextView = findViewById(R.id.error_msg)
        binIdTextView = findViewById(R.id.bin_id)
        longitudeTextView = findViewById(R.id.longitude)
        latitudeTextView = findViewById(R.id.latitude)
        currentWeightTextView = findViewById(R.id.current_weight)
        maxWeightTextView = findViewById(R.id.max_weight)
        fullnessTextView = findViewById(R.id.fullness)
        showTokenButton = findViewById(R.id.show_token_button)
        tokenTextView = findViewById(R.id.token)

        binId = intent.getStringExtra("binId").toInt()
        val o = getBinDetail()
            .map{ Gson().fromJson(it, BinItem::class.java)}
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

        o.subscribe({ it ->
            binIdTextView.text = "${binIdTextView.text}: ${it.id}"
            longitudeTextView.text = "${longitudeTextView.text}: ${it.longitude}"
            latitudeTextView.text = "${latitudeTextView.text}: ${it.latitude}"
            currentWeightTextView.text = "${currentWeightTextView.text}: ${it.currentWeight}"
            maxWeightTextView.text = "${maxWeightTextView.text}: ${it.maxWeight}"
            fullnessTextView.text = "${fullnessTextView.text}: ${it.fullness}"
            showTokenButton.setOnClickListener { _ ->
                val ob = getBinToken()
                    .map{ Gson().fromJson(it, BinToken::class.java)}
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                ob.subscribe({
                    tokenTextView.text = it.token
                }, {
                    errorTextView.text = it.message
                })
            }
        }, {
            errorTextView.text = it.message
        })
    }
}

class BinToken(
    val token: String
)
