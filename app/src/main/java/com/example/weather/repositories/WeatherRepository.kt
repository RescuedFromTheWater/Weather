package com.example.weather.repositories

import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weather.network.ApiClient
import com.example.weather.network.ApiService
import com.example.weather.response.DayResponse
import com.example.weather.response.HourlyResponse
import com.example.weather.response.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherRepository {
    private var apiService:
            ApiService = ApiClient
        .getRetrofit()
        .create(ApiService::class.java)

    fun getCityWeather(
        city: String,
        apiKey: String
    ): LiveData<WeatherResponse> {

        val data: MutableLiveData<WeatherResponse> = MutableLiveData()
        apiService.getCityWeather(
            city,
            apiKey
        ).enqueue(
            object : Callback<WeatherResponse> {
                override fun onFailure(
                    @NonNull call: Call<WeatherResponse>, t: Throwable
                ) { data.value = null
                }

                override fun onResponse(
                    @NonNull call: Call<WeatherResponse>,
                    @NonNull response: Response<WeatherResponse>
                ) { data.value = response.body() }
            })
        return data
    }

    fun getWeatherAtLastHour(
        apiKey: String
    ): LiveData<HourlyResponse> {
        val data: MutableLiveData<HourlyResponse> = MutableLiveData()
        apiService.getWeatherAtLastHour(
            apiKey
        ).enqueue(
            object : Callback<HourlyResponse> {
                override fun onFailure(
                    @NonNull call: Call<HourlyResponse>, t: Throwable
                ) { data.value = null }

                override fun onResponse(
                    @NonNull call: Call<HourlyResponse>,
                    @NonNull response: Response<HourlyResponse>
                ) { data.value = response.body()}
            })
        return data
    }

    fun getWeatherAtLastDay(
        lat: String,
        lon: String,
        apiKey: String
    ): LiveData<DayResponse> {
        val data: MutableLiveData<DayResponse> = MutableLiveData()
        apiService.getWeatherAtLastDay(
            lat,
            lon,
            apiKey
        ).enqueue(object : Callback<DayResponse> {
            override fun onFailure(
                @NonNull call: Call<DayResponse>, t: Throwable) {
                data.value = null
            }

            override fun onResponse(
                @NonNull call: Call<DayResponse>,
                @NonNull response: Response<DayResponse>
            ) { data.value = response.body()}
        })
        return data
    }
}