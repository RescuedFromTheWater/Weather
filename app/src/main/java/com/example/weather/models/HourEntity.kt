package com.example.weather.models

import com.google.gson.annotations.SerializedName

class HourEntity {
    var dateTimeString: String = ""

    @SerializedName("dt")
    var dateTime: Long = 0

    @SerializedName("temp")
    var temperature: Double = 0.0

    @SerializedName("weather")
    var weather: ArrayList<WeatherEntity> = ArrayList()
}