package com.yiyir.stepcounter;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private final String TAG = "StepCounter";
    private final int LAG = 5;
    //    private final double THRESHOLD = 3.5;
//    private final double INFLUENCE = 0.5;
    private final double SMOOTHING = 8.0;
    //private final int NUMOFCOUNTS = 4;
    private SensorManager sensorManager;
    private Button button;
    private TextView steps;
    private GraphView graph1;
    private GraphView graph2;
    private LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>();
    private boolean isRunning = false;
    //private int counts = 0;
    private boolean hasHit = false;
    private int stepCounter = 0;
    private int counter = 1;
    private double mag = 0.0;
//    private double filteredMag = 0.0;
//    private double avg = 0.0;
//    private double std = 0.0;
//    private int lastSignal = 0;
//    private int signal;
//    private double[] array = new double[LAG];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        button = (Button) findViewById(R.id.start_button);
        steps = (TextView) findViewById(R.id.vertical_counts);
        graph1 = (GraphView) findViewById(R.id.graph1);
        graph1.getViewport().setYAxisBoundsManual(true);
        graph1.getViewport().setXAxisBoundsManual(true);
        graph1.getViewport().setMinX(4);
        graph1.getViewport().setMaxX(80);
        graph1.getViewport().setMinY(0);
        graph1.getViewport().setMaxY(30);
        graph1.addSeries(series1);
        graph2 = (GraphView) findViewById(R.id.graph2);
        graph2.getViewport().setYAxisBoundsManual(true);
        graph2.getViewport().setXAxisBoundsManual(true);
        graph2.getViewport().setMinX(4);
        graph2.getViewport().setMaxX(80);
        graph2.getViewport().setMinY(0);
        graph2.getViewport().setMaxY(30);
        graph2.addSeries(series2);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "onCreate is called!");
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(sensorEvent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onPause() {
        super.onPause();

    }
    

    public void getAccelerometer(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;
        float x = values[0];
        float y = values[1];
        float z = values[2];
        double magWithNoise = Math.sqrt(x * x + y * y + z * z);
        if (counter == 1) {
            mag = magWithNoise;
        } else {
            mag += (magWithNoise - mag) / SMOOTHING;
            Log.d(TAG, "getAcceleration is called!" + mag);
        }

//
//
//        if (counter < LAG) {
//            array[counter - 1] = mag;
//        } else if (counter == LAG) {
//            array[counter - 1] = mag;
//            filteredMag = mag;
//            avg = getMean(array);
//            std = getSTD(array);
//
//        } else {
//            Log.d(TAG, "getAcceleration is called!" + Math.abs(mag-avg)+" Threshold"  + (std * THRESHOLD));
//            if (Math.abs(mag - avg) > (std * THRESHOLD)) {
//                filteredMag = INFLUENCE * mag + (1 - INFLUENCE) * filteredMag;
//                lastSignal = 1;
//
//            } else if (Math.abs(mag - avg) <= (std * THRESHOLD)) {
//                if (lastSignal == 1) {
//                    stepCounter++;
//                }
//                lastSignal = 0;
//                filteredMag = mag;
//            }
//            int mod = (counter - 1) % LAG;
//            array[mod] = filteredMag;
//            avg = getMean(array);
//            std = getSTD(array);
//
//        }
        if (isRunning) {
            if (mag >= 11.0) {
                hasHit = true;

            } else {
                if (hasHit) {
                    stepCounter++;
                    hasHit = false;
                }
            }
            series1.appendData(new DataPoint(counter, magWithNoise), true, 1000);
            series2.appendData(new DataPoint(counter, mag), true, 1000);
            counter++;
            steps.setText(String.valueOf(stepCounter));

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

//    private double getMean(double[] data) {
//        double total = 0.0;
//        for (int i = 0; i < data.length; i++) {
//            total += data[i];
//        }
//        return total / data.length;
//    }
//
//    private double getSTD(double[] data) {
//        double[] diff = new double[data.length];
//        for (int i = 0; i < data.length; i++) {
//            diff[i] = Math.pow(data[i] - getMean(data), 2);
//        }
//        double newTotal = 0.0;
//        for (int i = 0; i < diff.length; i++) {
//            newTotal += diff[i];
//        }
//        return Math.sqrt(newTotal / (diff.length - 1));
//    }

    public void pressButton(View view) {
        if (!isRunning) {
            isRunning = true;
            button.setText("STOP");
        } else {
            isRunning = false;
            button.setText("START");
        }
        Log.d(TAG, "pressButton is called!");
    }
}
