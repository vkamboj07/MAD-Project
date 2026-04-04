package com.example.currencyconverter;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private EditText amountInput;
    private Spinner fromSpinner;
    private Spinner toSpinner;
    private TextView resultText;
    private TextView rateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemePrefs.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        amountInput = findViewById(R.id.amountInput);
        fromSpinner = findViewById(R.id.fromSpinner);
        toSpinner = findViewById(R.id.toSpinner);
        resultText = findViewById(R.id.resultText);
        rateText = findViewById(R.id.rateText);

        ArrayAdapter<Currency> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                Currency.values()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);

        fromSpinner.setSelection(Currency.INR.ordinal());
        toSpinner.setSelection(Currency.USD.ordinal());

        findViewById(R.id.swapButton).setOnClickListener(v -> swapCurrencies());
        findViewById(R.id.settingsButton).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));

        AdapterView.OnItemSelectedListener recalcListener = new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { recalc(); }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        };
        fromSpinner.setOnItemSelectedListener(recalcListener);
        toSpinner.setOnItemSelectedListener(recalcListener);

        amountInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { recalc(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        recalc();
    }

    private void swapCurrencies() {
        int from = fromSpinner.getSelectedItemPosition();
        int to = toSpinner.getSelectedItemPosition();
        fromSpinner.setSelection(to);
        toSpinner.setSelection(from);
    }

    private void recalc() {
        Currency from = (Currency) fromSpinner.getSelectedItem();
        Currency to = (Currency) toSpinner.getSelectedItem();

        // Show exchange rate hint
        if (from != null && to != null) {
            BigDecimal oneUnit = Rates.convert(BigDecimal.ONE, from, to)
                    .setScale(4, RoundingMode.HALF_UP)
                    .stripTrailingZeros();
            rateText.setText(String.format(Locale.US, "1 %s = %s %s", from.code, oneUnit.toPlainString(), to.code));
        }

        String raw = amountInput.getText() == null ? "" : amountInput.getText().toString().trim();
        if (raw.isEmpty() || raw.equals(".")) {
            resultText.setText(getString(R.string.result_placeholder));
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(raw);
        } catch (NumberFormatException e) {
            resultText.setText(getString(R.string.result_invalid));
            return;
        }

        BigDecimal out = Rates.convert(amount, from, to)
                .setScale(4, RoundingMode.HALF_UP)
                .stripTrailingZeros();

        String formatted = String.format(Locale.US, "%s %s", out.toPlainString(), to != null ? to.code : "");
        resultText.setText(formatted);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
