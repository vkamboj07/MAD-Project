package com.mad.q3.sensorreader;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mad.q3.sensorreader.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ActivityMainBinding binding;
    private SensorManager sensorManager;
    @Nullable
    private Sensor accelerometer;
    @Nullable
    private Sensor light;
    @Nullable
    private Sensor proximity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }

        binding.textAccelStatus.setText(accelerometer == null
                ? getString(R.string.sensor_missing, getString(R.string.accelerometer))
                : getString(R.string.sensor_ok, getString(R.string.accelerometer)));
        binding.textLightStatus.setText(light == null
                ? getString(R.string.sensor_missing, getString(R.string.light))
                : getString(R.string.sensor_ok, getString(R.string.light)));
        binding.textProxStatus.setText(proximity == null
                ? getString(R.string.sensor_missing, getString(R.string.proximity))
                : getString(R.string.sensor_ok, getString(R.string.proximity)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            }
            if (light != null) {
                sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_UI);
            }
            if (proximity != null) {
                sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER: {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                binding.textAccelValues.setText(getString(R.string.accel_format, x, y, z));
                break;
            }
            case Sensor.TYPE_LIGHT: {
                float lux = event.values[0];
                binding.textLightValue.setText(getString(R.string.light_format, lux));
                break;
            }
            case Sensor.TYPE_PROXIMITY: {
                float distance = event.values[0];
                float maxRange = event.sensor.getMaximumRange();
                binding.textProxValue.setText(getString(R.string.proximity_format, distance, maxRange));
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
       
    }
}
