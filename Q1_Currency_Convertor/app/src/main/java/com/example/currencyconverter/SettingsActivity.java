package com.example.currencyconverter;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemePrefs.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        SwitchMaterial themeSwitch = findViewById(R.id.themeSwitch);
        TextView statusText = findViewById(R.id.themeStatusText);

        boolean isDark = ThemePrefs.isDark(this);
        themeSwitch.setChecked(isDark);
        statusText.setText(isDark ? R.string.theme_dark : R.string.theme_light);

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ThemePrefs.setDark(this, isChecked);
            statusText.setText(isChecked ? R.string.theme_dark : R.string.theme_light);
            recreate();
        });
    }
}
