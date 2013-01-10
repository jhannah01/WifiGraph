package com.blueodin.wifigraphs;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class WifiLockService extends Service {
	public static final String SET_SCAN_INTERVAL = "SET_SCAN_INTERVAL";
	public static final int DEFAULT_SCAN_INTERVAL = 5*1000;
	private static final String TAG = "WifiLockService";
	private static final int NOTIFICATION_SERVICE_ID = 1;
	
	private WifiScanner wifiScanner;
    private int scanInterval = DEFAULT_SCAN_INTERVAL;
 
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Starting WifiLockService...");
        wifiScanner = new WifiScanner(this, scanInterval);
        showNotification();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Bundle bundle = intent.getExtras();
    	if(bundle != null) {
    		if(bundle.containsKey(SET_SCAN_INTERVAL))
    			this.scanInterval = bundle.getInt(SET_SCAN_INTERVAL);
    	}
    	
    	return super.onStartCommand(intent, flags, startId);
    }
 
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void showNotification() {
    	Context ctx = getApplicationContext(); 
    	NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(ctx);
    	notifBuilder
    		.setContentText(ctx.getText(R.string.notif_service_content))
    		.setContentTitle(ctx.getText(R.string.notif_service_title))
    		.setSmallIcon(R.drawable.ic_action_bars)
    		.setAutoCancel(false)
			.setWhen(System.currentTimeMillis());
    	
    	TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
    	stackBuilder.addParentStack(MainActivity.class);
    	
    	Intent resultIntent = new Intent(this, MainActivity.class);
    	resultIntent.putExtra(MainActivity.FLAG_FROM_NOTIFICATION, true);
    	stackBuilder.addNextIntent(resultIntent);
    	
    	notifBuilder.setContentIntent(stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT));
    	
    	NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(NOTIFICATION_SERVICE_ID, notifBuilder.build());
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Stopping WifiLockService...");
        wifiScanner.stop();
        wifiScanner = null;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_SERVICE_ID);
    }
}