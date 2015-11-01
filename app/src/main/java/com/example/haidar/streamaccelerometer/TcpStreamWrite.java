package com.example.haidar.streamaccelerometer;

import android.util.Log;

import org.json.JSONObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class TcpStreamWrite extends Thread  {
    private boolean running;
    private JSONObject mJSON;
    private PrintWriter pw;
    private String mHostIP;
    private int mHostport;
    private BlockingQueue<JSONObject> mQueue = new ArrayBlockingQueue<JSONObject>(100);
    private long count;

    public TcpStreamWrite(String mHostIP , int mHostport ,BlockingQueue<JSONObject> mQueue ){
        this.mHostIP    = mHostIP;
        this.mHostport  = mHostport;
        this.mQueue     = mQueue;
    }
    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_FOREGROUND);
        try {
            pw = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    new Socket(mHostIP,mHostport).getOutputStream()
                            )
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(running){
            try {
                mJSON = mQueue.take();
                pw.print(mJSON);
                count++;
                pw.flush();
                // to push 100 record at time
            /*
                if(count%100==0){
                    pw.flush();
                }
            */
            } catch (InterruptedException e) {
                Log.d("TCP", "-----------------INTERRUP-------------------");
            }
        }
    }
    public void setRuning(boolean running){
        this.running = running;
        if(!running){
            this.interrupt();
            Log.d("Number of sent records", String.valueOf(count));
            Log.d("Last record", mJSON.toString());
            pw.flush();
            pw.close();
        }
    }
}