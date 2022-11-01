package com.example.weather.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.DayContainerBinding
import com.example.weather.listeners.WeatherListener
import com.example.weather.models.HourEntity
import java.time.Instant
import java.time.ZoneId

class WeatherAdapter(events_: List<HourEntity>, weatherListener_: WeatherListener) :
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    private var hourlyWeather: List<HourEntity> = events_
    private var layoutInflater: LayoutInflater? = null
    var eventsListener: WeatherListener = weatherListener_

    inner class WeatherViewHolder(itemLayoutBinding: DayContainerBinding) :
        RecyclerView.ViewHolder(itemLayoutBinding.root) {
        private var containerBinding: DayContainerBinding? = null

        init {
            this.containerBinding = itemLayoutBinding
        }

        fun bindEvent(hourEntity: HourEntity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                hourEntity.dateTimeString = Instant.ofEpochSecond(hourEntity.dateTime)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime().toString().replace("T"," ")
            }
            containerBinding?.weatherInfo = hourEntity
            containerBinding?.executePendingBindings()
            if (containerBinding?.root != null)
                itemView.setOnClickListener {
                    eventsListener.onWeatherClicked(hourEntity)
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        val binding: DayContainerBinding =
            DataBindingUtil.inflate(layoutInflater!!, R.layout.day_container, parent, false)
        return WeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bindEvent(hourlyWeather[position])
    }

    override fun getItemCount(): Int {
        return hourlyWeather.size
    }
}