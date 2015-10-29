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
    private boolean mGo = true;
    public static final DecimalFormat mDF = new DecimalFormat("0.000");
    private static final int MAX_CONCURRENT_THREADS = 1;
    private final Semaphore mLock = new Semaphore(MAX_CONCURRENT_THREADS, true);

    public TcpStreamWrite(String IP , int port , String label){
        mHostIP = IP;
        mHostport = port;
        mLabel = label;
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
        // create a0. socket
        while(running){
            if(mGo){
                pw.println(mJSON);
                pw.flush();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        pw.close();
    }


    public void streamTraining(float tiltX, float tiltY , float tiltZ ) {

        String X = mDF.format(tiltX);
        String Y = mDF.format(tiltY);
        String Z = mDF.format(tiltZ);
        mGo = false;
        mJSON = new JSONObject();
            try {
                mJSON.put("x", X);
                mJSON.put("y", Y);
                mJSON.put("z", Z);
                mJSON.put("Label", mLabel);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        mGo = true;
        }
    public void stream(float tiltX, float tiltY , float tiltZ ) {

        String X = mDF.format(tiltX);
        String Y = mDF.format(tiltY);
        String Z = mDF.format(tiltZ);
        mGo = false;
        mJSON = new JSONObject();
        try {
            mJSON.put("x", X);
            mJSON.put("y", Y);
            mJSON.put("z", Z);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mGo = true;
    }


    public void setRuning(boolean running){this.running = running;}
}

