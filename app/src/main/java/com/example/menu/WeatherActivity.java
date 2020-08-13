package com.example.menu;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.menu.database.DatabaseHelper;
import com.example.menu.model.DataModel;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class WeatherActivity extends AppCompatActivity {

    private OpenWeather openWeather;
    private TextView textTemp; // Температура (в градусах)
    private TextView description;
    private TextView editCity;
    private String pngUrl;
    private String NAME_CITY = "nameCity"; // для SharedPreferences
    private String TEMP_T = "temp"; // для SharedPreferences
    private ImageView imageView;
    private TextView date;


    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);

        initRetorfit();
        initGui();
        initEvents();
        textTown();
    }

    private void initGui() {
        textTemp = findViewById(R.id.tempretrofit);
        editCity = findViewById(R.id.editCity);
        description = findViewById(R.id.description);
        imageView = findViewById(R.id.coord);
        date = findViewById(R.id.date);
    }

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    public void textTown() {
        String town = editCity.getText().toString();

        if (town != null) {
            loadSharedPrefs();
            picasso();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    private void picasso() { //при первой загрузки активити
        ImageView imageView = findViewById(R.id.coord);
        Picasso.get()
                .load(R.mipmap.ic_1_6)
                //               .transform((Transformation) new IconTransformation())
                .into(imageView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    private void picasso(String icon) {
        icon = pngUrl;
        String url;
        url = "https://openweathermap.org/img/wn/";
        //       ImageView imageView = findViewById(R.id.coord);
        Picasso.get()
                .load(url + pngUrl + ".png")
                //            .transform((Transformation) new IconTransformation())
                .placeholder(R.mipmap.ic_1_6)
                .into(imageView);
    }

    private void initRetorfit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        openWeather = retrofit.create(OpenWeather.class);
    }

    private void requestRetrofit(String city, String metric, String keyApi) {
        openWeather.loadWeather(city, metric, keyApi)
                .enqueue(new Callback<ResponseWeather>() {

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(retrofit2.Call<ResponseWeather> call, Response<ResponseWeather> response) {

                        if (response.body() != null) {
                            int result = response.body().getMain().getTemp();
                            textTemp.setText(Integer.toString(result));
                            String dis = response.body().getWeather().get(0).getDescription();
                            description.setText(dis);
                            date.setText(TimeDate.g());
                            pngUrl = response.body().getWeather().get(0).getIcon();
                            picasso(pngUrl); // передаем "weather: icon"
                        }
//                        if (response.errorBody() != null) {
//                            textTemp.setText("no data");
//                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ResponseWeather> call, Throwable t) {
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
                            Thread.sleep(3000);
                            saveSharedPrefs();
                            result();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

    }


    public void result() {
        DatabaseHelper databaseHelper = App.getInstance().getDatabaseInstance();

        DataModel model = new DataModel();

        model.setCity(editCity.getText().toString());
        model.setTemp(textTemp.getText().toString());
        model.setDate(date.getText().toString());

        databaseHelper.getDataDao().insert(model);

        finish();
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
        String valueFirst = sharedPref.getString(NAME_CITY, keys);
        String valueSecond = sharedPref.getString(TEMP_T, value);
        editCity.setText(valueFirst);
        textTemp.setText(valueSecond);
    }

}
