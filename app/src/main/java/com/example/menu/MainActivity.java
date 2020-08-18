package com.example.menu;


import android.annotation.SuppressLint;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


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

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // получение состояния сети
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);//Произошло изменение сетевого подключения.
        registerReceiver(wifiMonitor, intentFilter);
        initNotificationChannel();
        getToken();
        // уровень заряда батареи
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_LOW));
        textToken = findViewById(R.id.token);


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

    private void getToken() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("PushMessage", "getInstanceId failed", task.getException());
                            return;
                        }


                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        textToken.setText(token);

                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiMonitor);
        unregisterReceiver(batteryReceiver);
    }

    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("4", "name", importance);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private void initDrawer(Toolbar toolbar) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        databaseHelper = App.getInstance().getDatabaseInstance();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add: {
                startActivity(new Intent(this, WeatherActivity.class));
                break;
            }
        }
        return false;
    }



    protected void onResume() {
        super.onResume();
        CityRecyclerAdapter recyclerAdapter = new CityRecyclerAdapter(this, databaseHelper.getDataDao().getAllData());
        recyclerAdapter.setOnDeleteListener(this);
        recyclerView.setAdapter(recyclerAdapter);

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

    @Override
    public void onDelete(DataModel dataModel) {
        databaseHelper.getDataDao().delete(dataModel);
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
