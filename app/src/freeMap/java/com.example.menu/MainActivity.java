package com.example.menu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.menu.database.DatabaseHelper;
import com.example.menu.model.DataModel;
import com.example.menu.weather.OpenWeather;
import com.example.menu.weather.ResponseWeather;
import com.example.menu.weather.TimeDate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.time.Instant;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private OpenWeather openWeather;
    private TextView textTemp; // Температура (в градусах)
    private TextView description;
    private TextInputEditText editCity;
    private TextView inputCity;
    private TextView date;
    private TextView speed; // скорость ветра
    private TextView wind; // ветер
    private TextView ms; // м/с
    private TextView clouds; // облачность
    private TextView humidity; // влажность
    private TextView pressure; // давление
    private TextView sunriseName;
    private TextView sunsetName;
    private ImageView imageView;
    private String pngUrl; //url картинки openweathermap.org/img/wn/(11d).png
    private String url;
    final String metric = "metric";
    final String lang = "ru";
    private LocationListener locationListener;
    private LatLng location;
    private String city;
    private String NAME_CITY = "nameCity"; // для SharedPreferences
    private String TEMP_T = "temp"; // для SharedPreferences
    private String SPEED_S = "speed"; // для SharedPreferences
    private String DESCR_D = "description"; // для SharedPreferences
    private String DATE_D = "date"; // для SharedPreferences
    private String CLOUDS_C = "clouds"; // для SharedPreferences
    private String HUMIDITY_H = "humidity"; // для SharedPreferences
    private String PRESSURE_P = "pressure"; // для SharedPreferencespressure
    private String SUNSET_S = "sunset"; // для SharedPreferencespressure
    private String SUNRISE_S = "sunrise"; // для SharedPreferencespressure

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = initToolbar();
        initDrawer(toolbar);
        initRetorfit();
        initGui();
        initCoord();
        picasso(pngUrl);

    }

    private void initGui() {
        description = findViewById(R.id.description);
        inputCity = findViewById(R.id.inputCity);
        textTemp = findViewById(R.id.temp);
        date = findViewById(R.id.date);
        speed = findViewById(R.id.speed);
        clouds = findViewById(R.id.clouds);
        humidity = findViewById(R.id.humidity);
        pressure = findViewById(R.id.press);
        wind = findViewById(R.id.wind);
        imageView = findViewById(R.id.coord);
        ms = findViewById(R.id.ms);
        sunriseName = findViewById(R.id.sunrise);
        sunsetName = findViewById(R.id.sunset);
    }

    //прием координат lon и lat от MapActivity
    private void initCoord() {
        Intent intent = getIntent();
        String latit = intent.getStringExtra(MapActivity.LAT);
        String lontit = intent.getStringExtra(MapActivity.LON);
        if (latit == null || lontit == null) {
            loadSharedPrefs();
        } else {
            requestRetrofit(latit, lontit, metric, BuildConfig.WEATHER_API_KEY, lang);
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        Thread.sleep(3000);
                        saveSharedPrefs();
                        result();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void picasso(String png) {
        png = pngUrl;
        url = "https://openweathermap.org/img/wn/";
        Picasso.get()
                .load(url + png + ".png")
                .placeholder(R.drawable.w_03d)
                .into(imageView);
    }

    private void initRetorfit() {
        Retrofit retrofit;
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        openWeather = retrofit.create(OpenWeather.class);
    }


    // погода по координатам lat и lon
    private void requestRetrofit(String lat, String lon, String metric, String keyApi, String lang) {
        openWeather.loadWeather(lat, lon, metric, keyApi, lang)
                .enqueue(new Callback<ResponseWeather>() {

                    @SuppressLint("SetTextI18n")
                    public void onResponse(Call<ResponseWeather> call, Response<ResponseWeather> response) {
                        if (response.body() != null) {
                            long result = response.body().getMain().getTemp();
                            textTemp.setText(Long.toString(result) + " ℃");

                            String dis = response.body().getWeather().get(0).getDescription();
                            description.setText(dis);

                            pngUrl = response.body().getWeather().get(0).getIcon();
                            picasso(pngUrl); // передаем "weather: icon"

                            String nameCity = response.body().getName();
                            inputCity.setText(nameCity);

                            date.setText((TimeDate.dateNow()));

                            double speedWind = response.body().getWind().getSpeed();
                            speed.setText(Double.toString(speedWind));

                            int nameClouds = response.body().getClouds().getAll();
                            clouds.setText(Integer.toString(nameClouds));

                            int nameHumidity = response.body().getMain().getHumidity();
                            humidity.setText(Integer.toString(nameHumidity));

                            int namePress = response.body().getMain().getPressure();
                            pressure.setText(Integer.toString(namePress));

                            int sunr = response.body().getSys().getSunrise();
                            sunriseName.setText(TimeDate.formatTime(Instant.ofEpochSecond(sunr)));
                            int suns = response.body().getSys().getSunset();
                            sunsetName.setText(TimeDate.formatTime(Instant.ofEpochSecond(suns)));

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseWeather> call, Throwable t) {
                        textTemp.setText("Error");
                    }
                });
    }

    //кнопка вызова
    public void FindOutTheWeather(View view) {
        requestLocation();
    }

    // определение погоды и населённый пункт пользователя с помощью геолокации "Текущая позиция"
    @SuppressLint("MissingPermission")
    private void requestLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String provider = locationManager.getBestProvider(criteria, true);

        if (provider != null) {
            locationManager.requestLocationUpdates(provider, 10000, 10, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat = location.getLatitude();// Широта
                    double lng = location.getLongitude();// Долгота
                    String latitude = Double.toString(lat);
                    String longitude = Double.toString(lng);
                    requestRetrofit(latitude, longitude, metric, BuildConfig.WEATHER_API_KEY, lang);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            });
        }
    }


    private Toolbar initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    private void initDrawer(Toolbar toolbar) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        drawer.setScrimColor(android.graphics.Color.BLUE);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_location) {
            requestLocation();
        } else if (id == R.id.nav_geolocation) {
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(this, AddDataAcctivity.class);
            startActivity(intent);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void saveSharedPrefs() {
        String preferenceFileName = "name";
        SharedPreferences sharedPref = getSharedPreferences(preferenceFileName, MODE_PRIVATE);
        savePreferences(sharedPref);    // сохранить настройки
    }

    public void loadSharedPrefs() {
        String preferenceFileName = "name";
        SharedPreferences sharedPref = getSharedPreferences(preferenceFileName, MODE_PRIVATE);
        loadPreferences(sharedPref);
    }

    private void savePreferences(SharedPreferences sharedPref) {
        String keys = inputCity.getText().toString();
        String values = textTemp.getText().toString();
        String keysSpeed = speed.getText().toString();
        String keysDescr = description.getText().toString();
        String keysDate = date.getText().toString();
        String keysClouds = clouds.getText().toString();
        String keysHumidity = humidity.getText().toString();
        String keysPress = pressure.getText().toString();
        String keysSunrise = sunriseName.getText().toString();
        String keysSunset = sunsetName.getText().toString();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(NAME_CITY, keys);
        editor.putString(TEMP_T, values);
        editor.putString(SPEED_S, keysSpeed);
        editor.putString(DESCR_D, keysDescr);
        editor.putString(DATE_D, keysDate);
        editor.putString(CLOUDS_C, keysClouds);
        editor.putString(HUMIDITY_H, keysHumidity);
        editor.putString(PRESSURE_P, keysPress);
        editor.putString(SUNRISE_S, keysSunrise);
        editor.putString(SUNSET_S, keysSunset);

        editor.apply();//сохраняет в backgraund потоке
    }

    private void loadPreferences(SharedPreferences sharedPref) {
        String keys = inputCity.getText().toString();
        String value = textTemp.getText().toString();
        String keysSpeed = speed.getText().toString();
        String keysDescr = description.getText().toString();
        String keysDate = date.getText().toString();
        String keysClouds = clouds.getText().toString();
        String keysHumidity = humidity.getText().toString();
        String keysPress = pressure.getText().toString();
        String keysSunrise = sunriseName.getText().toString();
        String keysSunset = sunsetName.getText().toString();
        String valueFirst = sharedPref.getString(NAME_CITY, keys);
        String valueSecond = sharedPref.getString(TEMP_T, value);
        String valueThird = sharedPref.getString(SPEED_S, keysSpeed);
        String valueFourth = sharedPref.getString(DESCR_D, keysDescr);
        String valueFifth = sharedPref.getString(DATE_D, keysDate);
        String valueSixth = sharedPref.getString(CLOUDS_C, keysClouds);
        String valueSeventh = sharedPref.getString(HUMIDITY_H, keysHumidity);
        String valueEighth = sharedPref.getString(PRESSURE_P, keysPress);
        String valueNinth = sharedPref.getString(SUNRISE_S, keysSunrise);
        String valueTenth = sharedPref.getString(SUNSET_S, keysSunset);
        inputCity.setText(valueFirst);
        textTemp.setText(valueSecond);
        speed.setText(valueThird);
        description.setText(valueFourth);
        date.setText(valueFifth);
        clouds.setText(valueSixth);
        humidity.setText(valueSeventh);
        pressure.setText(valueEighth);
        sunriseName.setText(valueNinth);
        sunsetName.setText(valueTenth);

    }

    public void result() {
        DatabaseHelper databaseHelper = App.getInstance().getDatabaseInstance();

        DataModel model = new DataModel();

        model.setCity(inputCity.getText().toString());
        model.setTemp(textTemp.getText().toString());
        model.setDate(date.getText().toString());

        databaseHelper.getDataDao().insert(model);
    }

}
