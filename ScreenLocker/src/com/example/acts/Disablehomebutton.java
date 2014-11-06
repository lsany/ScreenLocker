package com.example.acts;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;

public class Disablehomebutton extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		HomeChoice homeChoice = new HomeChoice(this);
		startService(new Intent(Disablehomebutton.this, myService.class));
		try {
			homeChoice.originalHome();
		} catch (Exception e) {
			homeChoice.chooseBackHome();
		}
	}


	public class HomeChoice {
		Context context;
		Intent intent;

		SharedPreferences sharedPreferences;
		Editor editor;

		String packageName = "packageName";
		String activityName = "activityName";

		List<String> pkgNames, actNames;

		public HomeChoice(Context context) {
			this.context = context;
			intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			sharedPreferences = context.getSharedPreferences("homeChoice", MODE_PRIVATE);
			editor = sharedPreferences.edit();
		}

		public void chooseBackHome() {

			List<String> pkgNamesT = new ArrayList<String>();
			List<String> actNamesT = new ArrayList<String>();
			List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
			for (int i = 0; i < resolveInfos.size(); i++) {
				String string = resolveInfos.get(i).activityInfo.packageName;
				if (!string.equals(context.getPackageName())) {
					pkgNamesT.add(string);
					string = resolveInfos.get(i).activityInfo.name;
					actNamesT.add(string);
				}
			}


			String[] names = new String[pkgNamesT.size()];
			for (int i = 0; i < names.length; i++) {
				names[i] = pkgNamesT.get(i);
			}

			pkgNames = pkgNamesT;
			actNames = actNamesT;
			new AlertDialog.Builder(context).setTitle("«Î—°‘ÒΩ‚À¯∫Ûµƒ∆¡ƒª").setCancelable(false).setSingleChoiceItems(names, 0, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					editor.putString(packageName, pkgNames.get(which));
					editor.putString(activityName, actNames.get(which));
					editor.commit();
					originalHome();
					dialog.dismiss();
				}
			}).show();
		}

		public void originalHome() {
			String pkgName = sharedPreferences.getString(packageName, null);
			String actName = sharedPreferences.getString(activityName, null);
			ComponentName componentName = new ComponentName(pkgName, actName);
			Intent intent = new Intent();
			intent.setComponent(componentName);
			context.startActivity(intent);
			((Activity) context).finish();
		}

		public void chooseHome() {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
		}

		public void getPkgName() {
			ActivityInfo activityInfo = context.getPackageManager().resolveActivity(intent, 0).activityInfo;
			String pkgName = activityInfo.packageName;
			String actName = activityInfo.name;

			Log.e("", "ƒ¨»œ÷˜∆¡ƒªŒ™£∫" + pkgName + "/" + actName);

			if (pkgName.equals("android")) {
			} else if (pkgName.equals("coder.zhuoke")) {
			} else {
			}
		}

	}

}
