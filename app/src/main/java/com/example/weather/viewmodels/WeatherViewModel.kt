package com.example.weather.viewmodels

import android.app.Application
import androidx.annotation.NonNull
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.weather.repositories.WeatherRepository
import com.example.weather.response.DayResponse
import com.example.weather.response.HourlyResponse
import com.example.weather.response.WeatherResponse

class WeatherViewModel(
    @NonNull
    application: Application
) : AndroidViewModel(application) {
    private var loginRepository: WeatherRepository =
        WeatherRepository()

    fun getCityWeather(
        city: String,
        apiKey: String
    ): LiveData<WeatherResponse> {
        return loginRepository.getCityWeather(city, apiKey)
    }

    fun getWeatherAtLastHour(
        apiKey: String
    ): LiveData<HourlyResponse> {
        return loginRepository.getWeatherAtLastHour(apiKey)
    }

    fun getWeatherAtLastDay(
        lat: String,
        lon: String,
        apiKey: String
    ): LiveData<DayResponse> {
        return loginRepository.getWeatherAtLastDay(lat, lon, apiKey)
    }
}