package com.example.weather.models

import com.google.gson.annotations.SerializedName

class City {
    @SerializedName("name")
    var cityName: String = ""

    @SerializedName("coord")
    var coordinates: ArrayList<Double> = ArrayList()
}