package com.example.lhb.jt808;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.example.lhb.common.TPMSConsts;
import com.example.lhb.tool.Jt808Client;
import com.example.lhb.util.HexStringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Jt808Service extends IntentService {
   static Jt808Client client=null;


    public Jt808Service() {
        super("Jt808Service");
    }

//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        //throw new UnsupportedOperationException("Not yet implemented");
//    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public void onCreate() {
        new Thread()
        {
            public void run()
            {
                Jt808Service.client = new Jt808Client(TPMSConsts.ipaddr, TPMSConsts.port);
                Jt808Service.client.start();
            }
        }.start();
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }
    private void showNotification(String str)  {
        Intent puin;
        PendingIntent pi;
        Notification.Builder builder;
        Notification notification;
         NotificationManager mNotifMan  = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);;
                puin=new Intent(this, RegActivity.class);
                Log.i("Communication","to show notification");
                puin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pi = PendingIntent.getActivity(this, 0,
                        puin, PendingIntent.FLAG_UPDATE_CURRENT);
                builder = new Notification.Builder(this.getApplicationContext())
                        .setAutoCancel(true)
                        .setContentTitle("注册平台回应")
                        .setContentText(str)
                        .setContentIntent(pi)
                        .setWhen(System.currentTimeMillis())
                        .setOngoing(true);
                notification=builder.getNotification();
                notification.defaults=Notification.DEFAULT_SOUND;
                mNotifMan.notify(1, notification);
    }
    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle=msg.getData();
            String hexString= HexStringUtils.toHexString(bundle.getByteArray("data"));
            showNotification(hexString);
        }
    };
}
