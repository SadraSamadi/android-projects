package ir.samadi.sadra.robotooth;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

public class DevicesListActivity extends Activity {

	private static Button searchBtn;
	private static ProgressBar searchProgBar;
	private static ListView pairedDevicesList;
	private static ListView availableDevicesList;
	private static Set<BluetoothDevice> devicesSet;
	private static ArrayList<BluetoothDevice> pairedDevicesArray;
	private static ArrayList<BluetoothDevice> availableDevicesArray;
	private static BluetoothClass btClass;
	private static Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_devices_list);

		searchBtn = (Button) findViewById(R.id.discoveryButton1);
		searchProgBar = (ProgressBar) findViewById(R.id.searchProgressBar);
		pairedDevicesList = (ListView) findViewById(R.id.pairedDevicesList);
		availableDevicesList = (ListView) findViewById(R.id.availableDevicesList);
		btClass = MainActivity.btClass;
		context = this;
		pairedDevicesArray = new ArrayList<BluetoothDevice>();
		availableDevicesArray = new ArrayList<BluetoothDevice>();

		searchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btClass.startSearching(DevicesListActivity.this);
			}
		});

		pairedDevicesList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (pairedDevicesArray.size() != 0) {
					if (btClass.isConnected())
						btClass.disconnect();
					btClass.connect(pairedDevicesArray.get(position));
				}
			}
		});

		availableDevicesList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (availableDevicesArray.size() != 0) {
					if (btClass.isConnected())
						btClass.disconnect();
					btClass.connect(availableDevicesArray.get(position));
				}
			}
		});

		searchProgBar.setVisibility(View.GONE);
	}

	private static void updatePairedList() {
		devicesSet = btClass.getPairedDevices();
		ArrayList<String> pairedArrayList = new ArrayList<String>();
		pairedDevicesArray.clear();
		if (devicesSet.size() == 0) {
			pairedArrayList.add("موردی یافت نشد !");
		} else {
			for (BluetoothDevice tmp : devicesSet)
				pairedDevicesArray.add(tmp);
			for (BluetoothDevice mDevice : pairedDevicesArray)
				pairedArrayList.add(mDevice.getName() + "\n" + mDevice.getAddress());
		}
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,
				pairedArrayList);
		pairedDevicesList.setAdapter(mAdapter);
	}

	private static void updateAvailabeList() {
		ArrayList<String> availableArrayList = new ArrayList<String>();
		if (availableDevicesArray.size() == 0) {
			availableArrayList.add("موردی یافت نشد");
		} else {
			for (BluetoothDevice mDevice : availableDevicesArray)
				availableArrayList.add(mDevice.getName() + "\n" + mDevice.getAddress());
		}
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,
				availableArrayList);
		availableDevicesList.setAdapter(mAdapter);
	}

	protected static void onFound(BluetoothDevice bluetoothDevice) {
		availableDevicesArray.add(bluetoothDevice);
		updatePairedList();
		updateAvailabeList();
	}

	protected static void onDiscoveryStart() {
		searchBtn.setText("");
		searchBtn.setEnabled(false);
		searchProgBar.setVisibility(View.VISIBLE);
	}

	protected static void onDiscoveryFinish() {
		searchBtn.setText("جستجو");
		searchBtn.setEnabled(true);
		searchProgBar.setVisibility(View.GONE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updatePairedList();
		updateAvailabeList();
		if (!btClass.isBtEnable())
			finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (btClass.isSearching())
			btClass.stopSearching(this);
	}

}