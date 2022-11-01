package com.example.weather.models

import com.google.gson.annotations.SerializedName

class Temperature {
    @SerializedName("day")
    var dayTemp: Double = 0.0
}