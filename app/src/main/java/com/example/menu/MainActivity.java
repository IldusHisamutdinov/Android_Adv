package com.example.menu;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
        temp = findViewById(R.id.temp);
        hum = findViewById(R.id.h);

        ImageView imageView = findViewById(R.id.coord);
        Picasso.get()
                .load("http://openweathermap.org/img/wn/01d.png")
    //            .transform(new CircleTransformation())
                .rotate(1)
                .placeholder(R.mipmap.ic_1_6)
                .into(imageView);

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


}
