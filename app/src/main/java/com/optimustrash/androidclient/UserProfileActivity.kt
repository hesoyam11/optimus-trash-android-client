package com.optimustrash.androidclient

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

var userDetail = UserDetail("", "", "", "", false)

class UserProfileActivity : AppCompatActivity() {
    private lateinit var errorTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var firstNameTextView: TextView
    private lateinit var lastNameTextView: TextView
    private lateinit var isConfirmedTextView: TextView
    private lateinit var binListButton: Button

    @SuppressLint("CheckResult", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile)

        errorTextView = findViewById(R.id.error_msg)
        usernameTextView = findViewById(R.id.username)
        emailTextView = findViewById(R.id.email)
        firstNameTextView = findViewById(R.id.first_name)
        lastNameTextView = findViewById(R.id.last_name)
        isConfirmedTextView = findViewById(R.id.is_confirmed)

        val o = getUserDetail()
            .map{ Gson().fromJson(it, UserDetail::class.java)}
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

        o.subscribe({
            usernameTextView.text = "${usernameTextView.text}: ${it.username}"
            emailTextView.text = "${emailTextView.text}: ${it.email}"
            firstNameTextView.text = "${firstNameTextView.text}: ${it.firstName}"
            lastNameTextView.text = "${lastNameTextView.text}: ${it.lastName}"
            isConfirmedTextView.text = "${isConfirmedTextView.text}: ${it.isConfirmed}"
            userDetail = it
        }, {
            errorTextView.text = it.message
        })

        binListButton = findViewById(R.id.bin_list_button)
        binListButton.setOnClickListener {
            val i = Intent(this, UserBinListActivity::class.java)
            startActivity(i)
        }
    }
}

class UserDetail(
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val isConfirmed: Boolean
)