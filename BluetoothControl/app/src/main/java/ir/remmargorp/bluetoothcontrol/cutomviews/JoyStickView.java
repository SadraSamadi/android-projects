package ir.remmargorp.bluetoothcontrol.cutomviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JoyStickView extends View {

    private static final float DIRECTIONS_PADDING = 10f;
    private static final float DIRECTIONS_RADIUS = 3f;

    public enum Direction {
        CENTER, UP_RIGHT, UP, UP_LEFT, LEFT, DOWN_LEFT, DOWN, DOWN_RIGHT, RIGHT
    }

    public interface JoyStickListener {
        void onJoyStick(float angle, float power, Direction direction);
    }

    private float centerX;
    private float centerY;

    private float xPosition;
    private float yPosition;

    private float buttonRadius;
    private float backRadius;

    private Paint backPaint;
    private Paint buttonPaint;
    private Paint directionsPaint;

    private JoyStickListener mListener;

    public JoyStickView(Context context) {
        this(context, null);
    }

    public JoyStickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backPaint.setColor(Color.DKGRAY);
        backPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        buttonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        buttonPaint.setColor(Color.RED);
        buttonPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        directionsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        directionsPaint.setColor(Color.WHITE);
        directionsPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        centerX = w / 2f;
        centerY = h / 2f;
        float min = Math.min(w, h);
        backRadius = min * 0.375f;
        buttonRadius = min * 0.125f;
        xPosition = centerX;
        yPosition = centerY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, backRadius, backPaint);
        for (int theta = 0; theta < 360; theta += 45) {
            float rad = (float) Math.toRadians(theta);
            float x = (float) ((backRadius - DIRECTIONS_PADDING) * Math.cos(rad) + centerX);
            float y = (float) ((backRadius - DIRECTIONS_PADDING) * Math.sin(rad) + centerY);
            canvas.drawCircle(x, y, DIRECTIONS_RADIUS, directionsPaint);
        }
        canvas.drawCircle(xPosition, yPosition, buttonRadius, buttonPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return onTouchDown(x, y);
            case MotionEvent.ACTION_MOVE:
                return onTouchMove(x, y);
            case MotionEvent.ACTION_UP:
                return onTouchUp(x, y);
        }
        return super.onTouchEvent(event);
    }

    private boolean onTouchDown(float x, float y) {
        if (distance(x, y, centerX, centerY) < backRadius) {
            xPosition = x;
            yPosition = y;
            invalidate();
            onChange();
            return true;
        }
        return false;
    }

    private boolean onTouchMove(float x, float y) {
        float distance = distance(x, y, centerX, centerY);
        if (distance > backRadius) {
            xPosition = (x - centerX) * backRadius / distance + centerX;
            yPosition = (y - centerY) * backRadius / distance + centerY;
        } else {
            xPosition = x;
            yPosition = y;
        }
        invalidate();
        onChange();
        return true;
    }

    private boolean onTouchUp(float x, float y) {
        xPosition = centerX;
        yPosition = centerY;
        invalidate();
        onChange();
        return false;
    }

    private float getAngle() {
        float x = xPosition - centerX;
        float y = centerY - yPosition;
        float alpha = (float) Math.atan(y / x);
        if (x < 0)
            alpha += Math.PI;
        else if (y < 0)
            alpha += 2 * Math.PI;
        return (float) Math.toDegrees(alpha);
    }

    private float getPower() {
        return distance(xPosition, yPosition, centerX, centerY) / backRadius;
    }

    private Direction getDirection(float angle) {
        Direction direction = Direction.CENTER;
        if (!Float.isNaN(angle)) {
            angle += angle < 22.5f ? 337.5f : -22.5f;
            for (int i = 1, theta = 0; i <= 8; i++, theta += 45) {
                if (angle >= theta && angle < theta + 45)
                    direction = Direction.values()[i];
            }
        }
        return direction;
    }

    private float distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public void setJoyStickListener(JoyStickListener listener) {
        this.mListener = listener;
    }

    private void onChange() {
        if (mListener != null) {
            float angle = getAngle();
            mListener.onJoyStick(angle, getPower(), getDirection(angle));
        }
    }

}
