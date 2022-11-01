package com.example.weather.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.weather.adapters.WeatherAdapter
import com.example.weather.adapters.WeatherPagerAdapter
import com.example.weather.databinding.WeatherFragmentBinding
import com.example.weather.models.DayEntity
import com.example.weather.models.HourEntity
import com.example.weather.response.DayResponse
import com.example.weather.response.HourlyResponse
import com.example.weather.response.WeatherResponse
import com.example.weather.ui.activities.MainActivity
import com.example.weather.viewmodels.WeatherViewModel
import com.google.android.gms.location.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.abs

class MainFragment : Fragment() {

    private var binding: WeatherFragmentBinding? = null
    private lateinit var viewModel: WeatherViewModel
    private var dailyWeather: ArrayList<DayEntity> = ArrayList()
    private lateinit var weatherAdapter: WeatherAdapter
    private lateinit var weatherPagerAdapter: WeatherPagerAdapter

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var settingsClient: SettingsClient // Доступ к настройкам
    private lateinit var locationRequest: LocationRequest // Сохранение данных запроса
    private lateinit var locationSettingsRequest: LocationSettingsRequest // Определние настроек девайса пользователя
    private lateinit var locationCallback: LocationCallback // События определения местоположения
    private var location: Location? = null// Широта и долгота пользователя
    private lateinit var appSettingPrefs: SharedPreferences

    private val cities = arrayOf(
        "Minsk",
        "Dzerzhinsk",
        "Grodno",
        "Gomel`",
        "По местоположению"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        if (checkLocationPermission() && fusedLocationProviderClient != null) startLocationUpdates()
    }
    /**
     * Проверка разрешений для получения местоположения
     */
    private fun checkLocationPermission(): Boolean {
        val state =
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        return state == PackageManager.PERMISSION_GRANTED
    }
    /**
     * Вспомогательныйметод для получения местоположения
     */
    private fun makeLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = PRIORITY_HIGH_ACCURACY
        }
    }
    /**
     * Вспомогательныйметод для получения местоположения
     */
    private fun makeLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                location = p0.lastLocation
                if (location == null) {
                    Toast.makeText(requireContext(), "Включите геолокацию", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
    /**
     * Вспомогательныйметод для получения местоположения
     */
    private fun makeLocationSettings() {
        val settingsBuilder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
        settingsBuilder.addLocationRequest(locationRequest)
        locationSettingsRequest = settingsBuilder.build()
    }
    /**
     * Прекращение получения местоположения
     */
    private fun stopLocationUpdates() {
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
    }
    /**
     * Вспомогательныйметод для получения местоположения
     */
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), Array(1) { Manifest.permission.ACCESS_FINE_LOCATION },
                SETTINGS_CODE
            )
            Toast.makeText(requireContext(), "Включите геолокацию", Toast.LENGTH_LONG).show()
        } else if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), Array(1) { Manifest.permission.ACCESS_COARSE_LOCATION },
                SETTINGS_CODE
            )
            Toast.makeText(requireContext(), "Включите геолокацию", Toast.LENGTH_LONG).show()
        } else {
            setLocation()
        }
    }
    /**
     * Вспомогательныйметод для получения местоположения
     */
    @SuppressLint("MissingPermission")
    private fun setLocation() {
        fusedLocationProviderClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )?.addOnFailureListener {
            when ((it as ApiException).statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    try {
                        val ex: ResolvableApiException = it as ResolvableApiException
                        ex.startResolutionForResult(requireActivity(), SETTINGS_CODE)
                    } catch (e: IntentSender.SendIntentException) {
                        Toast.makeText(
                            context,
                            "Проблемы с подтверждением доступа к геолокации",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    Toast.makeText(
                        context,
                        "Подтвердите настройки геолокации",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = WeatherFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        appSettingPrefs = this.requireActivity()
            .getSharedPreferences("AppSettingPrefs", 0)
        val isNightModeOn: Boolean = appSettingPrefs.getBoolean("NightMode", false)
        binding!!.apply {
            try {
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        itemSelected: View, position: Int, selectedId: Long
                    ) {
                        makeChoise(position)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
                themeSwitch.isChecked = isNightModeOn
                if (isNightModeOn) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                themeSwitch.setOnClickListener {
                    val sharedPrefsEdit: SharedPreferences.Editor = appSettingPrefs.edit()
                    if (isNightModeOn) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        sharedPrefsEdit.putBoolean("NightMode", false)
                        sharedPrefsEdit.apply()
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        sharedPrefsEdit.putBoolean("NightMode", true)
                        sharedPrefsEdit.apply()
                    }
                }
                imageSettings.setOnClickListener {
                    // Здесь должен был быть переход на страницу настроек, но его не будет :)
                }
            } catch (e: Exception) {
            }
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadViewPager()
    }
    /**
     * Метод, работающий с аргументами выпадающего списка
     */
    private fun makeChoise(position: Int) {
        if (dailyWeather.isNotEmpty()) {
            dailyWeather.clear()
        }
        when (cities[position]) {
            "Minsk" -> getWeatherAtLastDay("53.9", "27.56", position)
            "Dzerzhinsk" -> getWeatherAtLastDay("53.68", "27.13", position)
            "Grodno" -> getWeatherAtLastDay("53.68", "23.82", position)
            "Gomel" -> getWeatherAtLastDay("52.43","30.97",position)
            "По местоположению" -> {
                weatherPagerAdapter.notifyDataSetChanged()
                getWeatherByGeo(position)
            }
        }
        Toast.makeText(
            context,
            "Ваш выбор: " + cities[position], Toast.LENGTH_SHORT
        ).show()
    }


    @SuppressLint("MissingPermission")
    private fun getWeatherByGeo(position: Int) {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        settingsClient = LocationServices.getSettingsClient(requireActivity())
        makeLocationRequest()
        makeLocationCallback()
        makeLocationSettings()
        startLocationUpdates()
        val locationResult = fusedLocationProviderClient?.lastLocation
        locationResult?.addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                location = task.result
                if (location != null) {
                    getWeatherAtLastDay(
                        location?.latitude.toString(),
                        location?.longitude.toString(), position
                    )
                } else {
                    Toast.makeText(requireContext(), "Включите геолокацию", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                weatherPagerAdapter.notifyDataSetChanged()
            }
        }
    }
    /**
     * Создание адаптера для погоды на неделю.
     */
    private fun loadViewPager() {
        weatherPagerAdapter = WeatherPagerAdapter(dailyWeather, requireContext())
        binding!!.viewPager.apply {
            offscreenPageLimit = 3
            adapter = weatherPagerAdapter
            clipToPadding = false
            clipChildren = false
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            val compositePageTransformer = CompositePageTransformer()
            compositePageTransformer.addTransformer(MarginPageTransformer(40))
            compositePageTransformer.addTransformer { page, position ->
                val r = 1 - abs(position)
                page.scaleY = 0.85f + r * 0.15f
            }
            setPageTransformer(compositePageTransformer)
        }
    }

    private fun getCityWeather(city: String) {
        viewModel.getCityWeather(city, "d8c067ca50fc4748821b35656cca8e56")
            .observe(
                (activity as MainFragment)
            ) { response: WeatherResponse? ->
                if (response != null) {
                } else {
                    Toast.makeText(context, "Что-то пошло не так", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeatherAtLastHour(city: String) {
        viewModel.getWeatherAtLastHour("d8c067ca50fc4748821b35656cca8e56")
            .observe(
                (activity as MainActivity)
            ) { response: HourlyResponse? ->
                if (response != null) {
                    val purposeHour = findLastHourWeather(response.hourlyWeather)
                    purposeHour.weather[0].description
                    weatherAdapter.notifyDataSetChanged()
                    weatherPagerAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(context, "Что-то пошло не так", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }
    /**
     * Получение погоды за сегодняшний день и за неделю.
     */
    private fun getWeatherAtLastDay(lat: String, lon: String, position: Int) {
        viewModel.getWeatherAtLastDay(lat, lon, "d8c067ca50fc4748821b35656cca8e56")
            .observe((activity as MainActivity)) { response: DayResponse? ->
                if (response != null) {
                    val dayEntity = response.dayWeatherInfo[0]
                    dailyWeather.addAll(
                        response.dayWeatherInfo.subList(
                            1,
                            response.dayWeatherInfo.size))
                    weatherPagerAdapter.notifyDataSetChanged()
                    val cities = resources.getStringArray(R.array.cityNames)
                    dayEntity.apply {
                        cityName = "<" + cities[position] + ">"
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            dateTime = Instant.ofEpochSecond(dayEntity.dateTime.toLong())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime().toString().substringBefore("T")
                        }
                    }
                    binding!!.weatherInfo = dayEntity
                } else {
                    Toast.makeText(
                        context,
                        "При получении данных произошла ошибка",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun findLastHourWeather(hourlyWeather: ArrayList<HourEntity>): HourEntity {
        val now = LocalDateTime.now()
        var temp = LocalDateTime.MIN
        var purposeHour = HourEntity()

        for (weather in hourlyWeather) {
            val dt = Instant.ofEpochSecond(weather.dateTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
            if (dt < now && dt > temp) {
                temp = dt
                purposeHour = weather
            } else {return purposeHour }
        }
        return purposeHour
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        private const val SETTINGS_CODE: Int = 123
    }
}