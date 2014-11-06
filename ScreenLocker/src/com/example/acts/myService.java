
package com.example.acts;

//import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

//@SuppressWarnings("deprecation")
public class myService extends Service {
	private String TAG = "ScreenReceiver Log";
	Intent toHomeIntent;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
	@Override
	public void onCreate() {
		super.onCreate();

		toHomeIntent = new Intent(myService.this, Home.class);//Jump to home.class
		toHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
		intentFilter.addAction("android.intent.action.SCREEN_ON");
		registerReceiver(screenReceiver, intentFilter);
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		return Service.START_STICKY;//Never stop
	}
	
	//Start the service after Destory to make sure the service never stops.
	@Override
	
	public void onDestroy() 
	{
		super.onDestroy();
		unregisterReceiver(screenReceiver);
		startActivity(new Intent(myService.this,myService.class));
	}
	
	private BroadcastReceiver screenReceiver = new BroadcastReceiver() 
	{

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			String action = intent.getAction();
			Log.e(TAG, "intent.action = " + action);

			if (action.equals("android.intent.action.SCREEN_ON") || action.equals("android.intent.action.SCREEN_OFF")) 
			{
				startActivity(toHomeIntent);
			}
			
		}
	};
	


	
	

}
