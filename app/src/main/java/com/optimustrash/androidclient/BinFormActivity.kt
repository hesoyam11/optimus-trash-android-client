package com.optimustrash.androidclient

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class BinFormActivity : AppCompatActivity() {
    private lateinit var errorMessageTextView: TextView
    private lateinit var longitudeEditText: EditText
    private lateinit var latitudeEditText: EditText
    private lateinit var maxWeightEditText: EditText
    private lateinit var getCoordinatesButton: Button
    private lateinit var addBinButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bin_form)

        errorMessageTextView = findViewById(R.id.error_msg)
        longitudeEditText = findViewById(R.id.longitude)
        latitudeEditText = findViewById(R.id.latitude)
        maxWeightEditText = findViewById(R.id.max_weight)
        getCoordinatesButton = findViewById(R.id.get_coordinates_button)
        addBinButton = findViewById(R.id.add_bin_button)

        addBinButton.setOnClickListener { it ->
            val o = postBin(
                longitudeEditText.text.toString(),
                latitudeEditText.text.toString(),
                maxWeightEditText.text.toString()
            )
                .map{ Gson().fromJson(it, BinItem::class.java)}
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

            o.subscribe({
                errorMessageTextView.text = "Successfully created with ID: ${it.id}."
            }, {
                errorMessageTextView.text = it.message
            })
        }
    }
}