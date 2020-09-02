package com.example.menu.weather;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class DialogCity extends DialogFragment {

    @NonNull
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setTitle("Поиск...")
                .setMessage("Для этой местности нет данных")
                .setPositiveButton("OK", null)
                .create();
    }
}