package ir.samadi.sadra.robotooth;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorClass implements SensorEventListener {

	private final SensorManager snsMgr;
	protected Sensor linearAccelerationSns;
	protected Sensor proximitySns;
	protected Sensor gravitySns;
	protected float accSum;
	protected float prox;
	protected float gravX;
	protected float gravY;

	public SensorClass(Object object) {
		snsMgr = (SensorManager) object;
		gravitySns = snsMgr.getDefaultSensor(Sensor.TYPE_GRAVITY); // minsdk,
																	// api 9
		proximitySns = snsMgr.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		linearAccelerationSns = snsMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION); // minsdk,
																							// api
																							// 9
	}

	protected void regSensor(Sensor sensor) {
		snsMgr.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
	}

	protected void unRegSensor(Sensor sensor) {
		snsMgr.unregisterListener(this, sensor);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_GRAVITY:
			gravX = event.values[0];
			gravY = event.values[1];
			break;
		case Sensor.TYPE_LINEAR_ACCELERATION:
			accSum = event.values[0] + event.values[1] + event.values[2];
			break;
		case Sensor.TYPE_PROXIMITY:
			prox = event.values[0];
			break;
		}
		MainActivity.onSensManage();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

}
