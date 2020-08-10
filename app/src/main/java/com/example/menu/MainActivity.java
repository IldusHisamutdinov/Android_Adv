package com.example.menu;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    static final String BROADCAST_ACTION_WEATHERFINISHED = "com.example.menu.weatherfinished";
    private SensorManager sensorManager;
    private Sensor sensorTemp;
    private Sensor sensorHumidity;
    private TextView temp;
    private TextView hum;
    private TextView tempservice;
    private OpenWeather openWeather;
    private TextView textTemp; // Температура (в градусах)
    private TextView description;
    private TextView editCity;
    private String pngUrl;
    private String NAME_CITY = "nameCity"; // для SharedPreferences
    private String TEMP_T ="temp"; // для SharedPreferences
    private ImageView imageView;
    
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = initToolbar();
        initDrawer(toolbar);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorTemp = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensorHumidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);


        initRetorfit();
        initGui();
        initEvents();
        textTown();
    }

    private void initGui() {
        temp = findViewById(R.id.temp);
        hum = findViewById(R.id.h);
        textTemp = findViewById(R.id.tempretrofit);
        editCity = findViewById(R.id.editCity);
        description = findViewById(R.id.description);
        imageView = findViewById(R.id.coord);
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
        String keys = editCity.getText().toString();
        String values = textTemp.getText().toString();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(NAME_CITY, keys);
        editor.putString(TEMP_T, values);
        editor.apply();//сохраняет в backgraund потоке
    }

    private void loadPreferences(SharedPreferences sharedPref) {
        String keys = editCity.getText().toString();
        String value = textTemp.getText().toString();
        String valueFirst = sharedPref.getString(NAME_CITY, keys );
        String valueSecond = sharedPref.getString(TEMP_T, value );
        editCity.setText(valueFirst);
        textTemp.setText(valueSecond);
    }

    public void textTown() {
        String town = editCity.getText().toString();

        if (town != null) {
            loadSharedPrefs();
            picasso();
        }

    }

    private void picasso() { //при первой загрузки активити
        ImageView imageView = findViewById(R.id.coord);
        Picasso.get()
                .load(R.mipmap.ic_1_6)
                .transform(new IconTransformation())
                .into(imageView);
    }

    private void picasso(String icon) {
        icon = pngUrl;
        String url;
        url = "https://openweathermap.org/img/wn/";
 //       ImageView imageView = findViewById(R.id.coord);
        Picasso.get()
                .load(url + pngUrl + ".png")
                .transform(new IconTransformation())
                .placeholder(R.mipmap.ic_1_6)
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

    private void requestRetrofit(String city, String metric, String keyApi) {
        openWeather.loadWeather(city, metric, keyApi)
                .enqueue(new Callback<ResponseWeather>() {

                    public void onResponse(Call<ResponseWeather> call, Response<ResponseWeather> response) {
                        if (response.body() != null) {
                            float result = response.body().getMain().getTemp();
                            textTemp.setText(Float.toString(result));
                            String dis = response.body().getWeather().get(0).getDescription();
                            description.setText(dis);
                            pngUrl = response.body().getWeather().get(0).getIcon();
                            picasso(pngUrl); // передаем "weather: icon"
                        }
                        if (response.errorBody() != null) {
                            textTemp.setText("no data");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseWeather> call, Throwable t) {
                        textTemp.setText("Error");
                    }
                });
    }

    private void initEvents() {
        final String editApiKey = "0ecf8658c4caf135dd4f087798c91ffb";
        final String metric = "metric";
        Button button = findViewById(R.id.buttonretrofit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestRetrofit(editCity.getText().toString(), metric, editApiKey);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Thread.sleep(2000);
                            saveSharedPrefs();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                                          }
                }).start();
            }
        });
    }

    // Для регистрации Broadcast Receiver
    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(weatherFinishedReceiver, new IntentFilter(BROADCAST_ACTION_WEATHERFINISHED));
    }

    protected void onStop() {
        super.onStop();
        unregisterReceiver(weatherFinishedReceiver);
    }

    // Button onClick показание WeatherService
    public void onClickWeatherService(View v) {
        tempservice = findViewById(R.id.tempService);
        WeatherService.startWeatherService(MainActivity.this);
    }


    private Toolbar initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    private void initDrawer(Toolbar toolbar) {
        NavigationView navigationView = findViewById(R.id.nav_view);

//     final androidx.constraintlayout.widget.ConstraintLayout mainContent = findViewById(R.id.mainContent);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        // пока не настроил перемещение Navigation Drawer вместе с экраном
//        {
//
//            public void onDrawerSlide(android.view.View drawerView, float slideOffset){
//                super.onDrawerSlide(drawerView, slideOffset);
//
//                float slideX = drawerView.getWidth() * slideOffset;
//                mainContent.setTranslationX(slideX);
//            }
//        };
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

        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "переход на сайт", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_favorite) {
            Toast.makeText(getApplicationContext(), "Список любимых городов", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Toast.makeText(getApplicationContext(), "House", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_location) {
            Toast.makeText(getApplicationContext(), "City", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_geolocation) {
            Toast.makeText(getApplicationContext(), "Map", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_history) {
            Toast.makeText(getApplicationContext(), "list of open cities", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_setting) {
            Toast.makeText(getApplicationContext(), "Setting", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_send) {
            Toast.makeText(getApplicationContext(), "Write to developer", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_developer) {
            Toast.makeText(getApplicationContext(), "About the developer", Toast.LENGTH_SHORT).show();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Button onClick показание датчика температуры
    public void onClickSensTemp(View v) {
        sensorManager.registerListener(listenerSensor, sensorTemp,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    // Button onClick показание датчика влажности
    public void onClickSensHumidity(View v) {
        sensorManager.registerListener(listenerSensorHum, sensorHumidity,
                SensorManager.SENSOR_DELAY_NORMAL);
    }


    SensorEventListener listenerSensor = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            temp.setText(String.valueOf(event.values[0]));
        }

    };

    SensorEventListener listenerSensorHum = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            hum.setText(String.valueOf(event.values[0]));
        }

    };

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listenerSensor);
        sensorManager.unregisterListener(listenerSensorHum);
    }

    // Получатель широковещательного сообщения
    private BroadcastReceiver weatherFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final long result = intent.getLongExtra(WeatherService.EXTRA_RESULT, 0);
            // Потокобезопасный вывод данных
            tempservice.post(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    tempservice.setText(Long.toString(result));
                }
            });
        }
    };

//    //     сохранение города и температуры
//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString("CITY", editCity);
//      }
//
//
//    @Override
//    protected void onRestoreInstanceState(Bundle saveInstanceState) {
//        super.onRestoreInstanceState(saveInstanceState);
//        editCity = saveInstanceState.getString("CITY");
//    }
}
