package com.example.menu;

import android.os.Bundle;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = initToolbar();
        initDrawer(toolbar);
    }

    private Toolbar initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    private void initDrawer(Toolbar toolbar) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        }else if (id == R.id.nav_developer) {
            Toast.makeText(getApplicationContext(), "About the developer", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
