package ir.remmargorp.bluetoothcontrol;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

public class BluetoothConnection {

    private final String TAG = getClass().getName();

    private BluetoothSocket mSocket;

    private OutputStream outputStream;

    public BluetoothConnection(BluetoothSocket bluetoothSocket) {
        mSocket = bluetoothSocket;
        try {
            outputStream = mSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "can't do output");
        }
    }

    public boolean send(String data) {
        if (isConnected() && outputStream != null) {
            try {
                outputStream.write(data.getBytes());
                Log.v(TAG, "data has been sent");
                return true;
            } catch (IOException e) {
                Log.e(TAG, "can't send data");
                return false;
            }
        } else {
            Log.e(TAG, "not connected");
            return false;
        }
    }

    public boolean cancel() {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "can't close stream");
                return false;
            }
        }
        try {
            mSocket.close();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "can't close socket");
            return false;
        }
    }

    public boolean isConnected() {
        return mSocket.isConnected();
    }

}
