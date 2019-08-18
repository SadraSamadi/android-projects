package ir.samadi.sadra.torch;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;
import ir.samadi.sadra.androidtest.R;

/**
 * 
 * @author Sadra Samadi
 * @category Tools
 * @version 1.0
 *
 */

public class MainActivity extends Activity {

	/** Not support string id */
	private final int NOT_SUPPORTED = R.string.not_supported;

	/**
	 * Define components
	 */
	private ImageView lampImage;
	private ToggleButton toggleButton;

	/**
	 * For camera
	 */
	private boolean support;
	private Camera camera;
	private Parameters cameraParams;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/**
		 * Find components from resource
		 */
		lampImage = (ImageView) findViewById(R.id.imageView1);
		toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);

		/**
		 * Check device camera and flash
		 */
		support = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)
				&& getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

		/**
		 * Prepare
		 */
		if (support) {
			camera = Camera.open();
			cameraParams = camera.getParameters();

			/**
			 * Check flash mode in start
			 */
			if (cameraParams.getFlashMode() == Camera.Parameters.FLASH_MODE_TORCH) {
				lampImage.setImageResource(R.drawable.lamp_on);
			}
		} else {
			toast(NOT_SUPPORTED);
		}

		/**
		 * Listen to button
		 */
		toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton bouttunView, boolean isChecked) {
				if (support) {
					turnOnOff(isChecked);
				} else {
					toast(NOT_SUPPORTED);
				}
			}
		});
	}

	/**
	 * Turn flash on/off
	 * 
	 * @param on_off
	 *            ON = true , OFF = false
	 */
	protected void turnOnOff(boolean on_off) {
		if (on_off) {
			cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			lampImage.setImageResource(R.drawable.lamp_on);
			camera.setParameters(cameraParams);
			camera.startPreview();
		} else {
			cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			lampImage.setImageResource(R.drawable.lamp_off);
			camera.setParameters(cameraParams);
			camera.stopPreview();
		}
	}

	/**
	 * Show message
	 * 
	 * @param id
	 *            String resource id
	 */
	private void toast(int id) {
		Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onDestroy() {
		/**
		 * Close camera on exit
		 */
		if (support) {
			camera.stopPreview();
			camera.release();
		}
		super.onDestroy();
	}

}
