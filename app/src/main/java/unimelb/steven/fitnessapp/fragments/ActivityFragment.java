package unimelb.steven.fitnessapp.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import unimelb.steven.fitnessapp.BootCompletedIntentReceiver;
import unimelb.steven.fitnessapp.DeviceListActivity;
import unimelb.steven.fitnessapp.MainActivity;
import unimelb.steven.fitnessapp.R;
import unimelb.steven.fitnessapp.database.DatabaseHandler;
import unimelb.steven.fitnessapp.services.ARService;
import unimelb.steven.fitnessapp.services.AllSensorsService;
import unimelb.steven.fitnessapp.services.BluetoothChatService;
import unimelb.steven.fitnessapp.services.Constants;
import unimelb.steven.fitnessapp.utils.Utils;

import static android.content.Context.MODE_PRIVATE;

//import com.google.android.gms.maps.model.LatLng;


/**
 * Fragment for the userfeed
 */
public class ActivityFragment extends Fragment {


    private static final String ARG_ACCESS_TOKEN = "access_token";
    private static final String ARG_CLIENT_ID = "client_id";


    private static String mAccessToken;
    private String mClientId;


    ArrayAdapter<CharSequence> adapter;

    static Boolean userScrolled = false; //user scrolling down throug list
    Boolean userSwiped = false; //user swiping to refresh
    static Boolean lockScroll = false; //to avoid duplicated requests due to scroll issues

    private String TAG = "ActivityFragment";

    public static ActivityFragment activityFragment;

    private View rootView;

    private OnFragmentInteractionListener mListener;

    private Chronometer chronometer;

    private ImageButton btnStart;
    private ImageButton btnPause;
    private ImageButton btnStop;
    private Button timecircle;
    private Button btnGPS;

    private Intent iAccelerometer,iGyroscope, iBarometer,iHumidity, iTemperature;
    private Intent iAllSensors;
    private Intent iSendData;
    private Intent iAR;
    private Intent iEE;

    private String state;

    private Spinner spinner;

    private Boolean pauseflag = false;
    private Boolean startflag = false;

    private long timeWhenStopped = 0;

    private TextView statusgps;

    double userLatitude;
    double userLongitude;
    DatabaseHandler db;

    String iniLatitude;
    String iniLongitude;


    public LocationManager mLocationManager;
    //public GPSLocation.MyLocationListener mLocationListener;

    //ArrayList<LatLng> LatLngs = new ArrayList<>();

    String strDate;

    CountDownTimer countDownTimer;

    CountDownTimer sleepcountDownTimer;

    //GPSLocation gpsLocation;

    int SECS_TO_PREPARE = 20;

    TextView tvCurrentActivity;

    TextView tvARService;

    public static TextView tvPowerSavingMode;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;


    public SharedPreferences.Editor editor;
    public static SharedPreferences prefs;
    String sprefs;

    public SharedPreferences pref2;
    SharedPreferences.Editor editor2;


    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * Local Bluetooth adapter
     */
    private static BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private static BluetoothChatService mChatService = null;



    public static ActivityFragment newInstance(String accesstoken, String client_id) {
        ActivityFragment fragment = new ActivityFragment();
        return fragment;
    }

    public ActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG,"onCreateView");

        if (rootView != null) {
            Log.i(TAG,"onCreateView with rootviewnotnull");
            return rootView;
        }




        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("activityfragment"));

        getFragmentManager().popBackStack();

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_activity,container,false);

        db = new DatabaseHandler(getActivity().getApplicationContext());

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss.SSS");
//
//        Date originalDate = new Date();
//        String strDate1 = sdf.format(originalDate);
//
//
//        ArrayList<Calories2> calories = db.getCalories2(strDate1);
//        if (calories == null) { //Create the 5 rows for this day
//            db.addCaloriesData2(strDate1,"Walking","0","0","0");
//            db.addCaloriesData2(strDate1,"Jogging","0","0","0");
//            db.addCaloriesData2(strDate1,"Cycling","0","0","0");
//            db.addCaloriesData2(strDate1,"Upstairs","0","0","0");
//            db.addCaloriesData2(strDate1,"Downstairs","0","0","0");
//        }


        pref2 = getActivity().getApplicationContext().getSharedPreferences("mypref",MODE_PRIVATE);
        editor2 = pref2.edit();

        tvARService = (TextView)rootView.findViewById(R.id.tvService);
        tvPowerSavingMode = (TextView)rootView.findViewById(R.id.tvPowerSavingMode);

        btnStart = (ImageButton) rootView.findViewById(R.id.ibtnStart);
        btnStop = (ImageButton)rootView.findViewById(R.id.ibtnStop);

        editor = getActivity().getPreferences(MODE_PRIVATE).edit();
        prefs = getActivity().getPreferences(MODE_PRIVATE);

        if (MainActivity.inpowersavingmode){
            tvPowerSavingMode.setText("Power Saving Mode Enabled");
            tvPowerSavingMode.setTextColor(Color.GREEN);
        }else{
            tvPowerSavingMode.setText("Power Saving Mode Disabled");
            tvPowerSavingMode.setTextColor(Color.RED);
        }


        sprefs = prefs.getString("ARService",null);
        if (sprefs!=null){
            Log.i("ACC",sprefs);

            if (sprefs.equals("false")) {
               // tvARService.setText("AR Service Stopped");
                MainActivity.ARServiceRunning = false;
            }else{
               // tvARService.setText("AR Service Started");
                MainActivity.ARServiceRunning = true;
            }
        }else{
            Log.i("ACC","sprefs is null");
        }



        if (MainActivity.ARServiceRunning){
            tvARService.setText("AR Service Started");
            btnStart.setVisibility(View.GONE);
            btnStop.setVisibility(View.VISIBLE);
        } else {
            tvARService.setText("AR Service Stopped");
            btnStart.setVisibility(View.VISIBLE);
            btnStop.setVisibility(View.GONE);
        }


        tvCurrentActivity = (TextView)rootView.findViewById(R.id.tvcurrentactivity);

        spinner = (Spinner)rootView.findViewById(R.id.spinner_activities);
        adapter = ArrayAdapter.createFromResource(getActivity(),R.array.activities_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                state = parent.getItemAtPosition(position).toString();
                sendState(state);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }


        chronometer = (Chronometer) rootView.findViewById(R.id.chronometer);

        timecircle = (Button)rootView.findViewById(R.id.btn_timecircle);

        timecircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!startflag && !pauseflag){
                    if (SECS_TO_PREPARE==5)
                        SECS_TO_PREPARE=20;
                    else
                        SECS_TO_PREPARE=5;

                    timecircle.setText(String.valueOf(SECS_TO_PREPARE));
                }
            }
        });






        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Only start sensing if GPS is enabled (But only if I want to walk, jog or ride a bike...for other activities GPS doesnt matter)
            //    if (statusgps.getText().toString().equals("GPS Enabled")){//|| state!="walking" || state!="jogging" || state!="bike") {

                    //to get quicky a possible location
//                    SingleShotLocationProvider.requestSingleUpdate(getActivity(),
//                            new SingleShotLocationProvider.LocationCallback() {
//                                @Override
//                                public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
//                                    Log.d("Location", "my location is " + String.valueOf(location.longitude));
//                                    iniLatitude = String.valueOf(location.latitude);
//                                    iniLongitude = String.valueOf(location.longitude);
//                                    //sendCoord(String.valueOf(location.latitude), String.valueOf(location.longitude));
//                                }
//                            });

//                    if (pauseflag) {
//                        chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
//                        chronometer.start();
//                        sensorsStarted();
//
//
//                    }else { //If this is the first time Play is pressed
//
//                        db.deleteNullRows(); //deleted rows where finished=0 and record=null
//
//                        countDownTimer = new CountDownTimer(SECS_TO_PREPARE*1000,1000) {
//
//                            public void onTick(long millisUntilFinished) {
//
//                                timecircle.setText(String.valueOf(millisUntilFinished / 1000));
//                                spinner.setEnabled(false);
//                                //Log.i("seconds remaining", String.valueOf(millisUntilFinished / 1000));
//
//                            }
//
//                            public void onFinish() {
//                                chronometer.setBase(SystemClock.elapsedRealtime());
//                                chronometer.start();
//                                sensorsStarted();
//
//                                Calendar c = Calendar.getInstance();
//                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
//                                strDate = sdf.format(c.getTime());
//
//                                //get GPS location
//                                //GPSLocation gpsLocation = new GPSLocation();
//                                //gpsLocation.execute();
//
//                                timecircle.setVisibility(View.GONE);
//                                btnStop.setVisibility(View.VISIBLE);
//                            }
//
//
//                        }.start();
//
//                    }


                    sensorsStarted();

                    editor.putString("ARService","true");
                    editor.commit();

                    editor2.putString("message","true");
                    editor2.commit();
                    //BootCompletedIntentReceiver.editor.putString("ARService","true");
                    //BootCompletedIntentReceiver.editor.commit();

                    tvARService.setText("AR Service Started");

                    btnStart.setVisibility(View.GONE);
                    btnStop.setVisibility(View.VISIBLE);

              //  } else {
                  //  Toast.makeText(getActivity(),"Please enable GPS...",Toast.LENGTH_SHORT).show();
              //  }


            }
        });




        btnPause = (ImageButton)rootView.findViewById(R.id.ibtnPause);

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!startflag){
                    countDownTimer.cancel();
                    timecircle.setText(String.valueOf(SECS_TO_PREPARE));
                    spinner.setEnabled(true);
                    btnStart.setVisibility(View.VISIBLE);
                    btnPause.setVisibility(View.GONE);
                }else {
                    timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
                    chronometer.stop();
                    pauseflag = true;
                    sensorsStopped();
                    spinner.setEnabled(true);
                    btnStart.setVisibility(View.VISIBLE);
                    btnPause.setVisibility(View.GONE);
                }
            }
        });



        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //statusgps.setText("GPS Disabled");

                spinner.setEnabled(true);

                double elapsedTime = SystemClock.elapsedRealtime() - chronometer.getBase();
                Log.i(TAG, "total time : " + String.valueOf(elapsedTime));

                double elapsedTimeInMin = elapsedTime/60000;
                Log.i(TAG, "total time in min: " + String.valueOf(elapsedTimeInMin));

//                chronometer.setBase(SystemClock.elapsedRealtime());
//                chronometer.stop();
//
//                timeWhenStopped = 0;
//                pauseflag = false;

                editor.putString("ARService","false");
                editor.commit();

                editor2.putString("message","false");
                editor2.commit();

                //BootCompletedIntentReceiver.editor.putString("ARService","false");
                //BootCompletedIntentReceiver.editor.commit();

                tvARService.setText("AR Service Stopped");
                sensorsStopped();

                //TODO: disabled by now because it speeds down the app, change it later
                //LatLngs=db.getCoordinates();
//                if (LatLngs !=null && LatLngs.size()>0) {
//                    float totalDistance = calculateDistance(LatLngs);
//                    double speed = calculateSpeed(totalDistance,elapsedTime);
//
//                    if (speed < 0.5) speed =0;
//
//                    double VO2;
//
//                    //TODO: calculate the gradient
//                    double gradient = 1;
//
//                    //TODO: assign different formulas depending on the activity
//                    if (state.equals("walking")) {
//                        VO2 = (0.1*speed) + (1.8*speed*gradient) + 3.5;
//                    }else { //should only be for running
//                        VO2 = (0.2*speed) + (0.9*speed*gradient) + 3.5;
//                    }
//
//                    //TODO: the weight should be entered by the user
//                    double myWeight = 70.5;// 70.5 is my weight in Kilos
//
//
//                    double VO2noKg = VO2 * myWeight;
//
//                    double caloriesBurnedPerOxygenLiter = 5; // 5 kcal are burned per liter of oxygen consumed
//
//                    double calories = (VO2noKg/1000) * caloriesBurnedPerOxygenLiter * elapsedTimeInMin;
//
//                    Log.i(TAG, "Speed : " + speed + " m/min");
//                    Log.i(TAG, "Calories:" + calories + " Kcal");
//
//                    db.addCaloriesData(strDate,state,String.format("%.2f",elapsedTimeInMin),
//                            String.format("%.2f",totalDistance),
//                            String.format("%.2f",speed),
//                            String.format("%.2f",calories));
//                }

                //db.updateOnStop();
                db.deleteNullRows();

                btnStart.setVisibility(View.VISIBLE);
//                btnPause.setVisibility(View.GONE);
//                timecircle.setVisibility(View.VISIBLE);
//                timecircle.setText(String.valueOf(SECS_TO_PREPARE));
                btnStop.setVisibility(View.GONE);



            }
        });


        btnGPS = (Button)rootView.findViewById(R.id.btnGPS);

        btnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.displayPromptForEnablingGPS(getActivity());
                //callGPS();
            }
        });


        statusgps = (TextView)rootView.findViewById(R.id.statusGPS);


        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


        return rootView;

    }

    private double calculateSpeed (float distanceinmeters,double timeinminutes) {
        return distanceinmeters/timeinminutes;
    }

//    private float calculateDistance(ArrayList<LatLng> latLngs) {
//        Log.i(TAG,"calculating distance");
//
//        float totalDistance = 0;
//        float[] results = new float[latLngs.size()-1];
//
//        for (int i=0;i<latLngs.size()-1;i++){
//            Location.distanceBetween(latLngs.get(i).latitude,latLngs.get(i).longitude,
//                    latLngs.get(i+1).latitude, latLngs.get(i+1).longitude,
//                    results);
//        }
//
//        if (results.length>3) {
//            for(int i=3; i<results.length; i++){ //because the first three results always are wrong.
//                Log.i(TAG,"distance " + i +": "+results[i]);
//                totalDistance = totalDistance + results[i];
//            }
//        } else {
//            totalDistance = 0;
//        }
//
//
//
//        Log.i(TAG, "total distance: " + String.valueOf(totalDistance));
//
//        if (totalDistance < 1) totalDistance=0;
//
//        return totalDistance;
//    }

    private void sendState(String state) {
        Log.i(TAG,"sendState:" + state);
        Intent intent = new Intent("my-state");
        // add data

        try{
            Integer.parseInt(state); //just to check if it is a number
            intent.putExtra("steps", state);
        }catch (Exception e){
            intent.putExtra("state", state);
        }
        //intent.putExtra("state", state);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    private void sensorsStarted() {

        startflag = true;

        //then I can a more accurate location every 30 seconds.
       // gpsLocation = new GPSLocation(30); //updates gps location with minTime of 30seconds
       // gpsLocation.execute();


//
//        try {
//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
//            r.play();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //Toast.makeText(getActivity(), "Sensors started", Toast.LENGTH_SHORT).show();

//        iSendData = new Intent(getActivity(),SendDataService.class);
//        getActivity().startService(iSendData);

        iAR = new Intent(getActivity(),ARService.class);
        getActivity().startService(iAR);

//        iEE = new Intent(getActivity(),EEService.class);
//        getActivity().startService(iEE);

        iAllSensors = new Intent(getActivity(),AllSensorsService.class);
        iAllSensors.putExtra("state", state);

        if (iniLatitude!=null && iniLongitude!=null){
            iAllSensors.putExtra("iniLatitude",iniLatitude);
            iAllSensors.putExtra("iniLongitude",iniLongitude);
        }

        getActivity().startService(iAllSensors);

//        iAccelerometer = new Intent(getActivity(),AccelerometerService.class);
//        iAccelerometer.putExtra("state", state);
//        getActivity().startService(iAccelerometer);
//
//        iGyroscope = new Intent(getActivity().getApplicationContext(),GyroscopeService.class);
//        iGyroscope.putExtra("state", state);
//        getActivity().startService(iGyroscope);
//
//        iBarometer = new Intent(getActivity().getApplicationContext(),BarometerService.class);
//        iBarometer.putExtra("state", state);
//        getActivity().startService(iBarometer);
//
//        iHumidity = new Intent(getActivity().getApplicationContext(),HumidityService.class);
//        iHumidity.putExtra("state", state);
//        getActivity().startService(iHumidity);
//
//        iTemperature = new Intent(getActivity().getApplicationContext(), TemperatureService.class);
//        iTemperature.putExtra("state", state);
//        getActivity().startService(iTemperature);


    }

    private void sensorsStopped() {

        if (BootCompletedIntentReceiver.sleepcountDownTimer!=null)
            BootCompletedIntentReceiver.sleepcountDownTimer.cancel();

        startflag = false;
        //Toast.makeText(getActivity(), "Sensors stopped", Toast.LENGTH_SHORT).show();

//        File filename = new File(Environment.getExternalStorageDirectory()+"/mylog.txt");
//
//        try {
//            filename.createNewFile();
//            String cmd = "logcat -d -f"+filename.getAbsolutePath();
//            Runtime.getRuntime().exec(cmd);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //getActivity().stopService(new Intent(getActivity().getApplicationContext(), SendDataService.class));

        getActivity().stopService(new Intent(getActivity().getApplicationContext(), ARService.class));

        //getActivity().stopService(new Intent(getActivity().getApplicationContext(), EEService.class));

        getActivity().stopService(new Intent(getActivity().getApplicationContext(), AllSensorsService.class));

//        getActivity().stopService(new Intent(getActivity().getApplicationContext(), AccelerometerService.class));
//        getActivity().stopService(new Intent(getActivity().getApplicationContext(), GyroscopeService.class));
//        getActivity().stopService(new Intent(getActivity().getApplicationContext(), BarometerService.class));
//        getActivity().stopService(new Intent(getActivity().getApplicationContext(), HumidityService.class));
//        getActivity().stopService(new Intent(getActivity().getApplicationContext(), TemperatureService.class));

//        if (mChatService != null) {
//            mChatService.stop();
//        }

        if (spinner.isEnabled()) {
            int spinnerPosition = adapter.getPosition(tvCurrentActivity.getText().toString());
            spinner.setSelection(spinnerPosition);
        }

        //gpsLocation.onCancelled();
        //mLocationManager.removeUpdates(mLocationListener); //stop updating GPS every 30 seconds

    }

    // Default methods when creating new Fragment
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        Log.i("eee","onAttach");
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        Log.i("eee","onDetach");
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }


    @Override
    public void onResume() {
        super.onResume();

        Log.i(TAG, "onResume");

        if (mChatService!=null){
            Log.i(TAG,"mchatservice is NOT null");
        }else {
            Log.i(TAG,"mchatservice is null");
        }

//        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
//            statusgps.setText("GPS Enabled");
//        else
//            statusgps.setText("GPS Disabled");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                //mChatService.start();
            }
        }

    }


    @Override
    public void onStart() {
        super.onStart();

        Log.i(TAG,"onStart");

        if (mChatService!=null){
            Log.i(TAG,"mchatservice is NOT null");
        }else {
            Log.i(TAG,"mchatservice is null");
        }

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
//        if (!mBluetoothAdapter.isEnabled()) {
//            //Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            //startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
//            // Otherwise, setup the chat session
//        } else if (mChatService == null) {
//            //setupChat();
//        }
    }

    /**
     * Isn't being used
     */
//    public void callGPS() {
//
//        if (statusgps.getText().toString().equals("GPS Disabled")) {
//
//            final GPSLocation gpsLocation = new GPSLocation(5); //updates gps location with minTime of 5seconds
//            gpsLocation.execute();
//
//            countDownTimer = new CountDownTimer(60000,1000) { //get GPS coordinates for 1 min
//                public void onTick(long millisUntilFinished) {
//
//                }
//                public void onFinish() {
//                    gpsLocation.onCancelled();
//                    mLocationManager.removeUpdates(mLocationListener); //stop updating GPS every 5 seconds
//
////                    GPSLocation gpsLocation = new GPSLocation(30); //start updating GPS every 30 seconds
////                    gpsLocation.execute();
//
//                }
//
//
//            }.start();
//
//
//        } else { //if GPS is already enabled
//            Utils.displayPromptForEnablingGPS(getActivity());
//            statusgps.setText("GPS Disabled");
//            mLocationManager.removeUpdates(mLocationListener); //stop updating GPS every 30 seconds
//        }
//
//    }

    private void sendCoord(String latitude, String longitude) {
        Intent intent = new Intent("my-state");
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    /**
     * Handles the getting of gps coordinates from the mobile phone
     * both providers (network and GPS) make the requestlocation
     */
//    public class GPSLocation extends AsyncTask<String, Integer, String> {
//        public Handler mHandler;
//
//        ProgressDialog progDailog = null;
//
//        public int time = 0;
//        public boolean outOfTime = false;
//
//        public double lati = 0;
//        public double longi = 0;
//
////        public LocationManager mLocationManager;
////        public MyLocationListener mLocationListener;
//
//        String provider1,provider2;
//
//        public int rate=30;
//
//        public GPSLocation(int rate){
//            this.rate = rate;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            mLocationListener = new MyLocationListener();
////            mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//
//            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                statusgps.setText("GPS Disabled");
//                Utils.displayPromptForEnablingGPS(getActivity());
//                GPSLocation.this.cancel(true);
//            }else{
//
//                //Network provider criteria
//                Criteria criteria1 = new Criteria();
//                criteria1.setAccuracy(Criteria.ACCURACY_COARSE);
//                criteria1.setPowerRequirement(Criteria.POWER_LOW);
//                provider1 = mLocationManager.getBestProvider(criteria1,true);
//
//                //GPS provider criteria
//                Criteria criteria2 = new Criteria();
//                criteria2.setAccuracy(Criteria.ACCURACY_FINE);
//                provider2 = mLocationManager.getBestProvider(criteria2,true);
//
//
//                if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//                    mLocationManager.requestLocationUpdates(
//                            provider1, rate*1000, 0, //update location every rate*1000 ms
//                            mLocationListener);
//                } else { //GPS provider
//                    mLocationManager.requestLocationUpdates(
//                            provider2, rate*1000, 0, //update location every rate*1000 ms
//                            mLocationListener);
//                }
//
//            }
//
//
//            progDailog = new ProgressDialog(getActivity());
//            progDailog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                @Override
//                public void onCancel(DialogInterface dialog) {
//                    GPSLocation.this.cancel(true);
//                    statusgps.setText("GPS disabled");
//                }
//            });
//            progDailog.setMessage("Getting GPS location... ");
//            progDailog.setIndeterminate(true);
//            progDailog.setCancelable(true);
//            progDailog.show();
//
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            Timer t =new Timer();
//
//            TimerTask tk = new TimerTask() {
//
//                @Override
//                public void run() {
//                    outOfTime = true;
//                }
//            };
//
//            t.schedule(tk, 1700000); //to wait approximately 30 secs.
//
//            while(!outOfTime){
//                if (lati != 0 && longi != 0) {
//                    t.cancel();
//                    tk.cancel();
//                    break;
//                }
//            }
//
//            if (outOfTime) return "err";
//
//            return "ok";
//        }
//
//
//        @Override
//        protected void onCancelled(){
//            System.out.println("Cancelled by user!");
//            progDailog.dismiss();
//            //mLocationManager.removeUpdates(mLocationListener);
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            progDailog.dismiss();
//            //mLocationManager.removeUpdates(mLocationListener);
//
//            if (result.equals("ok")) {
//                userLatitude = lati;
//                userLongitude = longi;
//
//                statusgps.setText("GPS Enabled");
//
//                Toast.makeText(getActivity(), "GPS location: " + String.valueOf(userLatitude) + " "+ String.valueOf(userLongitude) , Toast.LENGTH_SHORT).show();
//
//                Log.i(TAG,
//                        "GPS location AQUI: " + String.valueOf(userLatitude) + " " + String.valueOf(userLongitude));
//                sendCoord(String.valueOf(userLatitude), String.valueOf(userLongitude));
//
//            }else {
//                Toast.makeText(getActivity(),
//                        "Please be located in an area of greater coverage or try again",
//                        Toast.LENGTH_LONG).show();
//
//            }
//
//
//        }
//
//
//        public class MyLocationListener implements LocationListener {
//
//            @Override
//            public void onLocationChanged(Location location) {
//                try {
//                    lati = location.getLatitude();
//                    longi = location.getLongitude();
//
//
//
//                    sendCoord(String.valueOf(lati), String.valueOf(longi));
//
//                    Log.i(TAG,
//                            "GPS location: " + String.valueOf(lati) + " " + String.valueOf(longi));
//
//                } catch (Exception e) {
//                    Log.i(TAG,e.getMessage());
//                }
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//                Log.i(TAG, "OnProviderDisabled");
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//                Log.i(TAG, "onProviderEnabled");
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status,
//                                        Bundle extras) {
//                Log.i(TAG, "onStatusChanged");
//
//            }
//
//        }
//
//    }


    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        // mConversationArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);

        //mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        //mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        //mSendButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // Send a message using content of the edit text widget
//                View view = getView();
//                if (null != view) {
//                    TextView textView = (TextView) view.findViewById(R.id.edit_text_out);
//                    String message = textView.getText().toString();
//                    sendMessage(message);
//                }
//            }
//        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(getActivity(), mHandler);

        // Initialize the buffer for outgoing messages
        //mOutStringBuffer = new StringBuffer("");
    }

    /**
     * Makes this device discoverable.
     */
//    private void ensureDiscoverable() {
//        if (mBluetoothAdapter.getScanMode() !=
//                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
//            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//            startActivity(discoverableIntent);
//        }
//    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), "Not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            //mOutStringBuffer.setLength(0);
            //mOutEditText.setText(mOutStringBuffer);
        }
    }



    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final android.os.Handler mHandler = new android.os.Handler() {

        @Override
        public void handleMessage(Message msg) {

            Log.i(TAG,"onhandleMessage: " + String.valueOf(msg.what));
            //FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            //setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            //mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            //setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            //setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.i(TAG, "mensaje: " + readMessage);

                    try{
                        Integer.parseInt(readMessage);

                    }catch (Exception e) {
                        Log.i(TAG,"currentActivity: " + tvCurrentActivity.getText().toString());
                        tvCurrentActivity.setText(readMessage);
                    }

                    sendState(readMessage);

                    if (spinner.isEnabled()) {
                        int spinnerPosition = adapter.getPosition(readMessage);
                        spinner.setSelection(spinnerPosition);
                    }


                    //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    //String devicename=msg.getData().getString(Constants.DEVICE_NAME);
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (getActivity()!=null) {
                        Toast.makeText(getActivity(), "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (getActivity()!=null) {
                        Toast.makeText(getActivity(), msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG,"onActivityResult2");
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    /**
     * Establish connection with other divice
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    public static void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device

        if (mChatService!=null){
            mChatService.connect(device, secure);
        }

    }


    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }




    private WakefulBroadcastReceiver mMessageReceiver = new WakefulBroadcastReceiver()  {
        @Override
        public void onReceive(Context context, Intent intent) {

//            try{
//                String flag = intent.getExtras().getString("sensorsrunning");
//                if ( flag != null) {//Just to avoid null on state
//                    if (startflag==false)
//                        sensorsStarted();
//                }
//
//            }catch (Exception ex) {
//
//            }


//            try{
//                String restart = intent.ACTION_BOOT_COMPLETED;
//                if ( restart != null) {//Just to avoid null on state
//
//                    Log.i("ABCDE","starting several times");
//                    sensorsStarted();
//                    //Log.i("ABCDE",activity);
//                }
//
//            }catch (Exception ex) {
//
//            }

//            try{
//                String flag_powersavingmode = intent.getExtras().getString("powermode");
//                if ( flag_powersavingmode != null) //Just to avoid null on state
//
//
//
//
//            }catch (Exception ex) {
//
//            }

            try{
                String activity = intent.getExtras().getString("prediction");
                if ( activity != null) //Just to avoid null on state
                    sendMessage(activity);
                //Log.i("ABCDE",activity);


            }catch (Exception ex) {

            }


            try{
                String calories = intent.getExtras().getString("calories");
                if ( calories != null) //Just to avoid null on state
                    sendMessage(calories);

            }catch (Exception ex) {

            }


            try{
                String activity = intent.getExtras().getString("sleep");
                Log.i("ACC_AR","sleep message arrived");
                if ( activity != null) //Just to avoid null on state
                    sleepSystem();


            }catch (Exception ex) {
                Log.i("ACC_AR","sleep message problem");
            }

        }
    };



    private void sleepSystem(){
        //stop both services
        sensorsStopped();
        //start a countdonw of 5 minutes //300*1000
        sleepcountDownTimer = new CountDownTimer(15*1000,1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                sensorsStarted();
            }


        }.start();



    }
}
