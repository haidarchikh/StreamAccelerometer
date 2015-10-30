package com.example.haidar.streamaccelerometer;



import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
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
    private String mLabel;
    private volatile float mTiltX;
    private volatile float mTiltY;
    private volatile float mTiltZ;
    private volatile long mTimeStamp;
    private boolean mTraining ;
    private static final long NS2MS = 1L / 1000000L;
    public static final DecimalFormat mDF = new DecimalFormat("0.000");
    private BlockingQueue<JSONObject> mQueue = new ArrayBlockingQueue<JSONObject>(100);



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
                    mJSON.put("TimeStamp" ,String.valueOf(mTimeStamp));
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
                    mJSON.put("TimeStamp" ,String.valueOf(mTimeStamp));
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


    public void stream(float tiltX, float tiltY , float tiltZ, long timestamp ) {
        mTiltX = tiltX;
        mTiltY = tiltY;
        mTiltZ = tiltZ;
        mTimeStamp = (new Date()).getTime()-(System.nanoTime()-timestamp) * NS2MS;
    }


    public void setRuning(boolean running){this.running = running;}
}

