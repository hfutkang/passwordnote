package com.sctek.passwordnote;

import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BleGattCallBack extends BluetoothGattCallback{
	
	private final static String TAG = "BleGattCallBack";
	
	private List<BluetoothGattService> serviceList;
	private List<BluetoothGattCharacteristic> characterList;
	
	private final static String SERVICE_UUID = "0000fee9-0000-1000-8000-00805f9b34fb";
	private final static String TX_CHARA_UUID = "d44bc439-abfd-45a2-b575-925416129600";
	private final static String RX_CHARA_UUID = "d44bc439-abfd-45a2-b575-925416129607";
	
	private String url;
	private String userName;
	private String password;
	
	private Handler handler;
	
	private UUID serviceUuid;
	private UUID txCharaUuid;
	private UUID rxCharaUuid;
	
	public BleGattCallBack(String u, String n, String p , Handler h) {
		
		url = u;
		userName = n;
		password = p;
		
		serviceUuid = UUID.fromString(SERVICE_UUID);
		txCharaUuid = UUID.fromString(TX_CHARA_UUID);
		rxCharaUuid = UUID.fromString(RX_CHARA_UUID);
		
		handler = h;
	}
	@Override
	public void onConnectionStateChange(BluetoothGatt gatt, int status,
			int newState) {
		Log.e(TAG, "onConnectionStateChange");
		switch (newState) {
		case BluetoothProfile.STATE_CONNECTED:
			Log.e(TAG, "STATE_CONNECTED");
			gatt.discoverServices();
			break;
		case BluetoothProfile.STATE_DISCONNECTED:
			Log.e(TAG, "STATE_DISCONNECTED");
			if(gatt != null)
				gatt.close();
			break;
		case BluetoothProfile.STATE_CONNECTING:
			Log.e(TAG, "STATE_CONNECTING");
			break;
		case BluetoothProfile.STATE_DISCONNECTING:
			Log.e(TAG, "STATE_DISCONNECTING");
			break;
		}
		super.onConnectionStateChange(gatt, status, newState);
	}

	@Override
	public void onServicesDiscovered(BluetoothGatt gatt, int status) {
		Log.e(TAG, "onServicesDiscovered");
		if (status == BluetoothGatt.GATT_SUCCESS) {
			serviceList = gatt.getServices();
			for (int i = 0; i < serviceList.size(); i++) {
				BluetoothGattService theService = serviceList.get(i);
				Log.e(TAG, "ServiceName:" + theService.getUuid());
				if(theService.getUuid().equals(serviceUuid)) {
					characterList = theService.getCharacteristics();
					for (int j = 0; j < characterList.size(); j++) {
						Log.e(TAG,
								"---CharacterName:"
										+ characterList.get(j).getUuid());
						BluetoothGattCharacteristic bleGattCharacteristic 
														= characterList.get(j);
						if(bleGattCharacteristic.getUuid().equals(txCharaUuid)) {
							if(handler != null)
								handler.sendEmptyMessage(R.id.find_service);
						}
						if(bleGattCharacteristic.getUuid().equals(rxCharaUuid)) {
							List<BluetoothGattDescriptor> descriptors = 
									bleGattCharacteristic.getDescriptors();
							BluetoothGattDescriptor mDescriptor = descriptors.get(0);
							mDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
							gatt.writeDescriptor(mDescriptor);
							gatt.setCharacteristicNotification(bleGattCharacteristic, true);
						}
					}
				}
			}
		}
		super.onServicesDiscovered(gatt, status);
	}

	@Override
	public void onCharacteristicRead(BluetoothGatt gatt,
			BluetoothGattCharacteristic characteristic, int status) {
		Log.e(TAG, "onCharacteristicRead");
		super.onCharacteristicRead(gatt, characteristic, status);
	}

	@Override
	public void onCharacteristicWrite(BluetoothGatt gatt,
			BluetoothGattCharacteristic characteristic, int status) {
		Log.e(TAG, "onCharacteristicWrite"+ "  status:" + status);
		super.onCharacteristicWrite(gatt, characteristic, status);
	}

	@Override
	public void onCharacteristicChanged(BluetoothGatt gatt,
			BluetoothGattCharacteristic characteristic) {
		Log.e(TAG, "onCharacteristicChanged" + 
				" " + characteristic.getUuid() + 
				" " + characteristic.getProperties());
		byte[] v = characteristic.getValue();
		int d = unsignedBytesToInt(v[0], v[1]);
		Log.e(TAG, "" + d + " " + v.length);
		if(handler != null) {
			Message msg = handler.obtainMessage(R.id.data_received, d, 0);
			msg.sendToTarget();
		}
		super.onCharacteristicChanged(gatt, characteristic);
	}

	@Override
	public void onDescriptorRead(BluetoothGatt gatt,
			BluetoothGattDescriptor descriptor, int status) {
		Log.e(TAG, "onDescriptorRead");
		super.onDescriptorRead(gatt, descriptor, status);
	}

	@Override
	public void onDescriptorWrite(BluetoothGatt gatt,
			BluetoothGattDescriptor descriptor, int status) {
		Log.e(TAG, "onDescriptorWrite");
		super.onDescriptorWrite(gatt, descriptor, status);
	}

	@Override
	public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
		Log.e(TAG, "onReliableWriteCompleted");
		super.onReliableWriteCompleted(gatt, status);
	}

	@Override
	public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
		Log.e(TAG, "onReadRemoteRssi");
		super.onReadRemoteRssi(gatt, rssi, status);
	}
	
	public int unsignedBytesToInt(byte b0, byte b1) {
        return (unsignedByteToInt(b0) + (unsignedByteToInt(b1) << 8));
    }
	
	public int unsignedByteToInt(byte b) {
        return b & 0xFF;
    }
	
	public void dettachHandler() {
		handler = null;
	}

}
