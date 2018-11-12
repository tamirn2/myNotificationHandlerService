package com.tamir.mynotificationhandlerservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MyHandlerService extends NotificationListenerService {

    // constant
    public static final long NOTIFY_INTERVAL =  10 * 1000;
    private Timer mTimer = null;
    private Handler mHandler = new Handler();

    private String TAG = this.getClass().getSimpleName();
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.tamir.mynotificationhandlerservice.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
//        registerReceiver(mMessageReceiver,filter);

        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        registerReceiver(mMessageReceiver, new IntentFilter("end_time"));
        // schedule task
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2018);
        cal.set(Calendar.MONTH, 11);
        Date date = new Date(2018, 11, 12, 12, 42);
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 30000, NOTIFY_INTERVAL);
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // display toast

                    SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
                    String currentDateTime = dateTimeFormat.format(new Date()).toString();
                    String[] startSplit = currentDateTime.split(" ");
                    String CurrentTime = startSplit[1] + " " + startSplit[2].toUpperCase();

                    Intent intent = new Intent();
                    intent.putExtra("timer", "1");
                    Toast toast = Toast.makeText(getApplicationContext(),"this is a toast 4", Toast.LENGTH_SHORT);
                    toast.show();
                    sendBroadcast(intent);

                }

            });
        }

    }
    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            if (mTimer != null)
                mTimer.cancel();
            unregisterReceiver(mMessageReceiver);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        Log.i(TAG,"**********  onNotificationPosted");
        Log.i(TAG,"ID :" + sbn.getId() + "t" + sbn.getNotification().tickerText + "t" + sbn.getPackageName());
        Intent i = new  Intent("com.tamir.mynotificationhandlerservice.NOTIFICATION_LISTENER_EXAMPLE");
        i.putExtra("notification_event","onNotificationPosted :" + sbn.getPackageName() + "n");
        sendBroadcast(i);

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG,"********** onNOtificationRemoved");
        Log.i(TAG,"ID :" + sbn.getId() + "t" + sbn.getNotification().tickerText +"t" + sbn.getPackageName());
        Intent i = new  Intent("com.tamir.mynotificationhandlerservice.NOTIFICATION_LISTENER_EXAMPLE");
        i.putExtra("notification_event","onNotificationRemoved :" + sbn.getPackageName() + "n");

        sendBroadcast(i);
    }

    class NLServiceReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("command").equals("clearall")){
                MyHandlerService.this.cancelAllNotifications();
            }
            else if(intent.getStringExtra("command").equals("list")){
                Intent i1 = new  Intent("com.tamir.mynotificationhandlerservice.NOTIFICATION_LISTENER_EXAMPLE");
                i1.putExtra("notification_event","=====================");
                sendBroadcast(i1);
                int i=1;
                for (StatusBarNotification sbn : MyHandlerService.this.getActiveNotifications()) {
                    Intent i2 = new  Intent("com.tamir.mynotificationhandlerservice.NOTIFICATION_LISTENER_EXAMPLE");
                    i2.putExtra("notification_event",i +" " + sbn.getPackageName() + "n");
                    sendBroadcast(i2);
                    i++;
                }
                Intent i3 = new  Intent("com.tamir.mynotificationhandlerservice.NOTIFICATION_LISTENER_EXAMPLE");
                i3.putExtra("notification_event","===== Notification List ====");
                sendBroadcast(i3);

            }

        }
    }

}