package com.example.weather.models

import com.google.gson.annotations.SerializedName

class DayEntity {
    var cityName: String = ""

    @SerializedName("dt")
    var dateTime: String = ""

    @SerializedName("temp")
    var temperature: Temperature = Temperature()

    @SerializedName("weather")
    var weather: ArrayList<WeatherEntity> = ArrayList()

    override fun equals(other: Any?): Boolean {
        return dateTime != (other as DayEntity).dateTime
    }

    override fun hashCode(): Int {
        var result = cityName.hashCode()
        result = 31 * result + dateTime.hashCode()
        result = 31 * result + temperature.hashCode()
        result = 31 * result + weather.hashCode()
        return result
    }
}