package com.example.haidar.streamaccelerometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Process;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static android.os.SystemClock.elapsedRealtime;


/**
 * Created by haidar  on 2015-10-30.
 * StreamAccelerometer
 */
public class AccelerometerDataSampler extends Thread implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private TcpStreamWrite mTcpStreamWrite;
    private String mHostIP;
    private int mHostport;
    private String mLabel;
    private boolean mTraining ;
    private JSONObject mJSON;
    private boolean running;
    private BlockingQueue<JSONObject> mQTCP = new ArrayBlockingQueue<JSONObject>(100);
    private BlockingQueue<double[]> mQ = new ArrayBlockingQueue<double[]>(100);
    private static final DecimalFormat mDF = new DecimalFormat("0.000");
    private int mSamplingRate;
    private long mStartTime = elapsedRealtime();

    public AccelerometerDataSampler(SensorManager mSensorManager ,String mHostIP , int mHostport ,
                                    String mLabel , boolean mTraining){
        this.mSensorManager = mSensorManager;
        this.mHostIP        = mHostIP;
        this.mHostport      = mHostport;
        this.mLabel         = mLabel;
        this.mTraining      = mTraining;
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
                    double [] record = mQ.take();
                    // [ TIME , X , Y ,Z ]
                    long timestamp = Double.valueOf(record[0]).longValue();
                    mJSON = new JSONObject();
                    mJSON.put("TimeStamp", String.valueOf(timestamp));
                    mJSON.put("x", mDF.format(record[1]));
                    mJSON.put("y", mDF.format(record[2]));
                    mJSON.put("z", mDF.format(record[3]));
                    mJSON.put("label", mLabel);
                    mQTCP.put(mJSON);
                    // rough sampling rate
                    mSamplingRate++;
                    if(elapsedRealtime() > mStartTime + 1000 ){
                        mStartTime = elapsedRealtime();
                        Log.d("Sampling Rate", String.valueOf(mSamplingRate));
                        mSamplingRate = 0 ;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.getCause();
                }
            }
        } else {
            while (running) {
                try {
                    double [] record =  mQ.take();
                    // [ TIME , X , Y ,Z ]
                    long timestamp = Double.valueOf(record[0]).longValue();

                    mJSON = new JSONObject();
                    mJSON.put("TimeStamp", String.valueOf(timestamp));
                    mJSON.put("x", mDF.format(record[1]));
                    mJSON.put("y", mDF.format(record[2]));
                    mJSON.put("z", mDF.format(record[3]));
                    mQTCP.put(mJSON);
                    // rough sampling rate
                    mSamplingRate++;
                    if(elapsedRealtime() > mStartTime + 1000 ){
                        mStartTime = elapsedRealtime();
                        Log.d("Sampling Rate", String.valueOf(mSamplingRate));
                        mSamplingRate = 0 ;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.getCause();
                }
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // event time uses elapsedRealtime() (uptime include deep sleep)
        // uptimeMillis() and System.nanoTime() (uptime without deep sleep)
        // System.currentTimeMillis() unix time .

        long mTimeStampEvent = TimeUnit.MILLISECONDS.convert(event.timestamp, TimeUnit.NANOSECONDS);
        // [ TIME , X , Y ,Z ]
            double [] record =
                    {
                            mTimeStampEvent ,
                            event.values[0] ,
                            event.values[1] ,
                            event.values[2]
                    };
        mQ.add(record);
    }

    public void setRuning(boolean running) {

        this.running = running;
        if(!running){
            mSensorManager.unregisterListener(this, mAccelerometer);
            this.interrupt();
            Log.d("mQ", String.valueOf(mQ.remainingCapacity()));
            Log.d("mQTCP", String.valueOf(mQTCP.remainingCapacity()));
            mTcpStreamWrite.setRuning(false);
        }
    }
}