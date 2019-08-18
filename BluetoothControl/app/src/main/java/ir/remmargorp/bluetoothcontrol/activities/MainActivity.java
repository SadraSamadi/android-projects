package ir.remmargorp.bluetoothcontrol.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import ir.remmargorp.bluetoothcontrol.BluetoothConnection;
import ir.remmargorp.bluetoothcontrol.R;
import ir.remmargorp.bluetoothcontrol.cutomviews.CustomToast;
import ir.remmargorp.bluetoothcontrol.cutomviews.JoyStickView;

public class MainActivity extends AppCompatActivity implements JoyStickView.JoyStickListener,
        View.OnClickListener, View.OnLongClickListener {

    private final String TAG = getClass().getName();

    public static final int REQUEST_BLUETOOTH_CONNECTION = 1;

    private EditText dataEditText;

    private JoyStickView joyStickView;

    private Button[] buttons;

    private BluetoothStateReceiver mReceiver;

    public static BluetoothConnection connection;

    class KeyButton {
        String name;
        String key;
        boolean enable;
    }

    private KeyButton[] keyButtons;

    class AnalogControl {
        boolean enable;
        String up;
        String upRight;
        String right;
        String downRight;
        String down;
        String downLeft;
        String left;
        String upLeft;
    }

    private AnalogControl analogControl;

    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.not_connected);
        dataEditText = (EditText) findViewById(R.id.dataEditText);
        joyStickView = (JoyStickView) findViewById(R.id.joyStick);
        buttons = new Button[9];
        buttons[0] = (Button) findViewById(R.id.keyButton0);
        buttons[1] = (Button) findViewById(R.id.keyButton1);
        buttons[2] = (Button) findViewById(R.id.keyButton2);
        buttons[3] = (Button) findViewById(R.id.keyButton3);
        buttons[4] = (Button) findViewById(R.id.keyButton4);
        buttons[5] = (Button) findViewById(R.id.keyButton5);
        buttons[6] = (Button) findViewById(R.id.keyButton6);
        buttons[7] = (Button) findViewById(R.id.keyButton7);
        buttons[8] = (Button) findViewById(R.id.keyButton8);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            CustomToast.toast(getString(R.string.bt_not_supported));
        } else {
            mReceiver = new BluetoothStateReceiver();
            IntentFilter mFilter = new IntentFilter();
            mFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, mFilter);
        }
        loadPrefs();
    }

    @Override
    protected void onResume() {
        loadPrefs();
        if (connection != null && !connection.isConnected())
            closeConnection();
        super.onResume();
    }

    private void loadPrefs() {
        keyButtons = new KeyButton[9];
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        for (int i = 0; i < 9; i++) {
            KeyButton kb = keyButtons[i] = new KeyButton();
            kb.name = prefs.getString("button_name" + i, getString(R.string.key) + " " + (i + 1));
            kb.key = prefs.getString("button_key" + i, "");
            kb.enable = prefs.getBoolean("button_enable" + i, true);
        }
        analogControl = new AnalogControl();
        analogControl.enable = prefs.getBoolean("enable_analog", true);
        analogControl.up = prefs.getString("analog_up_key", "");
        analogControl.up = prefs.getString("analog_up_right_key", "");
        analogControl.up = prefs.getString("analog_right_key", "");
        analogControl.up = prefs.getString("analog_down_right_key", "");
        analogControl.up = prefs.getString("analog_down_key", "");
        analogControl.up = prefs.getString("analog_down_left_key", "");
        analogControl.up = prefs.getString("analog_left_key", "");
        analogControl.up = prefs.getString("analog_up_left_key", "");
        initComponents();
    }

    private void initComponents() {
        for (int i = 0; i < 9; i++) {
            Button button = buttons[i];
            KeyButton keyButton = keyButtons[i];
            if (keyButton.enable) {
                button.setVisibility(View.VISIBLE);
                button.setText(keyButton.name);
                button.setTag(i);
                button.setOnClickListener(this);
                button.setOnLongClickListener(this);
            } else {
                button.setVisibility(View.INVISIBLE);
                button.setOnClickListener(null);
                button.setOnLongClickListener(null);
            }
        }
        if (analogControl.enable) {
            joyStickView.setVisibility(View.VISIBLE);
            joyStickView.setJoyStickListener(this);
        } else {
            joyStickView.setVisibility(View.INVISIBLE);
            joyStickView.setJoyStickListener(null);
        }
    }

    @Override
    public void onJoyStick(float angle, float power, JoyStickView.Direction direction) {
        if (power < 0.25f) {
            flag = true;
        } else if (flag) {
            String key = "";
            switch (direction) {
                case UP:
                    key = analogControl.up;
                    break;
                case UP_RIGHT:
                    key = analogControl.upRight;
                    break;
                case RIGHT:
                    key = analogControl.right;
                    break;
                case DOWN_RIGHT:
                    key = analogControl.downRight;
                    break;
                case DOWN:
                    key = analogControl.down;
                    break;
                case DOWN_LEFT:
                    key = analogControl.downLeft;
                    break;
                case LEFT:
                    key = analogControl.left;
                    break;
                case UP_LEFT:
                    key = analogControl.upLeft;
                    break;
            }
            flag = sendData(key);
        }
    }

    public void onSendButtonClicked(View view) {
        Editable editable = dataEditText.getText();
        if (sendData(editable.toString())) {
            editable.clear();
        }
    }

    private boolean sendData(String data) {
        if (connection == null) {
            CustomToast.toast(getString(R.string.not_connected));
            return false;
        } else {
            if (!data.isEmpty())
                connection.send(data);
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        int index = (int) v.getTag();
        sendData(keyButtons[index].key);
    }

    @Override
    public boolean onLongClick(View v) {
        final int index = (int) v.getTag();
        final Button button = buttons[index];
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = View.inflate(getApplicationContext(), R.layout.button_edit_layout, null);
        final Switch enableSwitch = (Switch) view.findViewById(R.id.buttonEnableSwitch);
        final EditText nameEditText = (EditText) view.findViewById(R.id.buttonNameEditText);
        nameEditText.setText(keyButtons[index].name);
        final EditText keyEditText = (EditText) view.findViewById(R.id.buttonKeyEditText);
        keyEditText.setText(keyButtons[index].key);
        builder.setTitle(getString(R.string.edit_button));
        builder.setView(view);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = nameEditText.getText().toString();
                String newKey = keyEditText.getText().toString();
                boolean newEnable = enableSwitch.isChecked();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                keyButtons[index].name = newName;
                editor.putString("button_name" + index, newName);
                keyButtons[index].key = newKey;
                editor.putString("button_key" + index, newKey);
                keyButtons[index].enable = newEnable;
                editor.putBoolean("button_enable" + index, newEnable);
                editor.apply();
                button.setVisibility(newEnable ? View.VISIBLE : View.INVISIBLE);
                button.setText(newName);
                dialog.cancel();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_devices_list:
                Intent intent = new Intent(getApplicationContext(), DevicesListActivity.class);
                startActivityForResult(intent, REQUEST_BLUETOOTH_CONNECTION);
                break;
            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;
            case R.id.action_about:
                about();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void about() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.about);
        View view = View.inflate(getApplicationContext(), R.layout.layout_about, null);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = " " + pInfo.versionName;
            TextView textView = (TextView) view.findViewById(R.id.appVersionTextView);
            textView.setText(getString(R.string.version).concat(version));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "app version not found", e);
        }
        builder.setView(view);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_BLUETOOTH_CONNECTION) {
            if (resultCode == RESULT_OK) {
                String name = data.getStringExtra(DevicesListActivity.DEVICE_NAME);
                setTitle(name);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class BluetoothStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_OFF) {
                    closeConnection();
                    CustomToast.toast(getString(R.string.bt_is_off));
                } else if (state == BluetoothAdapter.STATE_DISCONNECTED) {
                    closeConnection();
                    CustomToast.toast(getString(R.string.disconnected));
                }
            }
        }

    }

    private void closeConnection() {
        if (connection != null) {
            connection.cancel();
            connection = null;
        }
        setTitle(R.string.not_connected);
    }

    @Override
    protected void onDestroy() {
        closeConnection();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

}
