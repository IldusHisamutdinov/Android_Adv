package com.example.menu.weather;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.menu.MainActivity;
import com.example.menu.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class CitySelection extends AppCompatActivity {

    private TextInputEditText city;
    private Pattern checkCity = Pattern.compile("^[А-Я][а-я]{2,}$"); // русский
    private TextInputLayout cityLayout;
    private EditText seach;
    private ArrayAdapter<String> adapter;
    public final static String TOWN = "town";

    @SuppressLint({"CutPasteId", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selection);
        TextInputEditText city;
        city = findViewById(R.id.inputCity);
        cityLayout = findViewById(R.id.city);

        city.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) return;
                TextView tv = (TextView) v;
                String value = tv.getText().toString();
                if (checkCity.matcher(value).matches()) {
                    cityLayout.setError(null);
                } else {
                    cityLayout.setError("Это не город");
                }
            }
        });


        @SuppressLint("CutPasteId") final EditText text = findViewById(R.id.inputCity);
        Button startAct = findViewById(R.id.button2);
        startAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(TOWN, text.getText().toString());
                startActivity(intent);
                finish();
            }
        });


        final ListView list = findViewById(R.id.listView);

        final String[] city2 = getResources().getStringArray(R.array.city);
        adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, city2);
        list.setAdapter(adapter);


        EditText txt = findViewById(R.id.inputCity);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                txt.setText(parent.getItemAtPosition(position).toString());
            }
        });

        seach = (TextInputEditText) findViewById(R.id.inputCity);

        seach.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                CitySelection.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });

    }

    public void onClick(View view) {
        Snackbar.make(view, "Выберите город из списка или напишите", Snackbar.LENGTH_LONG)
                .show();
    }

}
