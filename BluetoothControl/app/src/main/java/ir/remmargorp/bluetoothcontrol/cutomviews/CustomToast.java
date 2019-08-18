package ir.remmargorp.bluetoothcontrol.cutomviews;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ir.remmargorp.bluetoothcontrol.AppController;
import ir.remmargorp.bluetoothcontrol.R;

public class CustomToast extends Toast {

    private static Context mContext = AppController.getInstance().getApplicationContext();

    private CustomToast(String text, int duration) {
        super(mContext);
        setDuration(duration);
        View view = View.inflate(mContext, R.layout.custom_toast_layout, null);
        TextView textView = (TextView) view.findViewById(R.id.customToastTextView);
        textView.setText(text);
        setView(view);
        show();
    }

    public static void toast(Object obj, int duration) {
        String text = String.valueOf(obj);
        new CustomToast(text, duration);
        Log.v("TOAST", text);
    }

    public static void toast(Object obj) {
        toast(obj, LENGTH_SHORT);
    }

}
