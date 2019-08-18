package ir.samadi.sadra.robotooth;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BluetoothClass extends Thread {

	private final BluetoothAdapter btAdapter;
	private BluetoothSocket mSocket = null;
	private ConnectedThread connectedThread;
	private ConnectThread connectThread;
	protected final IntentFilter mfilter;
	protected final Intent enableBtIntent;
	protected final BroadcastReceiver mReceiver;

	public BluetoothClass() {
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

		mfilter = new IntentFilter();
		mfilter.addAction(BluetoothDevice.ACTION_FOUND);
		mfilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		mfilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals(BluetoothDevice.ACTION_FOUND)) {
					DevicesListActivity
							.onFound((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
				} else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
					DevicesListActivity.onDiscoveryStart();
				} else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
					DevicesListActivity.onDiscoveryFinish();
				}
			}
		};
	}

	protected void connect(BluetoothDevice bluetoothDevice) {
		connectThread = new ConnectThread(bluetoothDevice);
		connectThread.run();
		if (isConnected()) {
			connectedThread = new ConnectedThread(mSocket);
			MainActivity.showToast("وصل شد !");
		} else {
			MainActivity.showToast("وصل نشد !");
		}
	}

	protected void enableBt(Activity activity) {
		activity.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
	}

	protected Set<BluetoothDevice> getPairedDevices() {
		return btAdapter.getBondedDevices();
	}

	protected void disableBt() {
		if (btAdapter.isEnabled())
			btAdapter.disable();
	}

	protected void disconnect() {
		connectThread.cancel();
	}

	protected boolean isConnected() {
		if (mSocket == null)
			return false;
		else
			return mSocket.isConnected();
	}

	protected boolean isSearching() {
		return btAdapter.isDiscovering();
	}

	protected void send(String data) {
		if (isConnected()) {
			if (data.equals("")) {
				byte[] clear = new byte[1];
				clear[0] = (byte) 0x00;
				connectedThread.write(clear);
			} else {
				connectedThread.write(data.getBytes(Charset.forName("UTF-8")));
				MainActivity.editText1.setText(null);
			}
		} else {
			MainActivity.showToast("دستگاه وصل نیست !");
		}
	}

	protected boolean isBtEnable() {
		return btAdapter.isEnabled();
	}

	protected void startSearching(Activity activity) {
		btAdapter.startDiscovery();
		activity.registerReceiver(mReceiver, mfilter);

	}

	protected void stopSearching(Activity activity) {
		if (btAdapter.isDiscovering())
			btAdapter.cancelDiscovery();
		activity.unregisterReceiver(mReceiver);
	}

	private class ConnectThread {

		private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

		private ConnectThread(BluetoothDevice device) {
			BluetoothSocket tmp = null;
			try {
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
			}
			mSocket = tmp;
		}

		private void run() {
			btAdapter.cancelDiscovery();
			try {
				mSocket.connect();
			} catch (IOException connectException) {
				try {
					mSocket.close();
				} catch (IOException closeException) {
				}
				return;
			}
		}

		private void cancel() {
			try {
				mSocket.close();
			} catch (Exception e) {
			}
		}
	}

	private class ConnectedThread {

		private final OutputStream mmOutStream;

		private ConnectedThread(BluetoothSocket socket) {
			OutputStream tmpOut = null;
			try {
				tmpOut = socket.getOutputStream();
			} catch (Exception e) {
			}
			mmOutStream = tmpOut;
		}

		private void write(byte[] bytes) {
			try {
				mmOutStream.write(bytes);
			} catch (IOException e) {
			}
		}

	}

}
