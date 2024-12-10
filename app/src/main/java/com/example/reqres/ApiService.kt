package com.example.reqres

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object ApiService {
    const val base = "https://reqres.in/api"


    fun GetUsers(n : Int) : List<User> {
        val connection = URL("$base/users?page=${n}").openConnection() as HttpURLConnection

        return try {
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            JSONObject(response).let {

                it.getJSONArray("data").let { data ->
                    List(data.length()) {
                        data.getJSONObject(it).let {
                            val avatar = it.getString("avatar")
                            val bitmap = try {
                                val stream = URL(avatar).openStream()
                                BitmapFactory.decodeStream(stream)
                            } catch (e : Exception) {
                                null
                            }

                            User(
                                it.getInt("id"),
                                it.getString("email"),
                                it.getString("first_name"),
                                it.getString("last_name"),
                                bitmap
                            )
                        }
                    }
                }

            }
        } catch (e : Exception) {
            e.printStackTrace()
            emptyList<User>()
        }
    }



//    "id": 1,
//    "email": "george.bluth@reqres.in",
//    "first_name": "George",
//    "last_name": "Bluth",
//    "avatar": "https://reqres.in/img/faces/1-image.jpg"

}

data class User (
    val id : Int,
    val email : String,
    val first_name : String,
    val last_name : String,
    val avatar : Bitmap? = null
)