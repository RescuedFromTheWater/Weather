package com.example.weather.network

import com.example.weather.response.DayResponse
import com.example.weather.response.HourlyResponse
import com.example.weather.response.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("weather?lang=ru?lat={lat}&lon={lon}&appid={API key}")
    fun getCityWeather(
        @Query("q") city: String,
        @Query("appid") appid: String
    ): Call<WeatherResponse>

    @GET("onecall?lat=55.749804&lon=37.621059&units=metric&exclude=current,minutely,daily,alerts&lang=ru")
    fun getWeatherAtLastHour(
        @Query("appid") appid: String
    ): Call<HourlyResponse>

    @GET("onecall?units=metric&exclude=current,minutely,hourly,alerts&lang=ru")
    fun getWeatherAtLastDay(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String
    ): Call<DayResponse>
}