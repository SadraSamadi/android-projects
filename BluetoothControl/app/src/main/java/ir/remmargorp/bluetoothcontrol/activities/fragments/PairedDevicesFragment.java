package ir.remmargorp.bluetoothcontrol.activities.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ir.remmargorp.bluetoothcontrol.R;
import ir.remmargorp.bluetoothcontrol.activities.DevicesListActivity;

public class PairedDevicesFragment extends Fragment implements AdapterView.OnItemClickListener {

    private DevicesListActivity activity;

    private ListView pairedDevicesListView;

    private ArrayAdapter<String> arrayAdapter;

    private List<BluetoothDevice> bluetoothDevices;

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paired_devices_list, container, false);
        mBluetoothAdapter = activity.getBluetoothAdapter();
        if (mBluetoothAdapter != null) {
            pairedDevicesListView = (ListView) view.findViewById(R.id.pairedDevicesListView);
            pairedDevicesListView.setOnItemClickListener(this);
            if (mBluetoothAdapter.isEnabled()) {
                showDevices();
            }
        }
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice device = bluetoothDevices.get(position);
        activity.connect(device);
    }

    public void showDevices() {
        bluetoothDevices = new ArrayList<>(mBluetoothAdapter.getBondedDevices());
        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        for (BluetoothDevice device : bluetoothDevices) {
            arrayAdapter.add(device.getName() + "\n" + device.getAddress());
        }
        pairedDevicesListView.setAdapter(arrayAdapter);
    }

    public void removeDevices() {
        arrayAdapter.clear();
        bluetoothDevices.clear();
    }

    public void setActivity(DevicesListActivity activity) {
        this.activity = activity;
    }

}
