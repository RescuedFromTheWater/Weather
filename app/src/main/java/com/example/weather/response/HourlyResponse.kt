package com.example.weather.response

import com.example.weather.models.HourEntity
import com.google.gson.annotations.SerializedName

class HourlyResponse {
    @SerializedName("timezone")
    var timezone: String = "default"

    @SerializedName("hourly")
    var hourlyWeather: ArrayList<HourEntity> = ArrayList()
}