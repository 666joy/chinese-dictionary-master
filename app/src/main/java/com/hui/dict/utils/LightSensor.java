package com.hui.dict.utils;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hui.dict.R;

public class LightSensor extends AppCompatActivity implements SensorEventListener {

    SensorManager mSensorManager;
    Sensor mSensorLight;
    TextView mTvLight;

    private static final String TAG = "LightSensor";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
       // mTvLight=findViewById( R.id.tvSensorList );

        mSensorManager = (SensorManager) getSystemService( getApplicationContext().SENSOR_SERVICE );
        mSensorLight = mSensorManager.getDefaultSensor( Sensor.TYPE_LIGHT );
        mSensorManager.registerListener( this, mSensorLight, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();

        float currentValue = event.values[0];
       // Log.d(TAG,  String.valueOf(currentValue));

        // 修改app亮度

        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = 1-200/currentValue;

        if( lp.screenBrightness<0){
            lp.screenBrightness=0.1f;
        }
        Log.d(TAG,  String.valueOf(lp.screenBrightness));
        window.setAttributes(lp);


      //  mTvLight.setText( Float.toString( currentValue ) );
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}