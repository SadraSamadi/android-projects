package ir.remmargorp.bluetoothcontrol.activities;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import ir.remmargorp.bluetoothcontrol.BluetoothConnection;
import ir.remmargorp.bluetoothcontrol.R;
import ir.remmargorp.bluetoothcontrol.activities.fragments.AvailableDevicesFragment;
import ir.remmargorp.bluetoothcontrol.activities.fragments.PairedDevicesFragment;
import ir.remmargorp.bluetoothcontrol.cutomviews.CustomToast;

public class DevicesListActivity extends AppCompatActivity {

    private final String TAG = getClass().getName();

    public static final String DEVICE_NAME = "device_name";

    private static final int REQUEST_ENABLE_BT = 1;

    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private PairedDevicesFragment pairedDevicesFragment;

    private AvailableDevicesFragment availableDevicesFragment;

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothStateReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        pairedDevicesFragment = new PairedDevicesFragment();
        pairedDevicesFragment.setActivity(this);
        availableDevicesFragment = new AvailableDevicesFragment();
        availableDevicesFragment.setActivity(this);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            CustomToast.toast(getString(R.string.bt_not_supported));
        } else {
            mReceiver = new BluetoothStateReceiver();
            IntentFilter mFilter = new IntentFilter();
            mFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, mFilter);
            if (!mBluetoothAdapter.isEnabled()) {
                CustomToast.toast(getString(R.string.bt_is_off));
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            }
        }
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    private void bluetoothOn() {
        CustomToast.toast(getString(R.string.bt_is_on));
        pairedDevicesFragment.showDevices();
    }

    private void bluetoothOff() {
        CustomToast.toast(getString(R.string.bt_is_off));
        pairedDevicesFragment.removeDevices();
        availableDevicesFragment.removeDevices();
    }

    public void connect(BluetoothDevice device) {
        BluetoothConnection connection = MainActivity.connection;
        if (connection != null) {
            connection.cancel();
        }
        MainActivity.connection = null;
        ConnectTask task = new ConnectTask(device);
        task.execute();
    }

    private class ConnectTask extends AsyncTask<Void, Void, BluetoothSocket> {

        private BluetoothDevice mDevice;

        private BluetoothSocket mSocket;

        private ProgressDialog progress;

        public ConnectTask(BluetoothDevice mDevice) {
            this.mDevice = mDevice;
        }

        @Override
        protected void onPreExecute() {
            availableDevicesFragment.cancelSearching();
            progress = new ProgressDialog(DevicesListActivity.this);
            progress.setCanceledOnTouchOutside(false);
            progress.setMessage(getString(R.string.connecting_to, mDevice.getName()));
            progress.setIndeterminate(true);
            progress.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            cancel(true);
                        }
                    });
            progress.show();
        }

        @Override
        protected BluetoothSocket doInBackground(Void... params) {
            try {
                mSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
                mSocket.connect();
            } catch (IOException e) {
                Log.e(TAG, "can't connect to device");
                closeSocket();
            }
            return mSocket;
        }

        private void closeSocket() {
            if (mSocket != null) {
                try {
                    mSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "can't close socket");
                }
                mSocket = null;
            }
        }

        @Override
        protected void onPostExecute(BluetoothSocket bluetoothSocket) {
            progress.cancel();
            if (bluetoothSocket == null) {
                CustomToast.toast(getString(R.string.device_not_connected));
            } else {
                CustomToast.toast(getString(R.string.connected));
                BluetoothConnection connection = new BluetoothConnection(bluetoothSocket);
                result(connection, mDevice.getName());
            }
        }

        @Override
        protected void onCancelled() {
            closeSocket();
        }

    }

    private void result(BluetoothConnection connection, String name) {
        Intent intent = new Intent();
        MainActivity.connection = connection;
        intent.putExtra(DEVICE_NAME, name);
        setResult(RESULT_OK, intent);
        finish();
    }

    private class BluetoothStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_ON) {
                    bluetoothOn();
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    bluetoothOff();
                }
            }
        }

    }

    @Override
    public void onDestroy() {
        if (mReceiver != null)
            unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return pairedDevicesFragment;
                case 1:
                    return availableDevicesFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.paired_devices);
                case 1:
                    return getString(R.string.available_devices);
            }
            return null;
        }

    }

}
