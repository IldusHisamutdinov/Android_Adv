package com.example.menu;

import android.content.Intent;
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
import com.google.android.material.textfield.TextInputEditText;
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
    private TextView tempservice;
    private OpenWeather openWeather;
    private TextView textTemp; // Температура (в градусах)
    private TextView description;
    private TextInputEditText editCity;
    private String png;
    private String url;

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
        initEvents();
        picasso(png);
    }

    private void initGui() {
        textTemp = findViewById(R.id.tempretrofit);
        editCity = findViewById(R.id.editCity);
        description = findViewById(R.id.description);
    }

    private void picasso(String p) {
        p = png;
        url = "https://openweathermap.org/img/wn/";
        ImageView imageView = findViewById(R.id.coord);
        Picasso.get()
                .load(url + png + ".png")
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

    private void requestRetrofit(String lat, String lon, String metric, String keyApi) {
        openWeather.loadWeather(lat, lon, metric, keyApi)
                .enqueue(new Callback<ResponseWeather>() {

                    public void onResponse(Call<ResponseWeather> call, Response<ResponseWeather> response) {
                        if (response.body() != null) {
                            float result = response.body().getMain().getTemp();
                            textTemp.setText(Float.toString(result));
                            String dis = response.body().getWeather().get(0).getDescription();
                            description.setText(dis);
                            png = response.body().getWeather().get(0).getIcon();
                            picasso(png); // передаем "weather: icon"
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
       //         requestRetrofit(editCity.getText().toString(), metric, editApiKey);
       //         requestRetrofit();
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
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
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


}
