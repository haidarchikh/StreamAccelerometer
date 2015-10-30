package com.example.haidar.streamaccelerometer;

import org.json.JSONObject;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class TcpStreamWrite extends Thread  {
    private boolean running;
    private Socket mySocket;
    private JSONObject mJSON;
    private OutputStream os;
    private PrintWriter pw;
    private String mHostIP;
    private int mHostport;
    private BlockingQueue<JSONObject> mQueue = new ArrayBlockingQueue<JSONObject>(100);



    public TcpStreamWrite(String mHostIP , int mHostport ,BlockingQueue<JSONObject> mQueue ){
        this.mHostIP = mHostIP;
        this.mHostport = mHostport;
        this.mQueue = mQueue;
    }
    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_FOREGROUND);
        try {
            mySocket = new Socket(mHostIP,mHostport);
            os = mySocket.getOutputStream();
            pw = new PrintWriter(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(running){
            try {
                mJSON = mQueue.take();
            } catch (InterruptedException e) {
            }
            pw.println(mJSON);
            pw.flush();
        }
        pw.close();
    }
    public void setRuning(boolean running){this.running = running;}
}

