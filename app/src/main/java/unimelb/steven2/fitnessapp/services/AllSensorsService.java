package unimelb.steven2.fitnessapp.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
//import android.support.v4.content.LocalBroadcastManager;
//import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.legacy.content.WakefulBroadcastReceiver;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import unimelb.steven2.fitnessapp.database.DatabaseHandler;
import unimelb.steven2.fitnessapp.utils.Utils;

public class AllSensorsService extends Service implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer,mGyroscope,mBarometer, mTemperature, mHumidity;
    private Sensor mStepCounter;
    private String state;
    private static final String TAG = "accgyroService";
    DatabaseHandler db;

    private static final float NS2S = 1.0f / 1000000000.0f;
    float timestamp = 0;
    float dT = 0;
    float sumdT = 0;
    float dT_previous = 0;

    Date currentTime;
    Date iniTime = null;

    String strDate;

    boolean samplingrateOK = true;

    float init_dT =0;
    float acc_dT = 0;

    private String latitude;
    private String longitude;

    float ax=0, ay=0 , az=0;
    float gx=0, gy=0 , gz=0;
    float bp=0;
    float tp = 0;
    float hm = 0;
    int sc=0;
    //float sd=0;

    //boolean stepdetected = false;

    PowerManager.WakeLock lock;


    //float initial_step_count= 0;




    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mBarometer = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        //mTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        //mHumidity = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        //mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("my-state"));

        db = new DatabaseHandler(getApplicationContext());


    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //state=(String) intent.getExtras().get("state");

        state = "blablabla";

        //latitude = (String)intent.getExtras().get("iniLatitude");
        //longitude = (String)intent.getExtras().get("iniLongitude");

        latitude = "";
        longitude = "";

        mSensorManager.registerListener(this, mAccelerometer, 50000); //sampling rate: 20Hz or 50ms
        mSensorManager.registerListener(this, mGyroscope, 50000);
        mSensorManager.registerListener(this, mBarometer, 50000);
        //mSensorManager.registerListener(this, mTemperature, 50000);
        //mSensorManager.registerListener(this, mHumidity, 50000);
        //mSensorManager.registerListener(this, mStepCounter, 50000);

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SensorRead");
        lock.acquire();

        //return flags;
        return START_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        lock.release();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.i(TAG,"onsensorchanged");

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        currentTime = c.getTime();
        strDate = sdf.format(currentTime);

        Log.i("AAA_before_ctime",String.valueOf(currentTime));
        Log.i("AAA_before_strdate",strDate);

        Sensor sensor = event.sensor;
        if (sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
            ax = event.values[0];
            ay = event.values[1];
            az = event.values[2];
        }else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gx = event.values[0];
            gy = event.values[1];
            gz = event.values[2];
//        }else if (sensor.getType() == Sensor.TYPE_STEP_COUNTER){
//            sc = event.values[0];
//            stepdetected =true;
//            Log.i("STEP1",String.valueOf(sc));
        }else if (sensor.getType() == Sensor.TYPE_PRESSURE) {
            bp = event.values[0];
        }

        //if (state != null && latitude != null && longitude != null)
        if (state != null)
        {

            Log.i("AAA_before",String.valueOf(acc_dT));

            if (iniTime != null) {
                dT = Utils.getRate(currentTime,iniTime);
                sumdT = sumdT + dT;
                acc_dT = acc_dT + dT;
            }

//            if (initial_step_count==0){
//                initial_step_count = sc;
//            }


//            if (acc_dT > 0.1){
//                Log.i("AAA_1",String.valueOf(dT));
//                Log.i("AAA_2",String.valueOf(currentTime));
//                Log.i("AAA_3",String.valueOf(iniTime));
//            }


            if (dT != 0){

                if (acc_dT > 0.03) {
                    samplingrateOK=true;
                    Log.i("AAA_4",String.valueOf(acc_dT));
                }else{
                    Log.i("AAA_5",String.valueOf(acc_dT));
                }

                if (samplingrateOK) {
//                    if (acc_dT > 1) {
//                        Log.i("AAA_6", String.valueOf(acc_dT));
//                        Log.i("AAA_7", String.valueOf(dT));
//                    }
//
//
//                    if (stepdetected)
//                        sc = sc - initial_step_count;
//
//
//                    Log.i("STEP",String.valueOf(sc));

//                    float level = getBatteryLevel();
//                    Log.i(TAG,String.valueOf(level));

                    Log.i(TAG, strDate + " " + state);
                    db.addSensingData2(strDate, String.format("%.3f", sumdT), String.format("%.3f", acc_dT), null,//state,
                            String.format("%.6f", ax), String.format("%.6f", ay), String.format("%.6f", az),
                            String.format("%.6f", gx), String.format("%.6f", gy), String.format("%.6f", gz),
                            String.format("%.6f", bp));

                    acc_dT = 0;
                    samplingrateOK = false;
                    //stepdetected =false;

                }

            } //fin del dT!=0


            iniTime = currentTime;


        }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Send an Intent with an action named "my-event".
    private void sendMessage(String mMonitorAcce ) {
        Intent intent = new Intent("my-event");
        // add data
        intent.putExtra("message", mMonitorAcce);
        intent.putExtra("sensor",TAG);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


//
//    public float getBatteryLevel() {
//        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
//        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
//        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
//
//        // Error checking that probably isn't needed but I added just in case.
//        if(level == -1 || scale == -1) {
//            return 50.0f;
//        }
//
//        return ((float)level / (float)scale) * 100.0f;
//    }


    private WakefulBroadcastReceiver mMessageReceiver = new WakefulBroadcastReceiver()  {
        @Override
        public void onReceive(Context context, Intent intent) {

            try{
                String bState = intent.getExtras().getString("state");
                if ( bState != null) //Just to avoid null on state
                    state = bState;

            }catch (Exception ex) {

            }

            try{
                String bSteps = intent.getExtras().getString("steps");
                if ( bSteps != null) //Just to avoid null on state

                    //sc = Integer.parseInt(bSteps);

                    //These steps come from the step counter app

                    switch (state){
                        case "walking":
                            sc = Integer.parseInt(bSteps) * 50;
                            break;
                        case "jogging":
                            sc = Integer.parseInt(bSteps) * 50;
                            break;
                        case "bike":
                            sc = Integer.parseInt(bSteps) * 25;
                            break;
                        case "upstairs":
                            sc = Integer.parseInt(bSteps) * 5;
                            break;
                        case "downstairs":
                            sc = Integer.parseInt(bSteps) * 5;
                            break;
                        default:
                            sc = Integer.parseInt(bSteps);
                            break;
                    }

                    Log.i("SC","act: " + state);
                    Log.i("SC",String.valueOf(sc));

            }catch (Exception ex) {

            }



            try{
                Log.i(TAG,"receiving coordinates");

                String bLatitude = intent.getExtras().getString("latitude");
                String bLongitude = intent.getExtras().getString("longitude");

                // if (bLatitude!=null && bLongitude!=null) {
                latitude = bLatitude;
                longitude = bLongitude;
                //}
            }catch (Exception ex){
                Log.i(TAG,"No coordinates found");
            }


        }
    };





}

