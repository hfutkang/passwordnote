package com.sctek.passwordnote;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class ServiceManager {
	private static ServiceManager instance = null;
	private BleService mService = null;
	private Context context = null;
	private Handler handler = null;
	private final String TAG = ServiceManager.class.getName();
	
	private ServiceConnection mConnection = new ServiceConnection() {
		
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.e(TAG, "ServiceManager onServiceConnected");
			mService = ((BleService.ServiceBinder) service).getService();
			if(handler != null)
				mService.attachHandler(handler);
		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
		}
		
	};	

	
	public ServiceManager(Context context) {
		this.context = context;
		instance = this;
	}
	
	public static ServiceManager getServiceManager() {
		return instance;
	}
	
	public void bindLocateService() {
		Log.e(TAG, "bindLocateService");
		context.bindService(
				new Intent(context,BleService.class), mConnection, Context.BIND_AUTO_CREATE);
	}
	
	public void unBindLocateService() {
		context.unbindService(mConnection);
	}
	
	public void attachHandler(Handler handler) {
		Log.e("ServiceManager", "attachHandler");
		if (mService != null)
			mService.attachHandler(handler);
		else
			this.handler = handler;
	}
	
	public void detachHandler() {
		this.handler = null;
		if (mService != null) 
			mService.dettachHandler();
	}
	
	BleService getService() {
		return mService;
	}
	
}
