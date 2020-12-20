package unimelb.steven.fitnessapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import unimelb.steven.fitnessapp.database.DatabaseHandler;
import unimelb.steven.fitnessapp.models.Accelerometer;
import unimelb.steven.fitnessapp.models.Sensors;

//import com.github.nkzawa.socketio.client.IO;
//import com.github.nkzawa.socketio.client.Socket;

public class SendDataService extends Service {
    DatabaseHandler db;
    private ArrayList<Accelerometer> accArr; //NOt being used
    private ArrayList<Sensors> senArr;
    //private Socket mSocket;
    {
//        try {
//            //mSocket = IO.socket("http://144.6.236.227:3000"); //Nectar
//             mSocket = IO.socket("http://10.12.92.235:3000"); //UniMelb
//            //mSocket = IO.socket("http://192.168.1.10:3000"); //Home
//        } catch (URISyntaxException e) {
//            Log.i("MainActitity", "couldnt connect");
//        }
    }

    public SendDataService() {

        Log.i("entro","entro");

        accArr = new ArrayList<Accelerometer>();

        db = new DatabaseHandler(this);
        //mSocket.connect();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                attemptSend("connected yei!");
            }
        }, 0, 20000);//put here time 1000 milliseconds=1 second
    }

    private void attemptSend(String message) {

        if (message.isEmpty()) {
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        Date originalDate = new Date();
        long diff = 20 * 1000; //20 seconds
        String strDate1 = sdf.format(originalDate);
        Log.i("MainActivity", "date original: " + strDate1);

        Date diffedDate = new Date(originalDate.getTime()-diff);
        String strDate = sdf.format(diffedDate);

        Log.i("MainActivity", "date restada:" + strDate);


        //accArr = db.getSensingData(strDate);
        senArr = db.getSensingData2(strDate,"");

        if (senArr !=null) {

            JSONArray sen_array = new JSONArray();

            for (int i = 0; i<senArr.size(); i++) {
                JSONObject sensors = new JSONObject();
                try {
//                    sensors.put("ax", senArr.get(i).getAx());
//                    sensors.put("ay", senArr.get(i).getAy());
//                    sensors.put("az", senArr.get(i).getAz());
                    sensors.put("gx", senArr.get(i).getGx());
                    sensors.put("gy", senArr.get(i).getGy());
                    sensors.put("gz", senArr.get(i).getGz());
//                    sensors.put("bp", senArr.get(i).getBp());
//                    sensors.put("tp", senArr.get(i).getTp());
//                    sensors.put("hm", senArr.get(i).getHm());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                sen_array.put(sensors);
            }

            JSONObject sen_obj = new JSONObject();
            try {
                sen_obj.put("sensors", sen_array);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Date testDate = new Date();
            String strtestDate = sdf.format(testDate);
            Log.i("tamanio del archivo", "datetime: " + strtestDate);
            Log.i("tamanio del archivo", "array size: " + String.valueOf(senArr.size()));
            Log.i("tamanio del archivo",String.valueOf(sen_obj.toString().getBytes().length));



           // mSocket.emit("new message", sen_obj);
        }

//        if (accArr !=null) {
//
//            JSONArray acc_array = new JSONArray();
//
//            for (int i = 0; i<accArr.size(); i++) {
//                JSONObject acc_axis = new JSONObject();
//                try {
//                    acc_axis.put("x", accArr.get(i).getX_axis());
//                    acc_axis.put("y", accArr.get(i).getY_axis());
//                    acc_axis.put("z", accArr.get(i).getZ_axis());
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                acc_array.put(acc_axis);
//            }
//
//            JSONObject acc_obj = new JSONObject();
//            try {
//                acc_obj.put("accelerometer", acc_array);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            Date testDate = new Date();
//            String strtestDate = sdf.format(testDate);
//            Log.i("tamanio del archivo", "datetime: " + strtestDate);
//            Log.i("tamanio del archivo", "array size: " + String.valueOf(accArr.size()));
//            Log.i("tamanio del archivo",String.valueOf(acc_obj.toString().getBytes().length));
//
//
//
//            mSocket.emit("new message", acc_obj.toString());
//        }




    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        //mSocket.disconnect();
    }
}
