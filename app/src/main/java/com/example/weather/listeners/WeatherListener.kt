package com.example.weather.listeners

import com.example.weather.models.HourEntity

interface WeatherListener {
    fun onWeatherClicked(hourEntity: HourEntity)
}