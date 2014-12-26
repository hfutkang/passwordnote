package com.sctek.passwordnote.ui;

import java.util.List;
import java.util.zip.Inflater;

import com.sctek.passwordnote.BleService;
import com.sctek.passwordnote.R;
import com.sctek.passwordnote.ServiceManager;
import com.sctek.passwordnote.R.id;
import com.sctek.passwordnote.R.layout;
import com.sctek.passwordnote.R.string;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity1 extends Activity {
	
	private final static String TAG = "MainActivity";
	private String url;
	private String userName;
	private String password;
	
	private EditText urlEt;
	private EditText userNameEt;
	private EditText passwordEt;
	private TextView stateTv;
	
	private BluetoothAdapter btAdapter;
	private BluetoothGatt gatt;
	private List<BluetoothGattService> serviceList;
	private List<BluetoothGattCharacteristic> characterList;
	private BluetoothManager btManager;
	private ServiceManager serviceManager;
	
	private boolean sending;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ActionBar actionBar = this.getActionBar();
		if(actionBar != null) {
			actionBar.setCustomView(R.layout.title_bar);
			actionBar.setDisplayShowCustomEnabled(true);
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setDisplayShowTitleEnabled(false);
			
			actionBar.show();
		}
		urlEt = (EditText)findViewById(R.id.url_et);
		stateTv = (TextView)findViewById(R.id.state_tv);
//		userNameEt = (EditText)findViewById(R.id.user_name_et);
//		passwordEt = (EditText)findViewById(R.id.password_et);
		
		serviceManager = new ServiceManager(this);
		serviceManager.bindLocateService();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		btAdapter = btManager.getAdapter();
		sending = false;
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				serviceManager.attachHandler(handler);
				
			}
		}, 0);
		
		urlEt.setFilters(new InputFilter[]{new InputFilter() {
			
			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				// TODO Auto-generated method stub
				StringBuffer sb = new StringBuffer();
				for(int i = start; i < end; i++) {
					char c = source.charAt(i);
					if(c < '\u4e00' || c > '\u9fa5')
						sb.append(c);
				}
				return sb.toString();
			}
		}});		
		super.onResume();
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onPause");
		serviceManager.detachHandler();
		handler.removeCallbacks(null);
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		serviceManager.unBindLocateService();
		super.onDestroy();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode ==1 && resultCode == RESULT_OK) {
			Intent intent = new Intent(this, BleService.class);
			intent.putExtra("url", url);
//			intent.putExtra("username", userName);
//			intent.putExtra("password", password);
			startService(intent);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void onSendButtonClicked(View v) {
		url = urlEt.getText().toString();
		if(!sending) {
			if(!url.isEmpty()) {
		//		userName = userNameEt.getText().toString();
		//		password = passwordEt.getText().toString();
				if(btAdapter == null || !btAdapter.isEnabled()) {
					Log.e(TAG, "bluetooth disabled");
					Intent enableBtIntent = new 
							Intent(btAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, 1);
				}
				else {
					handler.removeCallbacks(null);
					sending = true;
					Intent intent = new Intent(this, BleService.class);
					intent.putExtra("url", url);
		//			intent.putExtra("username", userName);
		//			intent.putExtra("password", password);
					startService(intent);
				}
			}
			else
				Toast.makeText(this, R.string.data_is_empty, Toast.LENGTH_SHORT).show();
		} else
			Toast.makeText(this,  R.string.sending_data, Toast.LENGTH_SHORT).show();
	}
	
	Handler handler = new Handler() {
		
		public void handleMessage(Message msg) {
			Log.e(TAG, "handlerMessage");
			switch (msg.what) {
			case R.id.data_send_complete:
				sending = false;
				stateTv.setText(R.string.success);
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						stateTv.setText("");
					}
				}, 5000);
				break;
			case R.id.reconnect_timeout:
				sending =false;
				stateTv.setText(R.string.fail_reconnect_timeout);
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						stateTv.setText("");
					}
				}, 5000);
				break;
			case R.id.resend_countout:
				sending = false;
				stateTv.setText(R.string.fail_resend_countout);
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						stateTv.setText("");
					}
				}, 5000);
				break;
			case R.id.bt_disabled:
				Intent enableBtIntent = new 
				Intent(btAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, 1);
				break;
			case R.id.scan_time_out:
				sending = false;
				stateTv.setText(R.string.device_scan_timeout);
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						stateTv.setText("");
					}
				}, 5000);
				break;
			case R.id.device_find:
				stateTv.setText(R.string.find_device);
				break;
			case R.id.find_service:
				stateTv.setText(R.string.service_find);
				break;
			case R.id.start_sending:
				stateTv.setText(R.string.sending);
				break;
			case R.id.scanning:
				stateTv.setText(R.string.scanning_device);
				break;
			default:
					break;
			}
		}
	};
}
