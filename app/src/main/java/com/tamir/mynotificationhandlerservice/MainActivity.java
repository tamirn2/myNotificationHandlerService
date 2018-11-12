package com.tamir.mynotificationhandlerservice;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends Activity {

    static String CHANNEL_ID = "10";
    private TextView txtView;
    public NotificationManager notificationManager;
    public NotificationChannel mNotificationChannel;

    private void createChannel(Context context) {

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence channelName = "default Channel";
        int importance = NotificationManager.IMPORTANCE_LOW;
        this.mNotificationChannel = new NotificationChannel(CHANNEL_ID, channelName, importance);
        mNotificationChannel.enableLights(true);
        mNotificationChannel.setLightColor(Color.MAGENTA);
//        making sure the notification won't activate the vibration
        mNotificationChannel.enableVibration(false);
        notificationManager.createNotificationChannel(mNotificationChannel);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createChannel(this);
        txtView = (TextView) findViewById(R.id.textView);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.tamir.mynotificationhandlerservice.NOTIFICATION_LISTENER_EXAMPLE");
        Intent myIntent = new Intent(this, MyHandlerService.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 1);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.add(Calendar.DAY_OF_MONTH, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + 30000, 10000, pendingIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void buttonClicked(View v) {

        if (v.getId() == R.id.btnCreateNotify) {
            NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder ncomp = new NotificationCompat.Builder(this, mNotificationChannel.getId());
            ncomp.setContentTitle("My Notification");
            ncomp.setContentText("Notification Listener Service Example");
            ncomp.setTicker("Notification Listener Service Example");
            ncomp.setSmallIcon(R.drawable.ic_launcher_background);
            ncomp.setAutoCancel(true);
            nManager.notify((int) System.currentTimeMillis(), ncomp.build());
        } else if (v.getId() == R.id.btnClearNotify) {
            Intent i = new Intent("com.tamir.mynotificationhandlerservice.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
            i.putExtra("command", "clearall");
            sendBroadcast(i);
        } else if (v.getId() == R.id.btnListNotify) {
            Intent i = new Intent("com.tamir.mynotificationhandlerservice.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
            i.putExtra("command", "list");
            sendBroadcast(i);
        }

    }

    class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String temp = intent.getStringExtra("notification_event") + "n" + txtView.getText();
            txtView.setText(temp);
        }
    }

}