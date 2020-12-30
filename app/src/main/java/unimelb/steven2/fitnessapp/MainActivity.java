package unimelb.steven2.fitnessapp;

//import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
//import android.support.design.widget.TabLayout;
//import android.support.v4.view.ViewPager;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
//import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager;
//import androidx.viewpager2.widget.ViewPager2;
import unimelb.steven2.fitnessapp.adapters.CustomViewPager;
import unimelb.steven2.fitnessapp.adapters.MainFragmentPagerAdapter;
import unimelb.steven2.fitnessapp.database.DatabaseHandler;
import unimelb.steven2.fitnessapp.fragments.ActivityFragment;
import unimelb.steven2.fitnessapp.models.Profile;
import unimelb.steven2.fitnessapp.services.ARService;


public class MainActivity extends AppCompatActivity {

    String appFolderPath;
    String systemPath;

    String dataPredictPath;
    String modelPath;
    String outputPath;

    private Toolbar toolbar;
    TabLayout tabLayout;
    CustomViewPager viewPager;
    public static String TAG = MainActivity.class.getSimpleName();
    double userLatitude;
    double userLongitude;
    DatabaseHandler db;
    ArrayList<String> LatLngs;

    ArrayList<Profile> profile;

    public SharedPreferences.Editor editor;
    public SharedPreferences prefs;

    String sprefs;

    public SharedPreferences.Editor editor2;

    public static Boolean inpowersavingmode = false;
    public static Boolean ARServiceRunning = false;

    public static String walkingStepParams ="gy,5";
    public static String joggingStepParams ="gz,1";
    public static String cyclingStepParams ="gy,5";
    public static String upstairsStepParams ="gz,1";
    public static String downstairsStepParams ="gx,5";


    public static MainFragmentPagerAdapter pagerAdapter;
    public static double userHeight = 174; //cm
    public static double userWeight = 70; //kg
    public static String userGender = "male";
    public static double userWalkingStrideLength = 0; //cm
    public static double userJoggingStrideLength = 0; //cm
    public static double userBikeWheelCircumference = 2.096; //in meters


    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;

//    static {
//        System.loadLibrary("jnilibsvm");
//    }
//
//    private native void jniSvmTrain(String cmd);
//    private native void jniSvmPredict(String cmd);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editor = getPreferences(MODE_PRIVATE).edit();

        editor2 = getSharedPreferences("myprefpowersavingmode",MODE_PRIVATE).edit(); //getPreferences(MODE_PRIVATE).edit();
        prefs = getPreferences(MODE_PRIVATE);

        profile = new ArrayList<Profile>();

        Log.i(TAG, "onCreate");

        if (savedInstanceState!=null){
            Log.i(TAG,"nuevo");
        }


        sprefs = prefs.getString("powersavingmode",null);
        if (sprefs!=null){
            Log.i("ACC",sprefs);

            if (sprefs.equals("false"))
                inpowersavingmode = false;
            else
                inpowersavingmode = true;
        }else{
            Log.i("ACC","sprefs is null");
        }

        Log.i("ACC_ARService3",inpowersavingmode.toString());



        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //toolbar.setLogo(R.drawable.instagram_text_logo);
        getSupportActionBar().setTitle("Fitness Mate");

        //visibleFragment = new ActivityFragment(); //the Default first visible Fragment

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (CustomViewPager) findViewById(R.id.viewpager); //use this custom viewpager to avoid the user to sliding through tabs
//        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        pagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        viewPager.setAdapter(pagerAdapter);



        // Give the TabLayout the ViewPager
        viewPager.setOffscreenPageLimit(1);
        //viewPager.setPagingEnabled(false); //to avoid the swipe gesture between tabs


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i(TAG,"onpagescrolled");
            }

            @Override
            public void onPageSelected(int position) {
                //Change the toolbar according to the Fragment

                if (position == 0) {
                    //getSupportActionBar().setTitle(null);
                   // tabLayout.setVisibility(View.VISIBLE);
                   // getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    //visibleFragment = new UserFeedFragment();
                } else {
                    //getSupportActionBar().setTitle(null);
                   // getSupportActionBar().setLogo(null);
                   // tabLayout.setVisibility(View.VISIBLE);
                   // getSupportActionBar().setDisplayHomeAsUpEnabled(false); //hide the back button
                    pagerAdapter.notifyDataSetChanged();
                    //visibleFragment = new DiscoverFragment();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i(TAG,"onpagescrollstatechanged");

            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //Set the icons of the main tabs

        TextView tab_control = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab,null);
        tab_control.setText("Activity");
        tabLayout.getTabAt(0).setCustomView(tab_control);

//        TextView tab_map = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab,null);
//        tab_map.setText("Map");
//        tabLayout.getTabAt(1).setCustomView(tab_map);
//
        TextView tab_calories = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab,null);
        tab_calories.setText("Calories");
        tabLayout.getTabAt(1).setCustomView(tab_calories);


        db = new DatabaseHandler(getApplicationContext());
        profile = db.getProfile();

        if (profile != null){
            userHeight = Double.parseDouble(profile.get(0).getHeight());
            userWeight = Double.parseDouble(profile.get(0).getWeight());
            userGender = profile.get(0).getGender();
        } else{ //in case the profile table has not been created
            db.addProfileData(String.valueOf(userHeight),String.valueOf(userWeight),userGender);
        }



//        systemPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
//        systemPath = Environment.getFilesDir().getAbsolutePath() + "/";
        systemPath = getFilesDir().getAbsolutePath() + "/";
        appFolderPath = systemPath+"libsvm/";

        // 1. create necessary folder to save model files
        CreateAppFolderIfNeed();
        copyAssetsDataIfNeed();

        // 2. assign model/output paths
        //String dataTrainPath = appFolderPath+"fitnessapp_training ";
        //String dataPredictPath = appFolderPath+"fitnessapp_test  ";
        String modelPath = appFolderPath+"model ";
        //String outputPath = appFolderPath+"predict ";


    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.i(TAG,"onSAveInstance");

        outState.putSerializable("test",1);

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.i(TAG,"onRestoreInstance");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v,
//                                    ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_main, menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.exportdb) {
            //Toast.makeText(this, "Exporting DB..", Toast.LENGTH_SHORT).show();
            try {
                db.deleteNullRows(); //delete Activity null or Activity "Others"
                backupDatabase();
            } catch (Exception ex) {
                Log.i(TAG, ex.getMessage());
                Toast.makeText(this, "DB couldn't be exported", Toast.LENGTH_SHORT).show();
            }

            return true;
        }
// else if(id == R.id.inputHeigth) {
//            Toast.makeText(this, "input height", Toast.LENGTH_SHORT).show();
//            return  true;
//        }
        else if (id == R.id.action_settings){
            LayoutInflater inflater = getLayoutInflater();
            final View dialoglayout = inflater.inflate(R.layout.settings_menu, null);
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialoglayout);


            RadioGroup rgGender = (RadioGroup) dialoglayout.findViewById(R.id.rgGender);
            rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch(checkedId){
                        case R.id.rbMAle:
                            userGender = "male";
                            break;
                        case R.id.rbFemale:
                            userGender="female";
                            break;
                    }
                }
            });


            final EditText userheight = (EditText) dialoglayout.findViewById(R.id.etHeight);
            final EditText userweight = (EditText) dialoglayout.findViewById(R.id.etWeight);

//            final EditText wstride = (EditText) dialoglayout.findViewById(R.id.etWstride);
//            final EditText jstride = (EditText) dialoglayout.findViewById(R.id.etJstride);
//            final EditText bikecircumference = (EditText) dialoglayout.findViewById(R.id.etBikeWheel);


            userheight.setText(String.valueOf(userHeight));
            userweight.setText(String.valueOf(userWeight));
//            wstride.setText(String.valueOf(userWalkingStrideLength));
//            jstride.setText(String.valueOf(userJoggingStrideLength));
//            bikecircumference.setText(String.valueOf(userBikeWheelCircumference));

            RadioButton rbmale = (RadioButton) dialoglayout.findViewById(R.id.rbMAle);
            RadioButton rbfemale = (RadioButton) dialoglayout.findViewById(R.id.rbFemale);


            switch (userGender){
                case "male":
                    rbmale.setChecked(true);
                    rbfemale.setChecked(false);
                    break;
                case "female":
                    rbfemale.setChecked(true);
                    rbmale.setChecked(false);
                    break;
            }

            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String uHeight = userheight.getText().toString().trim();
                    String uWeight = userweight.getText().toString().trim();

                    if (uHeight.equals("")) uHeight = "0";
                    if (uWeight.equals("")) uWeight = "0";

                    userHeight = Double.parseDouble(uHeight);
                    userWeight = Double.parseDouble(uWeight);


                    if (userHeight < 1 || userWeight< 1 || userGender == ""){ //this should never happen
                        Toast.makeText(getApplicationContext(),"Please complete all the fields",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        db.updateProfile(String.valueOf(userHeight),String.valueOf(userWeight),userGender);
                        Toast.makeText(getApplicationContext(),"User profile saved",Toast.LENGTH_SHORT).show();
                    }

                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            final AlertDialog ad = builder.create();
            ad.show();

            //ad.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

            userheight.addTextChangedListener(new TextWatcher() {
                private void handleText() {
                    // Grab the button
                    final Button okButton = ad.getButton(AlertDialog.BUTTON_POSITIVE);
                    if(userheight.getText().length() == 0 || userweight.getText().length()==0) {
                        okButton.setEnabled(false);
                    } else {
                        okButton.setEnabled(true);
                    }
                }
                @Override
                public void afterTextChanged(Editable arg0) {
                    handleText();
                }
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Nothing to do
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Nothing to do
                }
            });


            userweight.addTextChangedListener(new TextWatcher() {
                private void handleText() {
                    // Grab the button
                    final Button okButton = ad.getButton(AlertDialog.BUTTON_POSITIVE);
                    if(userweight.getText().length() == 0 || userheight.getText().length() == 0) {
                        okButton.setEnabled(false);
                    } else {
                        okButton.setEnabled(true);
                    }
                }
                @Override
                public void afterTextChanged(Editable arg0) {
                    handleText();
                }
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Nothing to do
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Nothing to do
                }
            });



           // Button save = (Button)dialoglayout.findViewById(R.id.btnSave);
            //final Button cancel = (Button)dialoglayout.findViewById(R.id.btnCancel);


//            save.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String uHeight = userheight.getText().toString().trim();
//                    String uWeight = userweight.getText().toString().trim();
////                    String uwstride = wstride.getText().toString().trim();
////                    String ujstride = jstride.getText().toString().trim();
////                    String ubcircum = bikecircumference.getText().toString().trim();
//
//                    if (uHeight.equals("")) uHeight = "0";
//                    if (uWeight.equals("")) uWeight = "0";
////                    if (uwstride.equals(""))uwstride = "0";
////                    if (ujstride.equals(""))ujstride = "0";
////                    if (ubcircum.equals(""))ubcircum = "0";
//
//                    userHeight = Double.parseDouble(uHeight);
//                    userWeight = Double.parseDouble(uWeight);
//
////                    userWalkingStrideLength = Double.parseDouble(uwstride);
////                    userJoggingStrideLength = Double.parseDouble(ujstride);
////                    userBikeWheelCircumference = Double.parseDouble(ubcircum);
//
//                    if (userHeight < 1 || userWeight< 1 || userGender == ""){
//                        Toast.makeText(getApplicationContext(),"Please complete all the fields",Toast.LENGTH_SHORT).show();
//                    }
//                    else{
//                        Toast.makeText(getApplicationContext(),"User settings saved",Toast.LENGTH_SHORT).show();
//                        ad.cancel();
//                    }
//                }
//            });
//
//            cancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ad.cancel();
//                }
//            });


            //builder.show();

        } else if(id == R.id.energysavingmode) {
            // Launch the DeviceListActivity to see devices and do scan
            //Intent serverIntent = new Intent(this, DeviceListActivity.class);
            //startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);

            if (inpowersavingmode) {

                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Disable Power Saving Mode");
                alert.setMessage("With the power saving mode disabled, the activity recognition service " +
                        "(AR Service) will be running all the time. This might drain your battery at a faster rate");

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        inpowersavingmode = false;
                        editor.putString("powersavingmode","false");
                        editor.commit();

                        editor2.putString("powersavingmode","false");
                        editor2.commit();

                        ARService.powersavingmode = false;

                        ActivityFragment.tvPowerSavingMode.setText("Power Saving Mode Disabled");
                        ActivityFragment.tvPowerSavingMode.setTextColor(Color.RED);
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //ad.cancel();
                    }
                });


                final AlertDialog ad = alert.create();
                ad.show();

            } else {

                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Enable Power Saving Mode");
                alert.setMessage("With the power saving mode enabled, if the application does not detect any of the " +
                        "possible 5 activities: walking, jogging, cycling, and walking upstairs/downstairs during 2 minutes, the " +
                        "activity recognition service (AR service) will be paused for 5 minutes");

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        inpowersavingmode = true;
                        editor.putString("powersavingmode","true");
                        editor.commit();

                        editor2.putString("powersavingmode","true");
                        editor2.commit();

                        ARService.powersavingmode = true;

                        ActivityFragment.tvPowerSavingMode.setText("Power Saving Mode Enabled");
                        ActivityFragment.tvPowerSavingMode.setTextColor(Color.GREEN);
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //ad.cancel();
                    }
                });


                final AlertDialog ad = alert.create();
                ad.show();

            }





            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult");
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    ActivityFragment.connectDevice(data, true);
                }
                break;

        }
    }

    public void backupDatabase() throws IOException {
        //Open your local db as the input stream
        String inFileName = "/data/data/unimelb.steven.fitnessapp/databases/FitnessAppDB";
        File dbFile = new File(inFileName);
        final FileInputStream fis = new FileInputStream(dbFile);

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        final EditText edittext = new EditText(MainActivity.this);
        alert.setMessage("Export SQLite DB as:");
        alert.setView(edittext);

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String db_name = edittext.getText().toString();

                try{

                    Toast.makeText(MainActivity.this, "Exporting DB..", Toast.LENGTH_SHORT).show();

                    String outFileName = getFilesDir()+"/FitnessMateDB_"+db_name;
//                    String outFileName = Environment.getExternalStorageDirectory()+"/FitnessMateDB_"+db_name;
                    //Open the empty db as the output stream
                    OutputStream output = new FileOutputStream(outFileName);
                    //transfer bytes from the inputfile to the outputfile
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer))>0){
                        output.write(buffer, 0, length);
                    }
                    //Close the streams
                    output.flush();
                    output.close();
                    fis.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //ad.cancel();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //ad.cancel();
            }
        });


        final AlertDialog ad = alert.create();
        ad.show();

        //ad.show();
        //alert.show();



    }


    /**
     * commented due callGPS is used in ActivityFragment.java
     */

//    public void callGPS() {
//        GPSLocation gpsLocation = new GPSLocation();
//        gpsLocation.execute();
//    }
//
//    private void sendCoord(String latitude, String longitude) {
//        Intent intent = new Intent("my-state");
//        intent.putExtra("latitude", latitude);
//        intent.putExtra("longitude", longitude);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//    }
//
//    /**
//     * Handles the getting of gps coordinates from the mobile phone
//     * both providers (network and GPS) make the requestlocation
//     */
//    public class GPSLocation extends AsyncTask<String, Integer, String> {
//
//        ProgressDialog progDailog = null;
//
//        public int time = 0;
//        public boolean outOfTime = false;
//
//        public double lati = 0;
//        public double longi = 0;
//
//        public LocationManager mLocationManager;
//        public MyLocationListener mLocationListener;
//
//        String provider1,provider2;
//
//        @Override
//        protected void onPreExecute() {
//            mLocationListener = new MyLocationListener();
//            mLocationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
//
//            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                Utils.displayPromptForEnablingGPS(MainActivity.this);
//                GPSLocation.this.cancel(true);
//            }else{
//
//                ActivityFragment fragment_obj = (ActivityFragment)getSupportFragmentManager().
//                        findFragmentById(R.id.statusGPS);
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
//                            provider1, 1000, 0,
//                            mLocationListener);
//                }
//
//                mLocationManager.requestLocationUpdates(
//                        provider2, 1000, 0,
//                        mLocationListener);
//            }
//
//            progDailog = new ProgressDialog(MainActivity.this);
//            progDailog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                @Override
//                public void onCancel(DialogInterface dialog) {
//                    GPSLocation.this.cancel(true);
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
//            mLocationManager.removeUpdates(mLocationListener);
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
//                Toast.makeText(MainActivity.this, "GPS location: " + String.valueOf(userLatitude) + " "+ String.valueOf(userLongitude) , Toast.LENGTH_LONG).show();
//
//            }else {
//                Toast.makeText(MainActivity.this,
//                        "Please be located in an area of greater coverage or try again",
//                        Toast.LENGTH_LONG).show();
//
//            }
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
//                    sendCoord(String.valueOf(lati),String.valueOf(longi));
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

        /*
    * Some utility functions
    * */
    private void CreateAppFolderIfNeed(){
        // 1. create app folder if necessary
        File folder = new File(appFolderPath);

        if (!folder.exists()) {
            folder.mkdir();
            Log.d(TAG,"Appfolder is not existed, create one");
        } else {
            Log.w(TAG,"WARN: Appfolder has not been deleted");
        }


    }

    private void copyAssetsDataIfNeed(){
        String assetsToCopy[] = {"model"};
        //String targetPath[] = {C.systemPath+C.INPUT_FOLDER+C.INPUT_PREFIX+AudioConfigManager.inputConfigTrain+".wav", C.systemPath+C.INPUT_FOLDER+C.INPUT_PREFIX+AudioConfigManager.inputConfigPredict+".wav",C.systemPath+C.INPUT_FOLDER+"SomeoneLikeYouShort.mp3"};

        for(int i=0; i<assetsToCopy.length; i++){
            String from = assetsToCopy[i];
            String to = appFolderPath+from;

            // 1. check if file exist
            File file = new File(to);
            if(file.exists()){
                Log.d(TAG, "copyAssetsDataIfNeed: file exist, no need to copy:"+from);
            } else {
                // do copy
                boolean copyResult = copyAsset(getAssets(), from, to);
                Log.d(TAG, "copyAssetsDataIfNeed: copy result = "+copyResult+" of file = "+from);
            }
        }
    }

    private boolean copyAsset(AssetManager assetManager, String fromAssetPath, String toPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(fromAssetPath);
            new File(toPath).createNewFile();
            out = new FileOutputStream(toPath);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            Log.e(TAG, "[ERROR]: copyAsset: unable to copy file = "+fromAssetPath);
            return false;
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }


}
