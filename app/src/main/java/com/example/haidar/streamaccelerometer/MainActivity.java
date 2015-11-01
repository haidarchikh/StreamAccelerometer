package com.example.haidar.streamaccelerometer;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.*;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private SensorManager mSensorManager;
    private EditText hostName;
    private EditText hostPort;
    private String mHostIP;
    private int mHostPort;
    private Switch mSwitch;
    private Spinner mSpinner ;
    private String mLabel;
    private AccelerometerDataSampler mSampler;
    private boolean streaming = false;
    private boolean mTraining;
    private PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_FOREGROUND);

        mWakeLock =((PowerManager) getSystemService(POWER_SERVICE))
                .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TCP_Accelerometer");

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        hostName = (EditText) findViewById(R.id.editTextIP);
        hostPort = (EditText) findViewById(R.id.editTextPort);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mSpinner.setOnItemSelectedListener(this);
        mSwitch = (Switch) findViewById(R.id.switch1);

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                        mSpinner.setVisibility(View.VISIBLE);
                        mTraining = true;
                } else {
                    // The toggle is disabled
                    mSpinner.setVisibility(View.INVISIBLE);
                    mTraining = false;
                }
            }
        });
        // Assign the array to the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.labels,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

    }
    public void myStreamHandler(View view){
        mHostIP = hostName.getText().toString();
        mHostPort = Integer.parseInt(hostPort.getText().toString());
        switch (view.getId()) {
            case R.id.startStreaming:
                if(!streaming){
                    mSampler = new AccelerometerDataSampler(
                            mSensorManager ,
                            mHostIP,
                            mHostPort ,
                            mLabel ,
                            mTraining
                    );
                    mSampler.setRuning(true);
                    mSampler.start();
                    streaming = true;
                    mWakeLock.acquire();
                }
                break;
            case R.id.stopStreaming:
                if(streaming){
                    mSampler.setRuning(false);
                    streaming = false;
                    mWakeLock.release();
                }
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mLabel = (String) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}