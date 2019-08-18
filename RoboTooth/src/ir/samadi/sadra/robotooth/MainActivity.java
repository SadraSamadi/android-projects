package ir.samadi.sadra.robotooth;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button button2;
	private Button[] buttons = new Button[9];
	protected static EditText editText1;
	private static SensorClass sensClass;
	protected static BluetoothClass btClass;
	private static Context mContext;
	private LinearLayout dataFieldLayout;

	private static boolean flag;

	// Analog Button Variables
	private ImageView blueCircle, orangeCircle;
	private RelativeLayout.LayoutParams mParams;
	private int downX, downY, x, y, Radius, radius, area;
	private double alpha, ratio;
	private Handler mHandler;
	private Runnable runnable;

	// Preferences Variables
	private SharedPreferences sharedPrefs;
	protected static boolean useAcc, useProx, useGrav, useDataField, useAnalog;
	protected static String up_analog_key, down_analog_key, right_analog_key, left_analog_key;
	protected static String up_grav_key, down_grav_key, right_grav_key, left_grav_key;
	protected static String prox_key, shake_key;
	protected static boolean[] useBtn = new boolean[9];
	protected static String[] nameBtn = new String[9];
	protected static String[] keyBtn = new String[9];
	protected static byte grav_sens, shake_sens;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		buttons[0] = (Button) findViewById(R.id.button1);
		buttons[1] = (Button) findViewById(R.id.button2);
		buttons[2] = (Button) findViewById(R.id.button3);
		buttons[3] = (Button) findViewById(R.id.button4);
		buttons[4] = (Button) findViewById(R.id.button5);
		buttons[5] = (Button) findViewById(R.id.button6);
		buttons[6] = (Button) findViewById(R.id.button7);
		buttons[7] = (Button) findViewById(R.id.button8);
		buttons[8] = (Button) findViewById(R.id.button9);
		dataFieldLayout = (LinearLayout) findViewById(R.id.am_dataFieldLayout);
		sensClass = new SensorClass(getSystemService(Service.SENSOR_SERVICE));
		btClass = new BluetoothClass();
		mContext = getApplicationContext();
		button2 = (Button) findViewById(R.id.am_sendButton);
		editText1 = (EditText) findViewById(R.id.editText1);
		blueCircle = (ImageView) findViewById(R.id.am_blue_circle);
		orangeCircle = (ImageView) findViewById(R.id.am_orange_circle);
		mParams = (RelativeLayout.LayoutParams) orangeCircle.getLayoutParams();
		mHandler = new Handler();

		runnable = new Runnable() {
			public void run() {
				analogControlManage();
				mHandler.post(this);
			}
		};

		if (!btClass.isBtEnable())
			btClass.enableBt(this);

		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (useAcc || useGrav || useProx)
					showToast("لطفا سنسورها را غیرفعال کنید !");
				else
					btClass.send(editText1.getText().toString());
			}
		});

		orangeCircle.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Radius = blueCircle.getHeight() / 2;
				radius = orangeCircle.getHeight() / 2;
				area = Radius / 2;
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (!btClass.isConnected())
						showToast("دستگاه وصل نیست !");
					downX = (int) event.getX();
					downY = (int) event.getY();
					mHandler.post(runnable);
					break;
				case MotionEvent.ACTION_MOVE:
					mParams.leftMargin += event.getX() - downX;
					mParams.topMargin += event.getY() - downY;
					x = (mParams.leftMargin + radius) - Radius;
					y = Radius - (mParams.topMargin + radius);
					if (Math.pow(x, 2) + Math.pow(y, 2) <= Math.pow(Radius, 2)) {
						orangeCircle.setLayoutParams(mParams);
					} else {
						ratio = (double) y / (double) Math.abs(x);

						if (x >= 0)
							alpha = Math.atan(ratio);
						else
							alpha = Math.PI - Math.atan(ratio);

						mParams.leftMargin = (int) (Radius * Math.cos(alpha) + (Radius - radius));
						mParams.topMargin = (int) ((Radius - radius) - Radius * Math.sin(alpha));
						orangeCircle.setLayoutParams(mParams);
					}
					break;
				case MotionEvent.ACTION_UP:
					x = 0;
					y = 0;
					mParams.leftMargin = (Radius - radius);
					mParams.topMargin = (Radius - radius);
					orangeCircle.setLayoutParams(mParams);
					mHandler.removeCallbacks(runnable);

					/**
					 * Informal
					 */
					btClass.send(" ");
					/**
					 * Informal
					 */
					break;
				}
				return true;
			}
		});

		buttons[0].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btClass.isConnected()) {
					btClass.send(keyBtn[0]);
				} else {
					showToast("دستگاه وصل نیست !");
				}
			}
		});

		buttons[1].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btClass.isConnected()) {
					btClass.send(keyBtn[1]);
				} else {
					showToast("دستگاه وصل نیست !");
				}
			}
		});

		buttons[2].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btClass.isConnected()) {
					btClass.send(keyBtn[2]);
				} else {
					showToast("دستگاه وصل نیست !");
				}
			}
		});

		buttons[3].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btClass.isConnected()) {
					btClass.send(keyBtn[3]);
				} else {
					showToast("دستگاه وصل نیست !");
				}
			}
		});

		buttons[4].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btClass.isConnected()) {
					btClass.send(keyBtn[4]);
				} else {
					showToast("دستگاه وصل نیست !");
				}
			}
		});

		buttons[5].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btClass.isConnected()) {
					btClass.send(keyBtn[5]);
				} else {
					showToast("دستگاه وصل نیست !");
				}
			}
		});

		buttons[6].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btClass.isConnected()) {
					btClass.send(keyBtn[6]);
				} else {
					showToast("دستگاه وصل نیست !");
				}
			}
		});

		buttons[7].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btClass.isConnected()) {
					btClass.send(keyBtn[7]);
				} else {
					showToast("دستگاه وصل نیست !");
				}
			}
		});

		buttons[8].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btClass.isConnected()) {
					btClass.send(keyBtn[8]);
				} else {
					showToast("دستگاه وصل نیست !");
				}
			}
		});

	}

	private void analogControlManage() {
		if (btClass.isConnected()) {
			if (x > area)
				btClass.send(right_analog_key);
			if (x < -area)
				btClass.send(left_analog_key);
			if (y > area)
				btClass.send(up_analog_key);
			if (y < -area)
				btClass.send(down_analog_key);
		}
	}

	protected static void onSensManage() {
		if (btClass.isConnected()) {
			if (sensClass.gravX > grav_sens) {
				btClass.send(left_grav_key);
				flag = true;
			}
			if (sensClass.gravX < -grav_sens) {
				btClass.send(right_grav_key);
				flag = true;
			}
			if (sensClass.gravY > grav_sens) {
				btClass.send(down_grav_key);
				flag = true;
			}
			if (sensClass.gravY < -grav_sens) {
				btClass.send(up_grav_key);
				flag = true;
			}
			if (sensClass.prox == 0) {
				btClass.send(prox_key);
				flag = true;
			}
			if (sensClass.accSum > shake_sens) {
				btClass.send(shake_key);
				flag = true;
			}
			if (sensClass.gravX <= grav_sens && sensClass.gravX >= -grav_sens && sensClass.gravY <= grav_sens
					&& sensClass.gravY >= -grav_sens && sensClass.prox != 0 && sensClass.accSum <= shake_sens) {
				if (flag) {
					btClass.send(" ");
					flag = false;
				}

			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item1:
			if (btClass.isBtEnable())
				startActivity(new Intent(MainActivity.this, DevicesListActivity.class));
			else
				showToast("لطفا بلوتوث را روشن کنید !");
			break;
		case R.id.item2:
			startActivity(new Intent(MainActivity.this, PreferencesActivity.class));
			break;
		case R.id.item3:
			startActivity(new Intent(MainActivity.this, AboutActivity.class));
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();

		refreshPrefs();

		editText1.setEnabled(!(useAcc || useGrav || useProx));

		if (useGrav)
			sensClass.regSensor(sensClass.gravitySns);
		else
			sensClass.unRegSensor(sensClass.gravitySns);

		if (useAcc)
			sensClass.regSensor(sensClass.linearAccelerationSns);
		else
			sensClass.unRegSensor(sensClass.linearAccelerationSns);

		if (useProx)
			sensClass.regSensor(sensClass.proximitySns);
		else
			sensClass.unRegSensor(sensClass.proximitySns);
	}

	private void refreshPrefs() {
		useAcc = sharedPrefs.getBoolean("useAcc", false);
		useProx = sharedPrefs.getBoolean("useProx", false);
		useGrav = sharedPrefs.getBoolean("useGrav", false);
		useDataField = sharedPrefs.getBoolean("useDataField", true);
		useAnalog = sharedPrefs.getBoolean("useAnalog", true);
		for (int i = 0; i < useBtn.length; i++) {
			useBtn[i] = sharedPrefs.getBoolean("useBtn" + (i+1), true);
		}
		grav_sens = Byte.parseByte(sharedPrefs.getString("gravity_sens", "5"));
		shake_sens = Byte.parseByte(sharedPrefs.getString("linear_acc_sens", "30"));
		prox_key = sharedPrefs.getString("prox_key", "");
		shake_key = sharedPrefs.getString("shake_key", "");
		up_analog_key = sharedPrefs.getString("up_analog_key", "");
		down_analog_key = sharedPrefs.getString("down_analog_key", "");
		right_analog_key = sharedPrefs.getString("right_analog_key", "");
		left_analog_key = sharedPrefs.getString("left_analog_key", "");
		up_grav_key = sharedPrefs.getString("up_gravity_key", "");
		down_grav_key = sharedPrefs.getString("down_gravity_key", "");
		right_grav_key = sharedPrefs.getString("right_gravity_key", "");
		left_grav_key = sharedPrefs.getString("left_gravity_key", "");
		for (int i = 0; i < nameBtn.length; i++) {
			nameBtn[i] = sharedPrefs.getString("name_btn" + (i + 1), "کلید " + (i + 1));
		}
		for (int i = 0; i < keyBtn.length; i++) {
			keyBtn[0] = sharedPrefs.getString("btn" + (i + 1) + "_key", "");
		}
		////////////////////////////////////////////////////////////////////////////////////////
		for (byte i = 0; i < 9; i++)
			buttons[i].setText(nameBtn[i]);

		for (byte i = 0; i < 9; i++)
			if (useBtn[i])
				buttons[i].setVisibility(View.VISIBLE);
			else
				buttons[i].setVisibility(View.INVISIBLE);

		if (useAnalog) {
			blueCircle.setVisibility(View.VISIBLE);
			orangeCircle.setVisibility(View.VISIBLE);
		} else {
			blueCircle.setVisibility(View.GONE);
			orangeCircle.setVisibility(View.GONE);
		}

		if (useDataField) {
			dataFieldLayout.setVisibility(View.VISIBLE);
		} else {
			dataFieldLayout.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (useAcc)
			sensClass.unRegSensor(sensClass.linearAccelerationSns);

		if (useGrav)
			sensClass.unRegSensor(sensClass.gravitySns);

		if (useProx)
			sensClass.unRegSensor(sensClass.proximitySns);

		if (btClass.isConnected())
			btClass.disconnect();

		if (btClass.isSearching())
			btClass.stopSearching(this);

		showToast(";-)");

	}

	protected static void showToast(String text) {
		Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
	}

}
