package com.example.haidar.streamaccelerometer;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.*;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.concurrent.Semaphore;


public class TcpStreamWrite extends Thread  {
    private boolean running = true;
    private Socket mySocket;
    private JSONObject mJSON;
    private OutputStream os;
    private PrintWriter pw;
    private String mHostIP;
    private int mHostport;
    private String mLabel;
    private volatile float mTiltX;
    private volatile float mTiltY;
    private volatile float mTiltZ;
    private boolean mTraining ;
    public static final DecimalFormat mDF = new DecimalFormat("0.000");



    public TcpStreamWrite(String IP , int port , String label , boolean training){
        mHostIP = IP;
        mHostport = port;
        mLabel = label;
        mTraining = training;
    }
    @Override
    public void run() {
        // Server address
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_FOREGROUND);
        try {
            //InetAddress serverAddr = InetAddress.getByName(mHostIP);
            //mySocket = new Socket(serverAddr ,mHostport);
            mySocket = new Socket(mHostIP,mHostport);
            os = mySocket.getOutputStream();
            pw = new PrintWriter(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(mTraining){
            while(running){

                mJSON = new JSONObject();
                try {
                    mJSON.put("x", mDF.format(mTiltX));
                    mJSON.put("y", mDF.format(mTiltY));
                    mJSON.put("z", mDF.format(mTiltZ));
                    mJSON.put("label", mLabel);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pw.println(mJSON);
                pw.flush();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            pw.close();

        }else {
            while(running){

                mJSON = new JSONObject();
                try {
                    mJSON.put("x", mDF.format(mTiltX));
                    mJSON.put("y", mDF.format(mTiltY));
                    mJSON.put("z", mDF.format(mTiltZ));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pw.println(mJSON);
                pw.flush();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            pw.close();
        }

    }


    public void stream(float tiltX, float tiltY , float tiltZ ) {
        mTiltX = tiltX;
        mTiltY = tiltY;
        mTiltZ = tiltZ;
    }


    public void setRuning(boolean running){this.running = running;}
}

