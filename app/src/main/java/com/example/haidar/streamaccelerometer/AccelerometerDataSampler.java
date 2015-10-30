package com.example.haidar.streamaccelerometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haidar on 2015-10-30.
 */
public class AccelerometerDataSampler extends Thread implements SensorEventListener {

    private List<JSONObject> dataQueue;
    private SensorManager sensorManager;
    private long SENSOR_READING_PERIOD = 20000;
    private long endTime;
    public static final DecimalFormat mDF = new DecimalFormat("0.000");

    public List<JSONObject> getDataQueue() {
        return dataQueue;
    }

    public void setSensorManager(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
    }


    public void run() {
        sensorManager.registerListener((SensorEventListener) this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);

        dataQueue = new ArrayList<JSONObject>();
        endTime = System.currentTimeMillis() + SENSOR_READING_PERIOD;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (System.currentTimeMillis() < endTime) {
            float[] values = event.values;
            float vx = values[0];
            float vy = values[1];
            float vz = values[2];

            Log.d("INFO", vx + "/" + vy + "/" + vz);
            JSONObject mJSON = new JSONObject();
            try {
                mJSON.put("x", mDF.format(vx));
                mJSON.put("y", mDF.format(vy));
                mJSON.put("z", mDF.format(vz));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //mJSON.put("TimeStamp" ,String.valueOf(new Date().getTime()));
            dataQueue.add(mJSON);
        } else {
            sensorManager.unregisterListener((SensorEventListener) this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
            Log.d("INFO", "READING ENDED");

        }
    }
}

