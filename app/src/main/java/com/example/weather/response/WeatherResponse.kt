package com.example.weather.response

import com.example.weather.models.WeatherEntity
import com.google.gson.annotations.SerializedName

class WeatherResponse {

    @SerializedName("weather")
    var weather: ArrayList<WeatherEntity>? = null
}