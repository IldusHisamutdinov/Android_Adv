package com.example.menu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE = 10;
    Marker currentMarker;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private Button back;
    private Button button;
    public final static String LAT = "lat";
    public final static String LON = "lon";
    private LatLng location = new  LatLng(0,0);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        requestPemissions();
        createGoogleApiClient();
        initNotificationChannel();

        back = findViewById(R.id.back);
        button = findViewById(R.id.button);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        currentMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .anchor(0.5f, 0.5f)
        //        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current))
                .title("Current Position"));

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Marker marker = addMarker(latLng);
                Geofence geofence = createGeofence(marker);
                createGeofencingRequest(geofence);

            }
        });

    }

    public void BackTemp(View view) {
        String latit = Double.toString(location.latitude);
        String lontit = Double.toString(location.longitude);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(LAT, latit);
        intent.putExtra(LON, lontit);
        startActivity(intent);
    }

    // Добавление меток на карту
    private Marker addMarker(LatLng location) {
        String title = Double.toString(location.latitude) + "," + Double.toString(location.longitude);
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(title));

        mMap.addCircle(new CircleOptions()
                .center(location)
                .radius(150)
                .strokeColor(Color.BLUE));

        return marker;
    }

    @SuppressLint("MissingPermission")
    private void createGeofencingRequest(Geofence geofence) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        // вешаем триггеры на вход, перемещение внутри и выход из зоны
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT | GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofence(geofence);  // Добавим геозону
        GeofencingRequest geoFenceRequest = builder.build();  // это запрос на добавление геозоны (параметры только что задавали, теперь строим)
        // создадим интент, при сигнале от Google Play будет вызываться этот интент, а интент настроен на запуск службы, обслуживающей всё это
        Intent geoService = new Intent(MapActivity.this, GeoFenceService.class);
        // интент будет работать через этот класс
        PendingIntent pendingIntent = PendingIntent
                .getService(MapActivity.this, 0, geoService, PendingIntent.FLAG_UPDATE_CURRENT);
        // это клиент геозоны, собственно он и занимается вызовом нашей службы
        GeofencingClient geoClient = LocationServices.getGeofencingClient(MapActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geoClient.addGeofences(geoFenceRequest, pendingIntent);   // добавляем запрос запрос геозоны и указываем, какой интент будет при этом срабатывать
    }

    private Geofence createGeofence(Marker marker) {
        // создаем геозону через построитель.
        return new Geofence.Builder()
                .setRequestId(String.valueOf(marker.getTitle()))   // Здесь указывается имя геозоны (вернее это идентификатор, но он строковый)
                // типа геозоны, вход, перемещение внутри, выход
                .setTransitionTypes(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT | GeofencingRequest.INITIAL_TRIGGER_DWELL)
                .setCircularRegion(marker.getPosition().latitude, marker.getPosition().longitude, 150) // Координаты геозоны
                .setExpirationDuration(Geofence.NEVER_EXPIRE)   // Геозона будет постоянной, пока не удалим геозону или приложение
                .setLoiteringDelay(1000)    // Установим временную задержку в мс между событиями входа в зону и перемещения в зоне
                .build();

    }
    // Геозоны работают через службы Google Play
    // поэтому надо создать клиента этой службы
    // И соединится со службой
    private void createGoogleApiClient() {
        // Создаем клиента службы GooglePlay
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)   // Укажем, что нам нужна геолокация
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
     }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    // Запрос координат
    @SuppressLint("MissingPermission")
    private void requestLocation() {
        // Если пермиссии все таки нет - то просто выйдем, приложение не имеет смысла
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        // Получить менеджер геолокаций
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Будем получать геоположение через каждые 10 секунд или каждые 10 метров
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();// Широта
                double lng = location.getLongitude();// Долгота
                // Перепестить карту на текущую позицию
                LatLng currentPosition = new LatLng(lat, lng);
                LatLng prevPosition = currentMarker.getPosition();
                if (!(prevPosition.longitude == 0 && prevPosition.latitude == 0)) {
                    mMap.addPolyline(new PolylineOptions()
                            .add(prevPosition, currentPosition)
                            .color(Color.RED)
                            .width(5));
                }
                currentMarker.setPosition(currentPosition);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, (float) 15));

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

    // Запрос пермиссий
    private void requestPemissions() {
        // Проверим на пермиссии, и если их нет, запросим у пользователя
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // запросим координаты
            requestLocation();
        } else {
            // пермиссии нет, будем запрашивать у пользователя
            requestLocationPermissions();
        }
    }

    // Запрос пермиссии для геолокации
    private void requestLocationPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Запросим эти две пермиссии у пользователя
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }

    // Это результат запроса у пользователя пермиссии
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {   // Это та самая пермиссия, что мы запрашивали?
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Все препоны пройдены и пермиссия дана
                // Запросим координаты
                requestLocation();
            }
        }
    }

    // На Андроидах версии 26 и выше необходимо создавать канал нотификации
    // На старых версиях канал создавать не надо
    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel("2", "name", importance);
            notificationManager.createNotificationChannel(mChannel);
        }
    }

    public void Back(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}