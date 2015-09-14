package com.example.jola.nextcompass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener{

    private RelativeLayout relativeLayout;
    private Sensor megnetomerter;
    private Sensor accelometer;
    private SensorManager sensorManager;

    private float[] accelerometer = new float[3];
    private float[] magneticField = new float[3];

    private float[] rotationMartix = new float[9];
    private float[] orintationMatrix = new float[3];

    private float degreeStart = 0f;
    private float moveArrow;
    private float rotationInDegrees;

    private ArrowView arrowView;

    private ImageView imageView;
    private EditText edit_lat, edit_long;
    private float get_lat_value;
    private float get_long_value;
    private String value_lat, value_long;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        arrowView = new ArrowView(getApplicationContext());
        relativeLayout.addView(arrowView);

        edit_lat = new EditText(MainActivity.this);
        edit_long = new EditText(MainActivity.this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        megnetomerter = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if(null == megnetomerter && null == accelometer){
            finish();
        }
    }

    public void getLat(View view) {
        final  EditText input = new EditText(this);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Latitude");
        alertDialog.setMessage("Enter new latitude: ");
        alertDialog.setView(input);
        alertDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edit_lat.setText(input.getText());
                value_lat = edit_lat.getText().toString();
                get_lat_value = Float.parseFloat(value_lat);
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
        final EditText input2 = new EditText(this);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Longitude");
        alertDialog.setMessage("Enter new longitude: ");
        alertDialog.setView(input2);
        alertDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edit_long.setText(input2.getText());
                value_long = edit_long.getText().toString();
                get_long_value = Float.parseFloat(value_long);
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
        sensorManager.registerListener(this, accelometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, megnetomerter, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometer, 0, event.values.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magneticField, 0, event.values.length);
        }
        if (accelerometer != null && magneticField != null){
            SensorManager.getRotationMatrix(rotationMartix, null, accelerometer, magneticField);
            SensorManager.getOrientation(rotationMartix, orintationMatrix);
            float rotationInRadians = orintationMatrix[0];
            rotationInDegrees = (float) Math.toDegrees(rotationInRadians);

        RotateAnimation rotateAnimation = new RotateAnimation(
                degreeStart,
                -rotationInDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

            rotateAnimation.setDuration(210);
            rotateAnimation.setFillAfter(true);
            imageView.startAnimation(rotateAnimation);

            degreeStart = -rotationInDegrees;
        }
       if(get_lat_value != 0 && get_long_value !=0) {
           if (get_lat_value <= 45.0 && get_long_value < 45.0) {
               //north
               moveArrow = 0f;
           }
           if (get_lat_value >= 45.0 && get_lat_value <= 80.0 && get_long_value <= 80.0  && get_long_value > 45.0) {
               //Northeast
               moveArrow = 45f;
           }
           if (get_lat_value > 80.0 && get_lat_value <= 100.0 && get_long_value <= 100.0 && get_long_value > 80.0) {
               //East
               moveArrow = 90f;
           }
           if (get_lat_value > 100.0 &&  get_lat_value <= 140.0 && get_long_value <= 140.0 && get_long_value > 100.0) {
               //Southeast
               moveArrow = 135f;
           }
           if (get_lat_value > 140.0 && get_lat_value <= 180.0 && get_long_value <= 180.0 && get_long_value > 140.0) {
               //South
               moveArrow = 180f;
           }
           if (get_lat_value >= -45.0 && get_lat_value <= 0.0 && get_long_value > -45.0 && get_long_value < 0.0) {
               //Southwest
                moveArrow = 225f;
           }
           if (get_lat_value < -45.0 && get_lat_value >= -100.0 && get_long_value >= -100.0  && get_long_value < -45.0) {
               //west
               moveArrow = 270f;
           }
           if (get_lat_value < -100.0 && get_lat_value >= -140.0 && get_long_value >= -140.0 && get_long_value > -100.0) {
               //Northwest
               moveArrow = 315f;
           }
       }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class ArrowView extends View {
        Bitmap bitmapArrow = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
        int bitmapArrowWidth= bitmapArrow.getWidth();

        int parentWidth;
        int parentHeight;

        int centerX;
        int centerY;

        int arrowY;
        int arrowX;

        public void onSizeChanged(int width, int height, int oldWidth, int oldHeight){
            parentWidth = relativeLayout.getWidth();
            parentHeight = relativeLayout.getHeight();
            centerX = parentWidth /2;
            centerY = parentHeight /3;
            arrowY = centerX - bitmapArrowWidth / 2;
            arrowX = centerY - bitmapArrowWidth / 2;
        }

        protected void onDraw(Canvas canvas){
            canvas.save();
            canvas.rotate(moveArrow, centerX, centerY);
            canvas.drawBitmap(bitmapArrow, arrowY, arrowX, null);
            canvas.restore();
        }

        public ArrowView(Context applicationContext) {
            super(getApplicationContext());
            applicationContext = getApplicationContext();
        }
    }
}
