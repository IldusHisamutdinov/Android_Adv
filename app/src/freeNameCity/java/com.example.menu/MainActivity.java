package com.example.menu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.menu.database.DatabaseHelper;
import com.example.menu.model.DataModel;
import com.example.menu.weather.CitySelection;
import com.example.menu.weather.DialogCity;
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

import static com.example.menu.weather.CitySelection.TOWN;


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
        initNameCity();
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

    private void initNameCity() {
        Intent intent = getIntent();
        String town = intent.getStringExtra(TOWN);
        if (town == null) {
            loadSharedPrefs();
            town = inputCity.getText().toString();
            requestRetrofitnameCity(town, metric, BuildConfig.WEATHER_API_KEY, lang);
        } else {
            requestRetrofitnameCity(town, metric, BuildConfig.WEATHER_API_KEY, lang);
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


    // погода по названию города
    private void requestRetrofitnameCity(String cityCountry, String metric, String keyApi, String lang) {
        openWeather.loadWeatherr(cityCountry, metric, keyApi, lang)
                .enqueue(new Callback<ResponseWeather>() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @SuppressLint("SetTextI18n")
                    public void onResponse(Call<ResponseWeather> call, Response<ResponseWeather> response) {
                        if (response.body() != null) {
                            long result = response.body().getMain().getTemp();
                            textTemp.setText(Long.toString(result) + " ℃");

                            String dis = response.body().getWeather().get(0).getDescription();
                            description.setText(dis);

                            pngUrl = (response.body().getWeather().get(0).getIcon());
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

                            wind.setText("ветер");
                            ms.setText("м/с");

                            int sunr = response.body().getSys().getSunrise();
                            sunriseName.setText(TimeDate.formatTime(Instant.ofEpochSecond(sunr)));
                            int suns = response.body().getSys().getSunset();
                            sunsetName.setText(TimeDate.formatTime(Instant.ofEpochSecond(suns)));

                        }
                        if (response.body() == null) {
                            loadSharedPrefs();
                            DialogCity dialog = new DialogCity();
                            dialog.show(getSupportFragmentManager(), "custom");

                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseWeather> call, Throwable t) {
                        textTemp.setText("Error");
                    }
                });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_favorite) {
            Intent intent = new Intent(this, CitySelection.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {

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
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(NAME_CITY, keys);
        editor.apply();//сохраняет в backgraund потоке
    }

    private void loadPreferences(SharedPreferences sharedPref) {
        String keys = inputCity.getText().toString();
        String valueFirst = sharedPref.getString(NAME_CITY, keys);
        inputCity.setText(valueFirst);
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
