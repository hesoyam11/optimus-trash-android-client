package com.optimustrash.androidclient

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

var accessToken = ""
var userId = -1

class LoginActivity : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.login_button)
        loginButton.setOnClickListener { _ ->
            val username = findViewById<EditText>(R.id.username).text.toString()
            val password = findViewById<EditText>(R.id.password).text.toString()

            val o = postLogin(username, password)
                .map { Gson().fromJson(it, AccessToken::class.java) }
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

            val loginErrorView = findViewById<TextView>(R.id.error_msg)
            o.subscribe({
                accessToken = it.access
                val jwtPayload = String(Base64.decode(
                    accessToken.split('.')[1],
                    Base64.URL_SAFE
                ), Charsets.UTF_8)
                userId = Gson().fromJson(jwtPayload, UserId::class.java).user_id
                loginErrorView.text = "Success! UserId: $userId"
                val i = Intent(this, UserProfileActivity::class.java)
                startActivity(i)
            }, {
                loginErrorView.text = it.message
            })
        }
    }
}

class AccessToken(
    val access: String
)

class UserId(
    val user_id: Int
)