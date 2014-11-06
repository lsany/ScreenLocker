/*launch the App after boot*/

package com.example.acts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
			Intent newIntent = new Intent(context, Home.class); 
		    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		    context.startActivity(newIntent);       

		
	}
}
