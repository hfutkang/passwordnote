package com.sctek.passwordnote.ui;

import java.lang.reflect.Field;
import java.util.List;

import com.sctek.passwordnote.BleService;
import com.sctek.passwordnote.R;
import com.sctek.passwordnote.ServiceManager;
import com.sctek.passwordnote.R.id;
import com.sctek.passwordnote.R.layout;
import com.sctek.passwordnote.R.menu;
import com.sctek.passwordnote.Web;
import com.sctek.passwordnote.Web.AccountData;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.text.AlteredCharSequence;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private final static String TAG = "LoginActivity";
	private String url;
	private String userName;
	private String password;
	private int tabs;
	
	private EditText userNameEt;
	private EditText passwordEt;
	private TextView stateTv;
	private CheckBox checkBox;
	private AlertDialog sendingDataDialog;
	
	private BluetoothAdapter btAdapter;
	private BluetoothManager btManager;
	private ServiceManager serviceManager;
	
	private boolean sending;
	private boolean textChanged;
	private boolean newAccount;
	
	private Web mWeb;
	private AccountData accountData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ActionBar actionBar = this.getActionBar();
		if(actionBar != null) {
			actionBar.setCustomView(R.layout.title_bar);
			actionBar.setDisplayShowCustomEnabled(true);
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setDisplayShowTitleEnabled(false);
			
			actionBar.show();
		}
		
		serviceManager = new ServiceManager(this);
		serviceManager.bindLocateService();
		initView();
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
			
		super.onResume();
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onPause");
		sendingDataDialog = null;
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
			intent.putExtra("username", userName);
			intent.putExtra("password", password);
			intent.putExtra("tabs", tabs);
			startService(intent);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void onLoginButtonClicked(View v) {
		
		if(sending) {
			Toast.makeText(this,  R.string.sending_data, Toast.LENGTH_SHORT).show();
			return;
		}
		userName = userNameEt.getText().toString();
		password = passwordEt.getText().toString();
		
		if(userName.isEmpty()||password.isEmpty()) {
			Toast.makeText(this, R.string.illegal_account, Toast.LENGTH_SHORT).show();
			return;
		}
		if(textChanged) {
			textChanged = false;
			if(newAccount) {
				
				accountData = mWeb.new AccountData();
				accountData.name = userName;
				accountData.pw = password;
				accountData.url = url;
				mWeb.newAccount(accountData);
				newAccount = false;
			}
			else {
				
				String oldName = accountData.name;
				accountData.name = userName;
				accountData.pw = password;
				mWeb.updateAccount(accountData, oldName);
			}
		}
		
		if(btAdapter == null || !btAdapter.isEnabled()) {
			Log.e(TAG, "bluetooth disabled");
			Intent enableBtIntent = new 
					Intent(btAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 1);
		}
		else {
			
			sending = true;
			Intent intent = new Intent(this, BleService.class);
			intent.putExtra("url", url);
			intent.putExtra("username", userName);
			intent.putExtra("password", password);
			intent.putExtra("tabs", tabs);
			startService(intent);
			showSendingDataView();
		}
	}
	
	public void initView() {
		
		url = getIntent().getStringExtra("url");
		tabs = getIntent().getIntExtra("tabs", 0);
		userNameEt = (EditText)findViewById(R.id.user_name_et);
		passwordEt = (EditText)findViewById(R.id.password_et);
		checkBox = (CheckBox)findViewById(R.id.edit_cb);
		mWeb = Web.getInstance(this);
		sendingDataDialog = new AlertDialog.Builder(this).create();
		
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
					userNameEt.setEnabled(isChecked);
					passwordEt.setEnabled(isChecked);
			}
		});
		
		if((accountData = mWeb.getAccount(url)) == null) {
			
			checkBox.setVisibility(View.INVISIBLE);
			newAccount = true;
			textChanged = false;
			
			userNameEt.addTextChangedListener(textChangeWatcher);
			passwordEt.addTextChangedListener(textChangeWatcher);
			return;
		}
		
		newAccount = false;
		textChanged = false;
		userNameEt.setText(accountData.name);
		userNameEt.setEnabled(false);
		passwordEt.setText(accountData.pw);
		passwordEt.setEnabled(false);
		checkBox.setChecked(false);
		
		userNameEt.addTextChangedListener(textChangeWatcher);
		passwordEt.addTextChangedListener(textChangeWatcher);
		
	}
	
	private TextWatcher textChangeWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onTextChanged" + "start:" + start + "before:" + before + "count:" +  count);
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			Log.e(TAG, "afterTextChanged");
			textChanged = true;
		}
	};
	
	public void showSendingDataView() {
		
		sendingDataDialog.show();
		View view = getLayoutInflater().inflate(R.layout.sending_data_dialog, null);
		sendingDataDialog.setContentView(view);
		stateTv = (TextView)view.findViewById(R.id.state_tv_d);
		
		try {
			Field afield = sendingDataDialog.getClass().getSuperclass().getDeclaredField("mShowing");
			afield.setAccessible(true);  
			afield.set(sendingDataDialog, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
	}
	
	public void cancelWaitView(boolean ok) {
		
		try {
			Field afield = sendingDataDialog.getClass().getSuperclass().getDeclaredField("mShowing");
			afield.setAccessible(true);  
			afield.set(sendingDataDialog, true);
			
			sendingDataDialog.cancel();
			if(ok) {
				finish();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	Handler handler = new Handler() {
		
		public void handleMessage(Message msg) {
			Log.e(TAG, "handlerMessage");
			switch (msg.what) {
			case R.id.data_send_complete:
				sending = false;
				stateTv.setText(R.string.success);
				cancelWaitView(true);
				break;
			case R.id.reconnect_timeout:
				sending =false;
				stateTv.setText(R.string.fail_reconnect_timeout);
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						cancelWaitView(false);
					}
				}, 1000);
				break;
			case R.id.resend_countout:
				sending = false;
				stateTv.setText(R.string.fail_resend_countout);
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						cancelWaitView(false);
					}
				}, 1000);
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
						cancelWaitView(false);
					}
				}, 1000);
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
