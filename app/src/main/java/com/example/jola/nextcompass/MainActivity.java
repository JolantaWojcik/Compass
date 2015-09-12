package com.example.jola.nextcompass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class MainActivity extends Activity implements SensorEventListener{

    private RelativeLayout relativeLayout;
    private Sensor megnetomerter;
    private Sensor accel;
    private SensorManager sensorManager;

    private float[] gravity = null;
    private float[] magnetic = null;

    private double rotationInDegrees;
    private ArrowView arrowView;

    private EditText edit_lat, edit_long;
    private float get_lat_value, get_long_value;
    String lat, lang, check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_lat = new EditText(MainActivity.this);
        edit_long = new EditText(MainActivity.this);

        lat = edit_lat.getText().toString();
        Log.d("value2", lat.toString());

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        arrowView = new ArrowView(getApplicationContext());
        relativeLayout.addView(arrowView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        megnetomerter = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if(null == accel || null == megnetomerter){
            finish();
        }
    }

    public void getLat(View view) {
        final EditText input = new EditText(this);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Latitude");
        alertDialog.setMessage("Enter new latitude");
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edit_lat.setText(input.getText());
               // lat = edit_lat.getText().toString();
            }
        });
        alertDialog.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    public void getLong(View view) {
        final EditText input = new EditText(this);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Longitude");
        alertDialog.setMessage("Enter new longitude");
        alertDialog.setView(edit_lat);
        alertDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edit_long.setText(input.getText());
               // lang = edit_lat.getText().toString();
            }
        });
        alertDialog.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, megnetomerter, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            gravity = new float[3];
            System.arraycopy(event.values, 0, gravity, 0, 3);
        }else
        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            magnetic = new float[3];
            System.arraycopy(event.values, 0, magnetic, 0, 3);
        }
        if(gravity != null && magnetic != null){
            float rotationMartix[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(rotationMartix, null, gravity, magnetic);
            if(success){
                float orientationMatrix[] = new float[3];
                SensorManager.getOrientation(rotationMartix, orientationMatrix);
                float rotationInRadions = orientationMatrix[0];
                rotationInDegrees = Math.toDegrees(rotationInRadions);
                arrowView.invalidate();
                gravity = magnetic = null;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class ArrowView extends View {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.compassbackup);
        int bitmapWidth= bitmap.getWidth();

        Bitmap bitmapArrow = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
        int bitmapArrowWidth= bitmapArrow.getWidth();

        int parentWidth;
        int parentHeight;

        int centerX;
        int centerY;

        int viewTopX;
        int viewLeftY;

        int arrowLeftY;
        int arrowViewTopX;

        public void onSizeChanged(int w, int h, int oldW, int oldH){
            parentWidth = relativeLayout.getWidth();
            parentHeight = relativeLayout.getHeight();

            centerX = parentWidth /2;
            centerY = parentHeight /2;

            viewLeftY = centerX - bitmapWidth/2;
            viewTopX = centerY - bitmapWidth/2;

            arrowLeftY = centerX - bitmapArrowWidth / 2;
            arrowViewTopX = centerY - bitmapArrowWidth / 2;
        }

        protected void onDraw(Canvas canvas){
            canvas.save();
            canvas.rotate((float) -rotationInDegrees, centerX, centerY);
            canvas.drawBitmap(bitmap, viewLeftY, viewTopX, null);
            canvas.drawBitmap(bitmapArrow, arrowLeftY, arrowViewTopX, null);
            canvas.restore();
        }

        public ArrowView(Context applicationContext) {
            super(getApplicationContext());
            applicationContext = getApplicationContext();
        }
    }
}
