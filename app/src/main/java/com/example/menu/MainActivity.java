package com.example.menu;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menu.dao.AccessToData;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    private SensorManager sensorManager;
    private Sensor sensorTemp;
    private Sensor sensorHumidity;
    private TextView temp;
    private TextView hum;
//    private CityRecyclerAdapter adapter;
    private CitySource citySource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = initToolbar();
        initDrawer(toolbar);
        setSupportActionBar(toolbar);

        initRecyclerView();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorTemp = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensorHumidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        temp = findViewById(R.id.temp);
        hum = findViewById(R.id.h);
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

    // Button onClick показание температуры
    public void onClickSensTemp(View v) {
        sensorManager.registerListener(listenerSensor, sensorTemp,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    // Button onClick показание влажности
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


    // Инициализация списка
    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        AccessToData accessToData = App
                .getInstance()
                .getAccessToData();
        citySource = new CitySource(accessToData);


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }




}
