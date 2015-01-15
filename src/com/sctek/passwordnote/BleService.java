package com.sctek.passwordnote;

import java.util.List;
import java.util.UUID;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;

public class BleService extends Service implements 
							BluetoothAdapter.LeScanCallback{
	
	private final static String TAG = "BleService";
	
	private final static String Q_DEVICE_NAME = "Quintic BLE";
	
	private String newLineChar = System.getProperty("line.separator", "\n");
	
	private final static String SERVICE_UUID = "0000fee9-0000-1000-8000-00805f9b34fb";
	private final static String TX_CHARA_UUID = "D44BC439-ABFD-45A2-B575-925416129600";
	private final static String RX_CHARA_UUID = "D44BC439-ABFD-45A2-B575-925416129601";
	
	private UUID serviceUuid;
	private UUID txCharaUuid;
	private UUID rxCharaUuid;
	
	private final IBinder mBinder = new ServiceBinder();
	private Handler handler;
	private BluetoothAdapter btAdapter;
	private BluetoothGatt gatt;
	private List<BluetoothGattService> serviceList;
	private List<BluetoothGattCharacteristic> characterList;
	private BluetoothManager btManager;
	private ServiceManager serviceManager;
	
	private BleGattCallBack mGattCallBack;
	private BluetoothGatt mBluetoothGatt;
	private BluetoothGattService mGattService;
	private BluetoothGattCharacteristic mGattCharacteristic;
	private BluetoothDevice mBleDevice;
	
	private String url;
	private String userName;
	private String password;
	private int tabs;
	
	private String data;
	private byte[] dataBuff;
	private byte[] segBuff;
	
	private Short totalLength;
	private Short CountIndex;
	
	private int retryCount;
	
	private String bleAddress;
	
	private boolean scanning;
	private boolean findOne;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onCreate");
		serviceUuid = UUID.fromString(SERVICE_UUID);
		txCharaUuid = UUID.fromString(TX_CHARA_UUID);
		rxCharaUuid = UUID.fromString(RX_CHARA_UUID);
		
		btManager = (BluetoothManager)getSystemService(BLUETOOTH_SERVICE);
		btAdapter = btManager.getAdapter();
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onStartCommand");
		
		Bundle bundle = intent.getExtras();
		initDataBuff(bundle);
		
		mGattCallBack = new BleGattCallBack(url, userName, password, bleHandler);
		
		scanLeDevice();
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	public class ServiceBinder extends Binder{	
		BleService getService(){
			return BleService.this;
		}
	}
	
	public void attachHandler(Handler handler){
		this.handler = handler;
	}
	
	public void dettachHandler() {
		handler = null;
	}

	@Override
	public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
		// TODO Auto-generated method stub
		String deviceName = device.getName();
		try {
			Log.e(TAG, deviceName);
		} catch (NullPointerException e) {
			// TODO: handle exception
			Log.e(TAG, "deviceName is null");
		}
		
		if(!findOne&&deviceName !=null&&deviceName.equals(Q_DEVICE_NAME)) {
			
			btAdapter.stopLeScan(this);
			findOne = true;
			scanning = false;
				
			handler.sendEmptyMessage(R.id.device_find);
			mBleDevice = device;
			
			mBluetoothGatt = device.connectGatt(getApplicationContext(), false, mGattCallBack);
			
			bleAddress = device.getAddress();
			Log.e(TAG, device.getAddress() + " " + device.getName() + " " + rssi);
		}
	}
	
	public void scanLeDevice() {
		Log.e(TAG, "scanLeDevice");
		if(btAdapter.enable()) {
			
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(scanning) {
						btAdapter.stopLeScan(BleService.this);
						handler.sendEmptyMessage(R.id.scan_time_out);
						Log.e(TAG, "le scan stopped");
						scanning = false;
					}
				}
			}, 10000);
			findOne = false;
			scanning = true;
			btAdapter.startLeScan(this);
			
			handler.sendEmptyMessage(R.id.scanning);
		}
		else {
			handler.sendEmptyMessage(R.id.bt_disabled);
		}
	}
	
	Handler bleHandler = new Handler() {
		public void handleMessage(Message msg) {
			Log.e(TAG, "handlerMessage");
			switch (msg.what) {
			case R.id.find_service:
				handler.sendEmptyMessage(R.id.find_service);
				mGattService = mBluetoothGatt.getService(serviceUuid);
				mGattCharacteristic = mGattService.getCharacteristic(txCharaUuid);
				bleHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(R.id.waiting_to_senddata_timeout);
						mBluetoothGatt.disconnect();
						mGattCallBack.dettachHandler();
					}
				}, 10*1000);
				//startSendData();
				break;
			case R.id.data_received:
				
				retryCount = 0;
				bleHandler.removeCallbacksAndMessages(null);
				
				int offset = msg.arg1;
				if(offset == 0)
					handler.sendEmptyMessage(R.id.start_sending);
				
				if(CountIndex < totalLength) {
					Log.e(TAG, "CountIndex:" + CountIndex);
					segBuff = getNextSegment();
					bleHandler.postDelayed(mRunnable, 0);
				}
				else {
					handler.sendEmptyMessage(R.id.data_send_complete);	
					mBluetoothGatt.disconnect();
				}
				break;
			default:
				break;
			}
		}
	};
	
	Runnable mRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(retryCount++ < 3) {
				Log.e(TAG, "retry:" + retryCount);
				if(btManager.getConnectionState(mBleDevice, BluetoothProfile.GATT)
						== BluetoothProfile.STATE_CONNECTED) {
					Log.e(TAG, "resend");
					sendData(segBuff);
					bleHandler.postDelayed(mRunnable, 2000);
				}
				else if(!reConnect(bleAddress)) {
					Log.e(TAG, "reconnect fail");
					handler.sendEmptyMessage(R.id.reconnect_timeout);
					mBluetoothGatt.disconnect();
				}
			}
			else {
				handler.sendEmptyMessage(R.id.resend_countout);
				mBluetoothGatt.disconnect();
			}
		}
	};
	
	public void initDataBuff(Bundle bundle) {
		
		url = bundle.getString("url");
		userName = bundle.getString("username");
		password = bundle.getString("password");
		tabs = bundle.getInt("tabs");
		Log.e(TAG, url + " " + userName + " " + password + " " + tabs);
		StringBuffer buffer = new StringBuffer();
		buffer.append(url + newLineChar);
		for(int i = 0; i < tabs; i++)
			buffer.append('\t');
		buffer.append(userName + '\t');
		buffer.append(password);
		
		dataBuff = buffer.toString().getBytes();
//		url = url + '\n';
//		dataBuff = url.getBytes();
		totalLength = (short) dataBuff.length;
		CountIndex = 0;
		
		Log.e(TAG ,buffer.toString() + " " + totalLength);
	}

	public void startSendData() {
		Log.e(TAG, "StartDataSend");
		// TODO Auto-generated method stub
		segBuff = getNextSegment();
		retryCount = 0;
		sendData(segBuff);

		//bleHandler.postDelayed(mRunnable, 0);
	}
	
	protected boolean reConnect(String address) {
		Log.e(TAG, "reConnect");
//		if(false) {
//			if(mBluetoothGatt.connect()) {
//				Log.e(TAG, "reConnect OK");
//				mGattCallBack.onConnectionStateChange(mBluetoothGatt, 
//						BluetoothGatt.GATT_SUCCESS, BluetoothGatt.STATE_CONNECTED);
//				return true;
//			}
//			else 
//				return false;
//		}
//		else 
		if(address != null) {
			BluetoothDevice device = 
					btAdapter.getRemoteDevice(address);
			if(device != null) {
				mBleDevice = device;
				mBluetoothGatt = device.connectGatt(this, false, mGattCallBack);
				bleAddress = device.getAddress();
				return true;
			}
	}
	return false;
			
	}

	public void sendData( byte[] data) {
		mGattCharacteristic.setValue(data);
		mBluetoothGatt.writeCharacteristic(mGattCharacteristic);
	}
	
	public byte[] getNextSegment() {
		
		ByteArrayBuffer buff = new ByteArrayBuffer(20);
		
		byte checkByte;
		
		byte[] len = new byte[2];
		len[1] = (byte) (totalLength >> 8);
		len[0] = (byte) (totalLength >> 0);
		buff.append(len, 0, 2);
		
		checkByte = (byte) (len[0]^len[1]);
		
		len[1] = (byte) (CountIndex >> 8);
		len[0] = (byte) (CountIndex >> 0);
		
		checkByte = (byte) (len[0]^checkByte);
		checkByte = (byte) (len[1]^checkByte);
		
		buff.append(len, 0, 2);
		
		int bytesLeft = totalLength - CountIndex;
		for(int i = 0; i<15&&i<bytesLeft; i++) {
			checkByte ^= dataBuff[CountIndex + i];
		}
		buff.append(dataBuff, CountIndex, 
				bytesLeft >= 15?15:bytesLeft);
		CountIndex = (short)(bytesLeft >= 15?
				(CountIndex + 15):(CountIndex + bytesLeft));
		buff.append(new byte[]{checkByte}, 0, 1);
		Log.e(TAG, "" + buff.toByteArray().length);
		return buff.toByteArray();
	}
	
	public byte[] shortToBytes(short i) {
		byte[] value = new byte[2];
		value[1] = (byte) (i >> 8);
		value[0] = (byte) (i >> 0);
		return value;
	}
	
	public int unsignedBytesToInt(byte b0, byte b1) {
        return (unsignedByteToInt(b0) + (unsignedByteToInt(b1) << 8));
    }
	
	public int unsignedByteToInt(byte b) {
        return b & 0xFF;
    }

}
