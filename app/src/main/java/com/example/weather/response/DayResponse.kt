package com.example.weather.response

import com.example.weather.models.DayEntity
import com.google.gson.annotations.SerializedName

class DayResponse {
    @SerializedName("lat")
    var lat: Double = 0.0

    @SerializedName("lon")
    var lon: Double = 0.0

    @SerializedName("timezone")
    var city: String = ""

    @SerializedName("timezone_offset")
    var timezoneOffset: String = ""

    @SerializedName("daily")
    var dayWeatherInfo: ArrayList<DayEntity> = ArrayList()
}