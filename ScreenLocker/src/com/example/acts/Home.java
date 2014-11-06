package com.example.acts;


import com.example.screenlocker.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;  
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import android.os.Handler;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.graphics.Path;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.util.DisplayMetrics;
import android.util.Log;

import jp.epson.moverio.bt200.SensorControl;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class Home extends Activity implements SensorEventListener  {
	
	
	
	private RelativeLayout relativeLayout;
	private ImageView view;
	private Path path;
	private Paint paint;
	private Canvas canvas;
	private Dot[] array = new Dot[9];
	public static boolean[] inputpw= new boolean[9];//save the pw which was input
	private Dot lastDot;
	private Bitmap bitmap;
	private boolean drawing = false;
	private int radius = 0;
	
	private SensorManager mySensorManager=null;
	private Sensor mySensor=null;
	private SensorControl mySensorcontrol=null;
	
	private int flagback=0;
	private int di=1,dj=1;
	private Dot dot1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		//fullscreen in BT-200
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		winParams.flags |= 0x80000000; 
		win.setAttributes(winParams);
		
		setContentView(R.layout.home_layout);
		//Draw these dots
		relativeLayout = (RelativeLayout) findViewById(R.id.rela);
		view = (ImageView) findViewById(R.id.view);
		

		
		
		//Button just in case
		Button myButton1; 
		myButton1=(Button)findViewById(R.id.Click_Button); 
		myButton1.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				finish(); 
			}
		});
		mySensorcontrol = new SensorControl(this);
	    mySensorcontrol.setMode(SensorControl.SENSOR_MODE_HEADSET);
	    
		mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		drawDots();		
		
		startService(new Intent(Home.this, myService.class));//intent
	
	}
	
	public void unlock()
	{
        //password
		int i=0;
		while(i<9)
		{
			if(Main.arraypw[i]!=inputpw[i])	break;
			else i++;
		}
		if(i==9)
			{
				for(int c=0; c<9; c++)
				{
					inputpw[c]=false;
				}
				i=0;
				finish();
			}	
	}
	//*****Block the hard keys. For "soft home menu back  keys", maybe hiding them is better
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
		return true;
		}
		
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
		return true;
		}
		
		//home button is invalid
		/*switch (keyCode) {
		case KeyEvent.KEYCODE_HOME:
			startService(new Intent(Home.this, myService.class));
			//return true;
		}*/
		
		return super.onKeyDown(keyCode, event);
	}

	protected void onResume() {
		 super.onResume();
		 mySensor = mySensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		 mySensorManager.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_NORMAL);
		 }
	protected void onPause() {
		mySensorManager.unregisterListener(this);
	  	super.onPause();
	    }
	@Override
	public void onStop() {
	  	super.onStop();
		mySensorManager.unregisterListener(this);
		}
	

	private void drawDots() {
		int TopMars = (getScreenWidth() - getScreenHeight()) / 2;
		radius = getScreenHeight() / 12;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(radius * 2, radius * 2);
				params.leftMargin = (int) (2*TopMars +radius * 4 * (j + 0.25));
				params.topMargin = (int) ( radius * 4 * (i + 0.25));
				Dot dot = new Dot(this, radius);
				array[i * 3 + j] = dot;
				relativeLayout.addView(dot, params);
			}
		}
	}
	
	
	public int getScreenWidth() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);	
		return metrics.widthPixels;
		}

	public int getScreenHeight() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.heightPixels;
		}
	

	public int getWindowWidth() {
	return getWindow().findViewById(Window.ID_ANDROID_CONTENT).getWidth();
	}

	public int getWindowHeight() {
	return getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i){	  
	}
	  
	@Override
	public void onSensorChanged(SensorEvent event){
     
		float azi=event.values[0];
		float pit=event.values[1];
		//float rol=event.values[2];

		
		switch(flagback)
		{
			case 0:
				dot1=array[di+dj*3];
				drawfirstdot(dot1);
				checkinput(pit,azi);
				Log.e("di:"+di," Flagback:"+flagback);
				break;
			case 1:
				checkinput(pit,azi);
				break;
			case 2:

				dot1=array[di+dj*3];
				drawnextdot(dot1);
				checkinput(pit,azi);
				break;
			case 3:
				checkinput(pit,azi);
				break;
			case 4:
				dot1=array[di+dj*3];
				drawnextdot(dot1);
				checkinput(pit,azi);
				break;
			case 5:
				checkinput(pit,azi);
				break;
			case 6:
				dot1=array[di+dj*3];
				drawnextdot(dot1);
				checkinput(pit,azi);
				break;
			default:
				drawlastdot();
				break;
		}
		}	
		
	/*	
	private void checkinput(float pit,float azi)
	{
		if(flagi<4&&pit>0.6){
			flagi++;
			}
		if(flagi==4){
			if(flagback%2==0&&di>0)di--;
			flagback++;
			flagi=0;
			}
		
		if(flagi<4&&pit<-0.6){
			flagi++;
			}
		if(flagi==4){
			if(flagback%2==0&&di<=1)di++;
			flagback++;
			flagi=0;
			}
		
		if(flagj<4&&azi>0.4){
			flagj++;
			}
		if(flagj==4){
			if(flagback%2==0&&dj>0)dj--;
			flagback++;
			flagj=0;
			}
		
		if(flagj<4&&azi<-0.4){
			flagj++;
			}
		if(flagj==4){
			if(flagback%2==0&&dj<=1)dj++;
			flagback++;
			flagj=0;
			}
		
	}
	*/
	/*
	private void checkinput(float pit,float azi)
	{
		if(flagi<4&&pit>1.0){
			flagi++;
			}
		if(flagi==4){
			if(flagback%2==0&&di>0)di--;
			flagback++;
			flagi=0;
			}
		
		if(flagi<4&&pit<-1.0){
			flagi++;
			}
		if(flagi==4){
			if(flagback%2==0&&di<=1)di++;
			flagback++;
			flagi=0;
			}
		
		if(flagj<3&&azi>0.7){
			flagj++;
			}
		if(flagj==3){
			if(flagback%2==0&&dj>0)dj--;
			flagback++;
			flagj=0;
			}
		
		if(flagj<3&&azi<-0.7){
			flagj++;
			}
		if(flagj==3){
			if(flagback%2==0&&dj<=1)dj++;
			flagback++;
			flagj=0;
			}
		
	}*/
	private void checkinput(float pit,float azi)
	{
		if(pit>1.2){
			if(flagback%2==0&&di>0)di--;
			flagback++;
			}
		
		if(pit<-1.2){
			if(flagback%2==0&&di<=1)di++;
			flagback++;
			}
		
		if(azi>0.7){
			if(flagback%2==0&&dj>0)dj--;
			flagback++;
			}
		if(azi<-0.7){
			if(flagback%2==0&&dj<=1)dj++;
			flagback++;
			}
	}
	
	public void drawlastdot()
	{
		if (drawing) {
			clear();
			canvas.drawPath(path, paint);
			view.invalidate();
			drawing = false;
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					clearAllDrawing();
					dj=1;
					di=1;
					flagback=0;
				}
			}, 1000);
		}
	}
	public void drawnextdot(Dot dot)
	{
		if (drawing) {
						clear();
						lastDot = dot;
						lastDot.drawPassed();
						RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dot.getLayoutParams(); 
						path.lineTo(params.leftMargin + radius, params.topMargin+ radius);
						canvas.drawPath(path, paint);
						}
	}
	
	public void drawfirstdot(Dot dot)
	{
		bitmap = Bitmap.createBitmap(getWindowWidth(), getWindowHeight(), Config.ARGB_8888);
		canvas = new Canvas(bitmap);
		paint = new Paint();
		path = new Path();
		
		paint.setARGB(255, 0, 0, 255);
		paint.setStrokeWidth(8);
		paint.setStyle(Style.STROKE);
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dot.getLayoutParams();
		lastDot = dot;
		lastDot.drawPassed();                              
	
		path.moveTo(params.leftMargin+radius,params.topMargin+radius);
		view.setImageBitmap(bitmap);
		drawing = true;
	}

	protected void clearAllDrawing() {
		clear();
		for (int i = 0; i < array.length; i++) {
			Dot dot = array[i];
			if (dot != null) {
				inputpw[i]=dot.getPassed();
				dot.drawNormal();	
				}
			}
			drawing = false;
			
			unlock();//unlock after clear the screen!!!
			
	}
	

	protected void clear() {
		if (canvas != null && paint != null) {
			paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
			canvas.drawPaint(paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
			view.invalidate();
			}
	}
	protected class Dot extends ImageView {
		private int dotradius = 0;
		private boolean passed = false;
		public Dot(Context context) {
			super(context);
			}
		public Dot(Context context, int rad) {
			super(context);
			dotradius = rad;
			setLayoutParams(new LayoutParams(dotradius * 2, dotradius * 2));
			drawNormal();
			}

		public void drawNormal() {
			passed = false;
			Bitmap bm = Bitmap.createBitmap(dotradius * 2, dotradius * 2, Config.ARGB_8888);
			Paint paint = new Paint();
			Canvas canvas = new Canvas(bm);
			paint.setAntiAlias(true);
			paint.setARGB(255, 100, 100, 100); //dot normal
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(5);
			canvas.drawCircle(dotradius, dotradius, dotradius - paint.getStrokeWidth(), paint);
			paint.setStrokeWidth(1);
			paint.setStyle(Style.FILL_AND_STROKE);
			canvas.drawCircle(dotradius, dotradius, 3, paint);
			setImageBitmap(bm);
			}

		public void drawPassed() {
			passed = true;
			Bitmap bm = Bitmap.createBitmap(dotradius * 2, dotradius * 2, Config.ARGB_8888);
			Paint paint = new Paint();
			Canvas canvas = new Canvas(bm);
			paint.setAntiAlias(true);
			paint.setARGB(255, 156, 156, 156);  //dot passed
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(5);
			canvas.drawCircle(dotradius, dotradius, dotradius - paint.getStrokeWidth(), paint);
			paint.setStyle(Style.FILL_AND_STROKE);
			canvas.drawCircle(dotradius, dotradius, dotradius / 3, paint);
			setImageBitmap(bm);
			}
		public boolean getPassed() {
			return passed;
			}
		public void setPassed(boolean temp)
		{
			this.passed=temp;
		}
		}
}