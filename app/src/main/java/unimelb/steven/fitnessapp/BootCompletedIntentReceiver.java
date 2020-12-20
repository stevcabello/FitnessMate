package unimelb.steven.fitnessapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.util.Log;

import unimelb.steven.fitnessapp.services.ARService;
import unimelb.steven.fitnessapp.services.AllSensorsService;

public class BootCompletedIntentReceiver extends BroadcastReceiver {
    public static SharedPreferences.Editor editor;
    public static SharedPreferences prefs;
    public static SharedPreferences prefs2;
    public  static CountDownTimer sleepcountDownTimer;
    //String sprefs;

    public BootCompletedIntentReceiver() {

    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.


        if("sleepsystem".equals(intent.getAction())){

            //stop services
            context.stopService(new Intent(context, AllSensorsService.class));
            context.stopService(new Intent(context, ARService.class));

            //5 minutes of inactivity --> 300 seconds
            sleepcountDownTimer = new CountDownTimer(300*1000,1000) {

                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {

                    //start services

                    Intent pushIntent = new Intent(context, AllSensorsService.class);
                    context.startService(pushIntent);

                    Intent iAR = new Intent(context,ARService.class);
                    context.startService(iAR);
                }


            }.start();
        }


        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

            prefs =context.getSharedPreferences("mypref",Context.MODE_PRIVATE);
            prefs2 = context.getSharedPreferences("myprefpowersavingmode",Context.MODE_PRIVATE);

            String sprefs = prefs.getString("message",null);
            String sprefpowersavingmode = prefs2.getString("powersavingmode",null);

            if (sprefpowersavingmode==null) {
                Log.i("ACC_BCR","power saving mode is null");
                sprefpowersavingmode="false";
            }else{
                Log.i("ACC_BCR","power saving mode is not null");
            }


            if (sprefs!=null){
                Log.i("ACC_",sprefs);

                if (sprefs.equals("false")) {
                    // tvARService.setText("AR Service Stopped");
            //        MainActivity.ARServiceRunning = false;
//                    Intent iAR = new Intent(context,ARService.class);
//                    context.startService(iAR);
                }else{
                    // tvARService.setText("AR Service Started");
              //      MainActivity.ARServiceRunning = true;
                    Intent pushIntent = new Intent(context, AllSensorsService.class);
                    context.startService(pushIntent);

                    Intent iAR = new Intent(context,ARService.class);
                    Log.i("ACC_ARS",sprefpowersavingmode.toString());
                    //iAR.putExtra("inpowersavingmode", sprefpowersavingmode);
                    context.startService(iAR);

                }
            }else{
                Log.i("ACC_","sprefs is null");
            }



        }


        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
