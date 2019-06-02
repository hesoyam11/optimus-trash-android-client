package com.optimustrash.androidclient

import io.reactivex.Observable
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

const val domain = "http://192.168.0.102:8000"

fun postLogin(username: String, password: String) = Observable.create<String> { it ->

    val requestBody = "{\"username\":\"$username\", \"password\":\"$password\"}"
    val postData = requestBody.toByteArray(Charsets.UTF_8)

    val urlConnection = URL("$domain/api/token/").openConnection() as HttpURLConnection
    urlConnection.doOutput = true
    urlConnection.instanceFollowRedirects = false
    urlConnection.requestMethod = "POST"
    urlConnection.setRequestProperty(
        "Content-Type", "application/json"
    )
    urlConnection.setRequestProperty(
        "charset", "utf-8"
    )
    urlConnection.setRequestProperty(
        "Content-Length", Integer.toString(postData.size)
    )
    urlConnection.useCaches = false

    val wr = DataOutputStream(urlConnection.outputStream)
    wr.use {
        it.write(postData)
    }

    try {
        urlConnection.connect()

        if (urlConnection.responseCode != HttpURLConnection.HTTP_OK)
            it.onError(RuntimeException(urlConnection.responseMessage))
        else {
            val str = urlConnection.inputStream.bufferedReader().readText()
            it.onNext(str)
        }
    }
    finally {
        urlConnection.disconnect()
    }
}

fun getUserDetail() = Observable.create<String> {
    val urlConnection = URL("$domain/api/users/$userId").openConnection() as HttpURLConnection
    try {
        urlConnection.connect()
        if (urlConnection.responseCode != HttpURLConnection.HTTP_OK)
            it.onError(RuntimeException(urlConnection.responseMessage))
        else {
            val str = urlConnection.inputStream.bufferedReader().readText()
            it.onNext(str)
        }
    }
    finally {
        urlConnection.disconnect()
    }
}