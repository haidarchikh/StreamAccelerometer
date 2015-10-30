package com.example.haidar.streamaccelerometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Process;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
/**
 * Created by haidar  on 2015-10-30.
 * StreamAccelerometer
 */
public class AccelerometerDataSampler implements SensorEventListener , Runnable {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private TcpStreamWrite mTcpStreamWrite;
    private String mHostIP;
    private int mHostport;
    private String mLabel;
    private boolean mTraining ;
    private JSONObject mJSON;
    private volatile float mTiltX;
    private volatile float mTiltY;
    private volatile float mTiltZ;
    private volatile long mTimeStamp;
    private boolean running;
    private BlockingQueue<JSONObject> mQTCP = new ArrayBlockingQueue<JSONObject>(100);
    private BlockingQueue<Integer> mQ = new ArrayBlockingQueue<Integer>(100);
    private static final long NS2MS = 1L / 1000000L;
    private static final DecimalFormat mDF = new DecimalFormat("0.000");

    public AccelerometerDataSampler(SensorManager mSensorManager ,String mHostIP , int mHostport ,
                                    String mLabel , boolean mTraining){
        this.mSensorManager = mSensorManager;
        this.mHostIP = mHostIP;
        this.mHostport = mHostport;
        this.mLabel = mLabel;
        this.mTraining = mTraining;
    }

    public void run() {

        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        mTcpStreamWrite = new TcpStreamWrite(mHostIP, mHostport, mQTCP);
        mTcpStreamWrite.setRuning(true);
        mTcpStreamWrite.start();

        if (mTraining) {
            while (running) {
                try {
                    mQ.take();
                    mJSON = new JSONObject();
                    mJSON.put("TimeStamp", String.valueOf(mTimeStamp));
                    mJSON.put("x", mDF.format(mTiltX));
                    mJSON.put("y", mDF.format(mTiltY));
                    mJSON.put("z", mDF.format(mTiltZ));
                    mJSON.put("label", mLabel);
                    mQTCP.put(mJSON);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            while (running) {
                try {
                    mQ.take();
                    mJSON = new JSONObject();
                    mJSON.put("TimeStamp", String.valueOf(mTimeStamp));
                    mJSON.put("x", mDF.format(mTiltX));
                    mJSON.put("y", mDF.format(mTiltY));
                    mJSON.put("z", mDF.format(mTiltZ));
                    mQTCP.put(mJSON);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        mSensorManager.unregisterListener(this, mAccelerometer);
        mTcpStreamWrite.setRuning(false);
        mTcpStreamWrite.interrupt();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mTiltX = event.values[0];
        mTiltY = event.values[1];
        mTiltZ = event.values[2];
        mTimeStamp = System.currentTimeMillis();
    //  mTimeStamp = System.currentTimeMillis()-(System.nanoTime()-event.timestamp) * NS2MS;
        mQ.add(0);
    }
    public void setRuning(boolean running){this.running = running;}
}