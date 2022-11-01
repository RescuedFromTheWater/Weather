package com.example.weather.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.PagerContainerBinding
import com.example.weather.models.DayEntity
import java.lang.NumberFormatException
import java.time.Instant
import java.time.ZoneId

class WeatherPagerAdapter(sliderWeatherInfo: ArrayList<DayEntity>, context_: Context) :
    RecyclerView.Adapter<WeatherPagerAdapter.WeatherPagerViewHolder>() {
    private var weatherInfo: ArrayList<DayEntity> = ArrayList()
    private var layoutInflater: LayoutInflater? = null
    var context: Context = context_

    init {
        this.weatherInfo = sliderWeatherInfo
    }

    inner class WeatherPagerViewHolder(itemContainerSliderImageBinding: PagerContainerBinding) :
        RecyclerView.ViewHolder(itemContainerSliderImageBinding.root) {
        private var binding: PagerContainerBinding? = null

        init {
            this.binding = itemContainerSliderImageBinding
        }

        fun bindSliderImage(weatherInfo: DayEntity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    weatherInfo.dateTime = Instant.ofEpochSecond(
                        weatherInfo.dateTime.toLong())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime().toString().substringBefore("T")
                } catch (e: NumberFormatException) {
                }
            }
            weatherInfo.dateTime
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherPagerViewHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        val sliderImageBinding: PagerContainerBinding = DataBindingUtil.inflate(
            layoutInflater!!, R.layout.pager_container, parent, false
        )
        return WeatherPagerViewHolder(sliderImageBinding)
    }

    override fun onBindViewHolder(holder: WeatherPagerViewHolder, position: Int) {
        weatherInfo?.get(position)
            ?.let { holder.bindSliderImage(it) }
    }

    override fun getItemCount(): Int {
        return weatherInfo.size
    }
}