package ir.remmargorp.bluetoothcontrol.activities.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import ir.remmargorp.bluetoothcontrol.R;
import ir.remmargorp.bluetoothcontrol.activities.DevicesListActivity;
import ir.remmargorp.bluetoothcontrol.cutomviews.CustomToast;

public class AvailableDevicesFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private DevicesListActivity activity;

    private BluetoothAdapter mBluetoothAdapter;

    private ArrayAdapter<String> arrayAdapter;

    private List<BluetoothDevice> bluetoothDevices;

    private Button searchButton;

    private ProgressBar progressBar;

    private BluetoothSearchReceiver mReceiver;

    private boolean searching;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_available_devices_list, container, false);
        ListView availableDevicesListView = (ListView) view.findViewById(R.id.availableDevicesListView);
        searchButton = (Button) view.findViewById(R.id.searchButton);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        searchButton.setOnClickListener(this);

        mBluetoothAdapter = activity.getBluetoothAdapter();
        if (mBluetoothAdapter != null) {
            bluetoothDevices = new ArrayList<>();
            arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
            availableDevicesListView.setAdapter(arrayAdapter);
            availableDevicesListView.setOnItemClickListener(this);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        if (mBluetoothAdapter == null) {
            CustomToast.toast(getString(R.string.bt_not_supported));
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                cancelSearching();
                removeDevices();
                setSearching(true);
                IntentFilter mFilter = new IntentFilter();
                mFilter.addAction(BluetoothDevice.ACTION_FOUND);
                mFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                mReceiver = new BluetoothSearchReceiver();
                activity.registerReceiver(mReceiver, mFilter);
                mBluetoothAdapter.startDiscovery();
                CustomToast.toast(getString(R.string.start_searching));
            } else {
                CustomToast.toast(getString(R.string.bt_is_off));
            }
        }
    }

    private void setSearching(boolean searching) {
        this.searching = searching;
        searchButton.setVisibility(searching ? View.INVISIBLE : View.VISIBLE);
        progressBar.setVisibility(searching ? View.VISIBLE : View.INVISIBLE);
    }

    public void cancelSearching() {
        if (searching) {
            removeReceiver();
            mBluetoothAdapter.cancelDiscovery();
            setSearching(false);
        }
    }

    private void removeReceiver() {
        if (mReceiver != null) {
            activity.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    public void removeDevices() {
        bluetoothDevices.clear();
        arrayAdapter.clear();
    }

    public void setActivity(DevicesListActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice device = bluetoothDevices.get(position);
        activity.connect(device);
    }

    private class BluetoothSearchReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && !bluetoothDevices.contains(device)) {
                    bluetoothDevices.add(device);
                    arrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    CustomToast.toast(getString(R.string.device_found));
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setSearching(false);
                if (!mBluetoothAdapter.isEnabled()) {
                    removeReceiver();
                }
                CustomToast.toast(getString(R.string.stop_searching));
            }
        }

    }

    @Override
    public void onDestroy() {
        removeReceiver();
        super.onDestroy();
    }

}