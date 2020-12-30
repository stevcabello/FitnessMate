package unimelb.steven2.fitnessapp.services;

//import android.annotation.SuppressLint;
//import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.PowerManager;
//import android.support.v4.content.LocalBroadcastManager;
//import LocalBroadcastManager;
import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.util.FastMath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import unimelb.steven2.fitnessapp.MainActivity;
import unimelb.steven2.fitnessapp.database.DatabaseHandler;
import unimelb.steven2.fitnessapp.models.Accelerometer;
import unimelb.steven2.fitnessapp.models.Calories2;
import unimelb.steven2.fitnessapp.models.Sensors;

import static java.lang.Math.pow;
import static org.apache.commons.math3.util.Precision.round;

public class ARService extends Service {

    String appFolderPath;
    String systemPath;

    String dataPredictPath;
    String modelPath;
    String outputPath;


    double K_walking;
    double K_jogging;


    DatabaseHandler db;
    private ArrayList<Accelerometer> accArr; //NOt being used
    private ArrayList<Sensors> senArr;
    private ArrayList<Sensors> slided_data;

    private ArrayList<Double> ax_axis;
    private ArrayList<Double> ay_axis;
    private ArrayList<Double> az_axis;
    private ArrayList<Double> gx_axis;
    private ArrayList<Double> gy_axis;
    private ArrayList<Double> gz_axis;

    private String TAG = "myapp:ARService";

    private double mean_ax;
    private double mean_ay;
    private double mean_az;
    private double std_ax;
    private double std_ay;
    private double std_az;
    private double cor_axy;
    private double cor_axz;
    private double cor_ayz;

    private double mean_gx;
    private double mean_gy;
    private double mean_gz;
    private double std_gx;
    private double std_gy;
    private double std_gz;
    private double cor_gxy;
    private double cor_gxz;
    private double cor_gyz;

    private int inactive_cont=0;

    public double R=3.5; //mL.kg-1.min-1

    public double distanceWalking = 0;
    public double distanceJogging = 0;
    public double distanceCycling = 0;
    public double distanceUpstairs = 0;
    public double distanceDownstairs = 0;


    public static Boolean powersavingmode = false;

    SharedPreferences pref;

    PowerManager.WakeLock lock;


    // link jni library
    static {
        System.loadLibrary("jnilibsvm");
    }

    // connect the native functions
    private native void jniSvmTrain(String cmd);
    private native void jniSvmPredict(String cmd);

    Timer t;

//    @SuppressLint("InvalidWakeLockTag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        try {
//            String Spowersavingmode = intent.getExtras().getString("inpowersavingmode");
//
//            Log.i("ACC_ARSERvice89",Spowersavingmode);
//
//            if (Spowersavingmode.equals("true"))
//                powersavingmode = true;
//            else powersavingmode = false;
//        }catch (Exception e){
//            Log.i("ACC_ARSERvice","spowersavingmode false");
//            e.printStackTrace();
//        }
//
        Log.i("ACC_ARSERvice44",powersavingmode.toString());

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
//        lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ARService");
        lock.acquire();

        return START_STICKY;

    }


    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");


        Log.i(TAG,"entro");

//        systemPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        systemPath = getFilesDir().getAbsolutePath() + "/";
        appFolderPath = systemPath+"libsvm/";

        accArr = new ArrayList<Accelerometer>();


//        if (MainActivity.inpowersavingmode){
//            powersavingmode = true;
//        }else{
//            powersavingmode = false;
//        }

        Log.i("ACC_ARService2",powersavingmode.toString());

        try {
            pref = getSharedPreferences("myprefpowersavingmode", MODE_PRIVATE);
            String sprefs = pref.getString("powersavingmode", null);

            if (sprefs != null) {
                Log.i("ACC_ARS56", sprefs);

                if (sprefs.equals("false"))
                    powersavingmode = false;
                else
                    powersavingmode = true;
            } else {
                Log.i("ACC_ARS45", "sprefs is null");
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.i("ACC_ARS67","not working pref power saving mode");
        }

        ax_axis = new  ArrayList<Double> ();
        ay_axis = new  ArrayList<Double> ();
        az_axis = new  ArrayList<Double> ();
        gx_axis = new  ArrayList<Double> ();
        gy_axis = new  ArrayList<Double> ();
        gz_axis = new  ArrayList<Double> ();


        db = new DatabaseHandler(this);

        //Select K based on gender
        switch (MainActivity.userGender){
            case "male":
                K_walking = 0.415;
                K_jogging = 0.65;
                break;
            case "female":
                K_walking = 0.413;
                K_jogging = 0.55;
                break;
        }


        MainActivity.userWalkingStrideLength = MainActivity.userHeight*K_walking;
        MainActivity.userJoggingStrideLength = MainActivity.userHeight*K_jogging;

        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                attemptSend("connected yei!");
            }
        }, 0, 5000);//put here time 1000 milliseconds=1 second

    }

//    public ARService() {
//
//        Log.i(TAG,"entro");
//
//        systemPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
//        appFolderPath = systemPath+"libsvm/";
//
//        accArr = new ArrayList<Accelerometer>();
//
//
//        if (MainActivity.inpowersavingmode){
//            powersavingmode = true;
//        }else{
//            powersavingmode = false;
//        }
//
//        Log.i("ACC_ARService2",powersavingmode.toString());
//
////        try {
////            pref = getSharedPreferences("myprefpowersavingmode", MODE_PRIVATE);
////            String sprefs = pref.getString("powersavingmode", null);
////
////            if (sprefs != null) {
////                Log.i("ACC_ARS", sprefs);
////
////                if (sprefs.equals("false"))
////                    powersavingmode = false;
////                else
////                    powersavingmode = true;
////            } else {
////                Log.i("ACC_ARS", "sprefs is null");
////            }
////        }catch (Exception e){
////            e.printStackTrace();
////            Log.i("ACC_ARS","not working pref power saving mode");
////        }
//
//        ax_axis = new  ArrayList<Double> ();
//        ay_axis = new  ArrayList<Double> ();
//        az_axis = new  ArrayList<Double> ();
//        gx_axis = new  ArrayList<Double> ();
//        gy_axis = new  ArrayList<Double> ();
//        gz_axis = new  ArrayList<Double> ();
//
//
//        db = new DatabaseHandler(this);
//
//        //Select K based on gender
//        switch (MainActivity.userGender){
//            case "male":
//                K_walking = 0.415;
//                K_jogging = 0.65;
//                break;
//            case "female":
//                K_walking = 0.413;
//                K_jogging = 0.55;
//                break;
//        }
//
//
//        MainActivity.userWalkingStrideLength = MainActivity.userHeight*K_walking;
//        MainActivity.userJoggingStrideLength = MainActivity.userHeight*K_jogging;
//
//        t = new Timer();
//        t.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                attemptSend("connected yei!");
//            }
//        }, 0, 5000);//put here time 1000 milliseconds=1 second
//    }

//    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void attemptSend(String message) {

        if (message.isEmpty()) {
            return;
        }


        try{

            distanceWalking = 0;
            distanceJogging = 0;
            distanceCycling = 0;
            distanceUpstairs = 0;
            distanceDownstairs = 0;

            ax_axis.clear();
            ay_axis.clear();
            az_axis.clear();
            gx_axis.clear();
            gy_axis.clear();
            gz_axis.clear();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            Date originalDate = new Date();
            long diff = 5 * 1000; //5 seconds
            String strDate1 = sdf.format(originalDate);
            Log.i(TAG, "date original: " + strDate1);

            Date diffedDate = new Date(originalDate.getTime()-diff);
            String strDate = sdf.format(diffedDate);

            Log.i(TAG, "date restada:" + strDate);


            //accArr = db.getSensingData(strDate);
            senArr = db.getSensingData2(strDate,strDate1); //it will only return approximately 100 sensors rows

            ArrayList helpArr = new ArrayList<>();


            if (senArr !=null) {

                Log.i(TAG,String.valueOf(senArr.size()));

                String mm = "Inicio: " + senArr.get(0).getId() + " - Fin: " + senArr.get(senArr.size()-1).getId();

                Log.i(TAG,mm);

                for (int i=0; i<senArr.size(); i++){
                    ax_axis.add(senArr.get(i).getAx());
                    ay_axis.add(senArr.get(i).getAy());
                    az_axis.add(senArr.get(i).getAz());
                    gx_axis.add(senArr.get(i).getGx());
                    gy_axis.add(senArr.get(i).getGy());
                    gz_axis.add(senArr.get(i).getGz());
                }



                Double[] ax_axis_arr = new Double[ax_axis.size()];
                ax_axis_arr = ax_axis.toArray(ax_axis_arr);

                Double[] ay_axis_arr = new Double[ay_axis.size()];
                ay_axis_arr = ay_axis.toArray(ay_axis_arr);

                Double[] az_axis_arr = new Double[az_axis.size()];
                az_axis_arr = az_axis.toArray(az_axis_arr);

                Double[] gx_axis_arr = new Double[gx_axis.size()];
                gx_axis_arr = gx_axis.toArray(gx_axis_arr);

                Double[] gy_axis_arr = new Double[gy_axis.size()];
                gy_axis_arr = gy_axis.toArray(gy_axis_arr);

                Double[] gz_axis_arr = new Double[gz_axis.size()];
                gz_axis_arr = gz_axis.toArray(gz_axis_arr);




                mean_ax = StatUtils.mean(ArrayUtils.toPrimitive(ax_axis_arr));
                mean_ay = StatUtils.mean(ArrayUtils.toPrimitive(ay_axis_arr));
                mean_az = StatUtils.mean(ArrayUtils.toPrimitive(az_axis_arr));
                std_ax = FastMath.sqrt(StatUtils.variance(ArrayUtils.toPrimitive(ax_axis_arr)));
                std_ay = FastMath.sqrt(StatUtils.variance(ArrayUtils.toPrimitive(ay_axis_arr)));
                std_az = FastMath.sqrt(StatUtils.variance(ArrayUtils.toPrimitive(az_axis_arr)));
                cor_axy = new PearsonsCorrelation().correlation(ArrayUtils.toPrimitive(ax_axis_arr),
                        ArrayUtils.toPrimitive(ay_axis_arr));
                cor_axz = new PearsonsCorrelation().correlation(ArrayUtils.toPrimitive(ax_axis_arr),
                        ArrayUtils.toPrimitive(az_axis_arr));
                cor_ayz = new PearsonsCorrelation().correlation(ArrayUtils.toPrimitive(ay_axis_arr),
                        ArrayUtils.toPrimitive(az_axis_arr));

                mean_gx = StatUtils.mean(ArrayUtils.toPrimitive(gx_axis_arr));
                mean_gy = StatUtils.mean(ArrayUtils.toPrimitive(gy_axis_arr));
                mean_gz = StatUtils.mean(ArrayUtils.toPrimitive(gz_axis_arr));
                std_gx = FastMath.sqrt(StatUtils.variance(ArrayUtils.toPrimitive(gx_axis_arr)));
                std_gy = FastMath.sqrt(StatUtils.variance(ArrayUtils.toPrimitive(gy_axis_arr)));
                std_gz = FastMath.sqrt(StatUtils.variance(ArrayUtils.toPrimitive(gz_axis_arr)));
                cor_gxy = new PearsonsCorrelation().correlation(ArrayUtils.toPrimitive(gx_axis_arr),
                        ArrayUtils.toPrimitive(gy_axis_arr));
                cor_gxz = new PearsonsCorrelation().correlation(ArrayUtils.toPrimitive(gx_axis_arr),
                        ArrayUtils.toPrimitive(gz_axis_arr));
                cor_gyz = new PearsonsCorrelation().correlation(ArrayUtils.toPrimitive(gy_axis_arr),
                        ArrayUtils.toPrimitive(gz_axis_arr));

                String data = "20 1:"+String.valueOf(mean_ax)+" 2:"+String.valueOf(mean_ay)+" 3:"+String.valueOf(mean_az)+" 4:"+String.valueOf(std_ax)+" 5:"+
                        String.valueOf(std_ay)+" 6:"+String.valueOf(std_az)+" 7:"+String.valueOf(cor_axy)+" 8:"+String.valueOf(cor_axz)+" 9:"+
                        String.valueOf(cor_ayz)+" 10:"+String.valueOf(mean_gx)+" 11:"+String.valueOf(mean_gy)+" 12:"+String.valueOf(mean_gz)+" 13:"+
                        String.valueOf(std_gx)+" 14:"+String.valueOf(std_gy)+" 15:"+String.valueOf(std_gz)+" 16:"+String.valueOf(cor_gxy)+" 17:"+
                        String.valueOf(cor_gxz)+" 18:"+String.valueOf(cor_gyz);

                //Log.i(TAG,data);

                writeToFile(data,getApplicationContext());

                dataPredictPath = appFolderPath+"fitnessapp_test  ";
                modelPath = appFolderPath+"model ";
                outputPath = appFolderPath+"predict ";

                jniSvmPredict(dataPredictPath+modelPath+outputPath);


                String activity = ReadFromFile("predict");

                int steps = 0;
                if (activity.equals("Walking"))
                    steps = getStepCounts(senArr,MainActivity.walkingStepParams.split(",")[0],
                            Integer.parseInt(MainActivity.walkingStepParams.split(",")[1]),activity);
                else if (activity.equals("Jogging"))
                    steps = getStepCounts(senArr,MainActivity.joggingStepParams.split(",")[0],
                            Integer.parseInt(MainActivity.joggingStepParams.split(",")[1]),activity);
                else if (activity.equals("Cycling"))
                    steps = getStepCounts(senArr,MainActivity.cyclingStepParams.split(",")[0],
                            Integer.parseInt(MainActivity.cyclingStepParams.split(",")[1]),activity);
                else if (activity.equals("Upstairs"))
                    steps = getStepCounts(senArr,MainActivity.upstairsStepParams.split(",")[0],
                            Integer.parseInt(MainActivity.upstairsStepParams.split(",")[1]),activity);
                else if (activity.equals("Downstairs"))
                    steps = getStepCounts(senArr,MainActivity.downstairsStepParams.split(",")[0],
                            Integer.parseInt(MainActivity.downstairsStepParams.split(",")[1]),activity);

//            if (activity.equals("Walking"))
//                steps = getStepCounts(senArr,"gy",5,activity);
//            else if (activity.equals("Jogging"))
//                steps = getStepCounts(senArr,"gy",5,activity);
//            else if (activity.equals("Cycling"))
//                steps = getStepCounts(senArr,"gy",10,activity);
//            else if (activity.equals("Upstairs"))
//                steps = getStepCounts(senArr,"gz",1,activity);
//            else if (activity.equals("Downstairs"))
//                steps = getStepCounts(senArr,"gx",5,activity);


                //update the activity only if it is not "Others"
                if (!activity.equals("Others"))
                    db.updateActivity(activity,senArr.get(0).getId(),senArr.get(senArr.size()-1).getId());
                else //delete the rows that were classified as "Others"
                    db.deleteOthersRows(strDate,strDate1);



                //Caloric expenditure estimation

                double caloriesConsumed = 0;
                double distanceTraveled = 0;

                double height = MainActivity.userHeight;
                double weight = MainActivity.userWeight;


                double p1 = senArr.get(0).getBp();
                double p2 = senArr.get(senArr.size()-1).getBp();

                double diffelev = getAltitude(p2) - getAltitude(p1);


                switch (activity) {
                    case "Walking":
                        caloriesConsumed = EE_Walking(height,weight,steps,diffelev);
                        distanceTraveled = distanceWalking;
                        break;
                    case "Jogging":
                        caloriesConsumed = EE_Jogging(height,weight,steps,diffelev);
                        distanceTraveled = distanceJogging;
                        break;
                    case "Cycling":
                        caloriesConsumed = EE_Cycling(weight,steps,diffelev);
                        distanceTraveled = distanceCycling;
                        break;
                    case "Upstairs":
                        caloriesConsumed = EE_Upstairs(steps,weight);
                        distanceTraveled = distanceUpstairs;
                        break;
                    case "Downstairs":
                        caloriesConsumed = EE_Downstairs(steps,weight);
                        distanceTraveled = distanceDownstairs;
                        break;
                    default:
                        caloriesConsumed = 0;
                        distanceTraveled = 0;
                }

                //update the calories
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                String strDate2 = sdf2.format(diffedDate);
                Log.i(TAG,strDate2);

                //activity = "Walking";
                SimpleDateFormat sdf_ = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss.SSS");
                Date originalDate_ = new Date();
                String strDate1_ = sdf_.format(originalDate_);

                if (!activity.equals("Others")){

                    ArrayList<Calories2> calories = db.getCalories2(strDate1_);

                    if (calories == null) { //Create the 5 rows for this day in case they do not exists
                        db.addCaloriesData2(strDate1_, "Walking", "0", "0", "0");
                        db.addCaloriesData2(strDate1_, "Jogging", "0", "0", "0");
                        db.addCaloriesData2(strDate1_, "Cycling", "0", "0", "0");
                        db.addCaloriesData2(strDate1_, "Upstairs", "0", "0", "0");
                        db.addCaloriesData2(strDate1_, "Downstairs", "0", "0", "0");
                    }

                    ArrayList<Calories2> calories2s = db.getCaloriesConsumed(strDate2, activity);
                    //Log.i(TAG,String.valueOf(calories2s.size()));
                    double previousCalories = Double.parseDouble(calories2s.get(0).getCalories());
                    double previousSteps = Double.parseDouble(calories2s.get(0).getSteps());
                    double previousDistance = Double.parseDouble(calories2s.get(0).getDistance());

                    double newCalories = previousCalories + caloriesConsumed;
                    double newSteps = previousSteps + steps;
                    double newDistance = previousDistance + distanceTraveled;


                    db.updateCalories2(activity, strDate2, String.valueOf(round(newCalories,2)), String.valueOf(Math.round(newSteps)), String.valueOf(round(newDistance,2)));

//                    }else { //in case there already exist a record from this day
//                        db.updateCalories2(activity, strDate2, String.valueOf(round(newCalories,2)), String.valueOf(Math.round(newSteps)), String.valueOf(round(newDistance,2)));
//                    }


                    //sendMessage(strDate1.split(" ")[1] + " --> " + activity + " -- Steps: " + String.valueOf(Math.round(newSteps)) + " -- Calories: " + String.valueOf(round(newCalories,2))+" kcal");


                } else {

                    //sendMessage(strDate1.split(" ")[1] +" --> "+ activity);
                }



                Log.i("ACC_ARSErvice90",powersavingmode.toString());
                //energy String Spowersavingmode = getIntent();saving approach
                if (powersavingmode) {

                    if (activity.equals("Others"))
                        inactive_cont++;
                    else
                        inactive_cont = 0;

                    if (inactive_cont == 24) {//two minute inactive 24*5 => 120 seconds => 2min
                        inactive_cont = 0;
                        t.cancel();
                        sendSleepMessage(activity); //the activity isnt necessary

                    }
                }




            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.i("AAC_ARS_Destroyed","true");
        lock.release();
        t.cancel();

    }

    // Send an Intent with an action named "my-event".
    private void sendMessage(String activity) {
        Intent intent = new Intent("activityfragment");
        // add data
        intent.putExtra("prediction", activity);

        //This was commented out on 24/Dec/2020
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//        sendBroadcast(intent);
    }

    // Send an Intent with an action named "my-event".
    private void sendSleepMessage(String activity) {
        Log.i("ACC_AR","sendSleepMessage");
//        Intent intent = new Intent("activityfragment");
//        intent.putExtra("sleep", activity);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        Intent intent = new Intent("sleepsystem");
        intent.putExtra("sleep", activity);
        sendBroadcast(intent);
        //LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void writeToFile(String data,Context context) {
        try {
            File file = new File(appFolderPath,"fitnessapp_test");
            FileOutputStream outputStream = new FileOutputStream(file);

            PrintWriter pw = new PrintWriter(outputStream);
            pw.print(data);
            pw.close();
            outputStream.close();

        }
        catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }
    }


    private String ReadFromFile(String filename) {
        String activity = "";
        try {
            File file = new File(appFolderPath,filename);

            int length = (int)file.length();
            byte[] bytes = new byte[length];

            FileInputStream in = new FileInputStream(file);
            try {
                in.read(bytes);
            } finally {
                in.close();
            }

            activity = new String(bytes);

            activity = activity.trim();

            switch (activity){
                case "0":
                    activity = "Walking";
                    break;
                case "1":
                    activity = "Jogging";
                    break;
                case "2":
                    activity = "Cycling";
                    break;
                case "3":
                    activity = "Upstairs";
                    break;
                case "4":
                    activity = "Downstairs";
                    break;
                default:
                    activity = "Others";
            }

            //return activity;

        }
        catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }

        return activity;
    }



    public int getStepCounts(ArrayList<Sensors> data, String axis, int w_size, String activity){

        ArrayList<Double> gx_axis_sc = new ArrayList<>();
        ArrayList<Double> gy_axis_sc = new ArrayList<>();
        ArrayList<Double> gz_axis_sc = new ArrayList<>();
        ArrayList<Double> seconds = new ArrayList<>();

        ArrayList data_axis = new ArrayList();
        ArrayList<Double> seconds_data = new ArrayList();

        double mean_gx = 0;
        double mean_gy = 0;
        double mean_gz = 0;
        double mean_seconds = 0;

        ArrayList<Sensors> slided_data = new ArrayList();

        for (int i=0; i<data.size()-w_size; i++){
            slided_data.addAll(data.subList(i,i+w_size));



            for (int j=0; j<slided_data.size();j++){
                gx_axis_sc.add(slided_data.get(j).getGx());
                gy_axis_sc.add(slided_data.get(j).getGy());
                gz_axis_sc.add(slided_data.get(j).getGz());
                seconds.add(slided_data.get(j).getSeconds());
            }

            Double[] gx_axis_arr = new Double[gx_axis_sc.size()];
            gx_axis_arr = gx_axis_sc.toArray(gx_axis_arr);

            Double[] gy_axis_arr = new Double[gy_axis_sc.size()];
            gy_axis_arr = gy_axis_sc.toArray(gy_axis_arr);

            Double[] gz_axis_arr = new Double[gz_axis_sc.size()];
            gz_axis_arr = gz_axis_sc.toArray(gz_axis_arr);

            Double[] seconds_arr = new Double[seconds.size()];
            seconds_arr = seconds.toArray(seconds_arr);

            mean_gx = StatUtils.mean(ArrayUtils.toPrimitive(gx_axis_arr));
            mean_gy = StatUtils.mean(ArrayUtils.toPrimitive(gy_axis_arr));
            mean_gz = StatUtils.mean(ArrayUtils.toPrimitive(gz_axis_arr));
            mean_seconds = StatUtils.mean(ArrayUtils.toPrimitive(seconds_arr));


            switch (axis){
                case "gx":
                    data_axis.add(mean_gx);
                    break;
                case "gy":
                    data_axis.add(mean_gy);
                    break;
                default:
                    data_axis.add(mean_gz);
            }

            seconds_data.add(mean_seconds);

            gx_axis_sc.clear();
            gy_axis_sc.clear();
            gz_axis_sc.clear();
            seconds.clear();

            slided_data.clear();


        }


        int steps=0;

        if (data_axis.size() > 0) {

            if (activity.equals("Jogging")) {
                steps = CountSteps(data_axis, seconds_data, 0.0);
            } else {
                steps = CountSteps(data_axis, seconds_data, 0.5); //0.5 min time threshold to minimize false zero crossings
            }
        }

        return steps;
    }


    public int CountSteps(ArrayList data, ArrayList<Double> seconds_data, double mintime){

        int current_index = 0;
        int next_index = 0;
        double time_difference= 0;
        int nsteps = 0;

        ArrayList<Integer> zerocrossings = new ArrayList<>();

        zerocrossings.addAll(getZeroCrossings(data));

        ArrayList<Integer> real_zerocrossings = new ArrayList<>();
        real_zerocrossings.add(zerocrossings.get(0));

        for (int i=0; i<zerocrossings.size()-1; i++){
            current_index = zerocrossings.get(i) + 1;
            next_index = zerocrossings.get(i+1) + 1;
            time_difference = seconds_data.get(next_index) - seconds_data.get(current_index);
            if (time_difference > mintime)
                real_zerocrossings.add(zerocrossings.get(i+1));
        }

        nsteps = real_zerocrossings.size() * 2;

        return nsteps;
    }




    public ArrayList<Integer> getZeroCrossings(ArrayList<Double> data){

        double v = 0;


        //np.sign
        ArrayList<Double> sign_arr = new ArrayList<>();
        for(int i=0; i<data.size();i++){
            v = changeSign(data.get(i));
            sign_arr.add(v);
        }


        //np.diff
        ArrayList<Double> diff_arr = new ArrayList<>();
        for(int i=0; i<sign_arr.size()-1;i++){
            v = sign_arr.get(i+1) - sign_arr.get(i);
            diff_arr.add(v);
        }

        //np.where get indices of diff_arr where values are > 0
        ArrayList<Integer> indices_arr = new ArrayList<>();
        for(int i=0; i<diff_arr.size();i++){
            if (diff_arr.get(i)>0)
                indices_arr.add(i);
        }

        return indices_arr;
    }



    //np.sign
    public double changeSign(double value){
        if (value>0)
            value = 1;
        else if (value <0)
            value = -1;
        else
            value = 0;

        return value;
    }


    private double EE_Walking(double height,  double weight, int steps, double diffElev){

        //double K_walking = 0.415; //for women 0.413
        //double strideLen = height*K_walking; // in cm

        double strideLen = MainActivity.userWalkingStrideLength;

        distanceWalking = steps * (strideLen/(float)100); //in meters

        double speed = distanceWalking/(float)5; //in m/seg --> 5 seconds samples
        speed = speed * 60; // m/min

        double grade =0;

        if (diffElev>0)
            grade = diffElev/(float)distanceWalking;


        double timeinMin = 5/(float)60; //5 seconds to minutes

        double h = getH_walking(speed);
        double v = getV_walking(speed,grade);
        double vo2max = getVO2max(R,h,v,weight);
        double calories = getCalories(vo2max,timeinMin);

        return calories;
    }

    private double getH_walking(double speed){
        return 0.1*speed;
    }

    private double getV_walking(double speed, double grade){
        return 1.8*speed*grade;
    }

    private double getH_jogging(double speed){
        return 0.2*speed;
    }

    private double getV_jogging(double speed, double grade){
        return 0.9*speed*grade;
    }

    private double getH_cycling(){
        return 3.5; // in mL.kg-1.min-1
    }


    private double getV_cycling(double workrate, double weight){
        //workrate in kn.m/min
        return (1.8*workrate)/(float)weight; // in mL.kg-1.min-1
    }

    private double getH_upstairs(double steps,double timeinMin){
        double stepsmin = steps/(float)timeinMin;
        return 0.2*stepsmin;
    }

    private double getV_upstairs(double steps, double stepHeight, double timeinMin){
        double stepsmin = steps/(float)timeinMin;
        return 1.33*(1.8*stepHeight*stepsmin);
    }


    private double getWorkRate(double weight, double speed, double grade){

        //speed must come in m/s

        double workrate;

        double m_bike = 8.6; //kg....19 pounds(average bike mass)
        double m = m_bike + weight;
        double g = 9.8;
        double Cr = 0.61;
        double s = grade;
        double p = 1.0; //kg/m3
        double Ca = 0.26;
        double A = 0.4; // m2  http://www.cyclingpowerlab.com/CyclingAerodynamics.aspx
        double Va;
        double Vw = 0; //wind velocity ..not considered

        Va = speed + Vw;

        double Fr = m*g*Cr; //rolling resistance of bike .. in  Kg.m.s-2 --> N
        double Fg = m*g*s; // component of gravity .. in Kg.m.s-2 --> N
        double Fa = 0.5*p*Ca*pow(Va,2)*A; // force of aerodynamic drag kg.m.s-2 --> N

        double R = Fr + Fg + Fa; // kg.m/s2 -> Newton

        workrate = R * speed; //speed must be in m/s --> N.m.s-1 --> Watts

        return workrate; //in Watts

    }

    private double getVO2max(double R, double H, double V, double W){
        return (R+H+V)*W/(float)1000; //
    }

    private double getCalories(double VO2max,double timeinMin){
        return VO2max*5.01*timeinMin; //kcal
    }

    private double EE_Jogging(double height,  double weight, int steps, double diffElev){
        //double K_jogging = 0.65;
        //double strideLen = height*K_jogging; // in cm

        double strideLen = MainActivity.userJoggingStrideLength;

        distanceJogging = steps * (strideLen/(float)100); //in meters

        double speed = (float)distanceJogging/5; //in m/seg --> 5 seconds samples
        speed = speed * 60; // m/min

        double grade =0;

        if (diffElev>0)
            grade = (float)diffElev/distanceJogging;


        double timeinMin = 5/(float)60; //5 seconds to minutes

        double h = getH_jogging(speed);
        double v = getV_jogging(speed,grade);
        double vo2max = getVO2max(R,h,v,weight);
        double calories = getCalories(vo2max,timeinMin);

        return calories;
    }

    private double EE_Cycling(double weight, int steps, double diffElev){

        double distanceperrev = MainActivity.userBikeWheelCircumference; // m/rev
        double revpersecond = steps/(float)5; // rev/second


        distanceCycling = steps*distanceperrev; // in m

        double grade =0;

        if (diffElev>0)
            grade = diffElev/(float)distanceCycling;


        double speed = distanceperrev * revpersecond; // in m/seconds

        double workrate = getWorkRate(weight, speed, grade);

        double timeinMin = 5/(float)60; //5 seconds to minutes

        double h = getH_cycling();
        double v = getV_cycling(workrate,weight);
        double vo2max = getVO2max(R,h,v,weight);
        double calories = getCalories(vo2max,timeinMin);

        return calories;
    }

    private double EE_Upstairs(double steps,double weight){

        double timeinMin = 5/(float)60;
        double stepHeigth = 0.20; // 20cm

        distanceUpstairs = stepHeigth*steps;

        double h = getH_upstairs(steps,timeinMin);
        double v = getV_upstairs(steps,stepHeigth,timeinMin);
        double vo2max = getVO2max(R,h,v,weight);
        double calories = getCalories(vo2max,timeinMin);

        return calories;
    }

    private double EE_Downstairs(double steps, double weight){

        double timeinMin = 5/(float)60;
        double stepHeigth = 0.20; // 20cm

        distanceDownstairs = stepHeigth*steps;

        double h = getH_upstairs(steps,timeinMin);
        double v = 0; //no considering resistance component
        double vo2max = getVO2max(R,h,v,weight);
        double calories = getCalories(vo2max,timeinMin);

        return calories;
    }

    private double getAltitude(double p){
        double po = 1013.25; //hpa
        double exp = 1/(float)5.255;
        double relation = p/(float)po;
        double var = pow(relation,exp);
        int constant = 44330;

        return constant * (1-var);
    }


}
