/* Snapback S.r.l. 2015
More info about batching in Android: https://source.android.com/devices/sensors/batching.html

Learn more about Snapback!
www.snapback.io
developer@snapback.io
 */


package io.snapback.sample.batchingwake_uptest;

import android.annotation.TargetApi;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private final String LOG_TAG = "SnapbackBatchWakeupTest";

    private static final int FIFO_ERROR_NO_SENSOR = -1;
    private static final int FIFO_ERROR_NO_SUPPORT = -2;

    final String deviceName = getDeviceName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "Start");
        Log.d(LOG_TAG, "Device: " + deviceName);

        // Get the sensor manager
        SensorManager mgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor;
        // Accelerometer
        // wake-up
        sensor = getDefaultSensor(mgr, Sensor.TYPE_ACCELEROMETER, true);
        TextView textView = (TextView) findViewById(R.id.acc_wakeup_value_text);
        final int acc_wakeup_fifo = checkFifoReserved(sensor, textView);
        Log.d(LOG_TAG, "accelerometer wake-up: " + fifo2String(acc_wakeup_fifo));
        // non-wake-up
        sensor = getDefaultSensor(mgr, Sensor.TYPE_ACCELEROMETER, false);
        textView = (TextView) findViewById(R.id.acc_nonwakeup_value_text);
        final int acc_nonwakeup_fifo = checkFifoReserved(sensor, textView);
        Log.d(LOG_TAG, "accelerometer non-wake-up: " + fifo2String(acc_nonwakeup_fifo));

        // Proximity
        // wake-up
        sensor = getDefaultSensor(mgr, Sensor.TYPE_PROXIMITY, true);
        textView = (TextView) findViewById(R.id.prox_wakeup_value_text);
        final int prox_wakeup_fifo = checkFifoReserved(sensor, textView);
        Log.d(LOG_TAG, "proximity wake-up: " + fifo2String(prox_wakeup_fifo));
        // non-wake-up
        sensor = getDefaultSensor(mgr, Sensor.TYPE_PROXIMITY, false);
        textView = (TextView) findViewById(R.id.prox_nonwakeup_value_text);
        final int prox_nonwakeup_fifo = checkFifoReserved(sensor, textView);
        Log.d(LOG_TAG, "proximity non-wake-up" + fifo2String(prox_nonwakeup_fifo));

        textView = (TextView) findViewById(R.id.api_text);
        if(Build.VERSION.SDK_INT < 19) {
            textView.setText("API " + Build.VERSION.SDK_INT+" (batching support only from Android 4.4!");
        } else {
            textView.setText("API " + Build.VERSION.SDK_INT);
        }
        Log.d(LOG_TAG, "API " + Build.VERSION.SDK_INT);

        // Manage buttons
        // snapback share
        final String testValues = "Accelerometer batching\n"
                + "wake up: " + fifo2String(acc_wakeup_fifo) + "\n"
                + "non wake up: " + fifo2String(acc_nonwakeup_fifo) + "\n\n"
                + "Proximity batching\n"
                + "wake up: " + fifo2String(prox_wakeup_fifo) + "\n"
                + "non wake up: " + fifo2String(prox_nonwakeup_fifo)+"\n";

        Button btn = (Button) findViewById(R.id.share_snapback_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subjectText = "Batching & Wake-up test - " + deviceName;
                String bodyText = "Hi Snapback,\n"
                        +"My " + deviceName + " has the following characteristics for batching and wake-up:\n\n" + testValues;
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"developer@snapback.io"});
                i.putExtra(Intent.EXTRA_SUBJECT, subjectText);
                i.putExtra(Intent.EXTRA_TEXT, bodyText);
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // friends share
        final String appLink = "https://drive.google.com/open?id=0BzCckIssc0EfekV0R0lLV3MzMFk&authuser=0";
        final String slidesLink = "http://www.slideshare.net/SnapbackLabs/";
        final String gitLink = "https://github.com/SnapbackLabs/android-batching-wake-up-test.git";
        btn = (Button) findViewById(R.id.share_friends_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subjectText = "Batching & Wake-up test - " + deviceName;
                String bodyText = "Hi mate,\n\n"
                        + "I used a simple app to test batching, a new great energy saving feature in Android and the result is that..."
                        + "my " + deviceName + " " + fifos2SupportString(acc_wakeup_fifo,acc_nonwakeup_fifo,prox_wakeup_fifo,prox_nonwakeup_fifo) + "\n\n"
                        + "Do you want to learn about your device? Download the app: \n\n"
                        + appLink + "\n\n"
                        + "Or get the code on SnabpackLabs repository on GitHub:\n\n"
                        + gitLink + "\n\n"
                        + "Please note that it works only on Android 4.4+ (KitKat and later)\n"
                        + "Enjoy!\n\n"
                        + "The Snapback Team\n"
                        + "www.snapback.io\n"
                        + slidesLink;
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, subjectText);
                i.putExtra(Intent.EXTRA_TEXT   , bodyText);
                try {
                    startActivity(Intent.createChooser(i, "Send text..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "There are no text clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    private Sensor getDefaultSensor(SensorManager mgr, int type, boolean wakeUp) {
        Sensor sensor = null;
        if(Build.VERSION.SDK_INT > 20) {
            sensor = getDefaultSensor21(mgr, type, wakeUp);
        }
        else {
            if ((type == Sensor.TYPE_ACCELEROMETER) && (!wakeUp)) {
                sensor = mgr.getDefaultSensor(type);
            }
            if ( (type == Sensor.TYPE_PROXIMITY) && (wakeUp) ) {
                sensor = mgr.getDefaultSensor(type);
            }
        }
        return sensor;
    }

    @TargetApi(21)
    private Sensor getDefaultSensor21(SensorManager mgr, int type, boolean wakeUp) {
        return mgr.getDefaultSensor(type, wakeUp);
    }

    @TargetApi(19)
    private int checkFifoReserved19(Sensor sensor) {
        return sensor.getFifoReservedEventCount();
    }

    private int checkFifoReserved(Sensor sensor, TextView textView) {
        int fifoReserved = FIFO_ERROR_NO_SENSOR;
        if (sensor != null) {
            if(Build.VERSION.SDK_INT >= 19 ) {
                fifoReserved = sensor.getFifoReservedEventCount();
                Log.d(LOG_TAG, "batching, max fifo len " + sensor.getFifoMaxEventCount());
            } else {
                fifoReserved = FIFO_ERROR_NO_SUPPORT;
            }
            Log.d(LOG_TAG, "batching, reserved fifo len " + fifoReserved);
        } else {
            Log.d(LOG_TAG, "No sensor");
        }

        textView.setText(fifo2String(fifoReserved));
        return fifoReserved;
    }

    private static String fifo2String(int fifoReserved) {
        String fifoReservedString;
        if (fifoReserved == FIFO_ERROR_NO_SENSOR) {
            fifoReservedString = "N/A";
        } else if (fifoReserved == FIFO_ERROR_NO_SUPPORT) {
            fifoReservedString = "NO (fifo N/A)";
        } else if (fifoReserved == 0) {
            fifoReservedString = "NO (fifo "+fifoReserved+")";
        } else {
            fifoReservedString = "OK (fifo "+fifoReserved+")";
        }
        return fifoReservedString;
    }

    private static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String api = Build.VERSION.SDK_INT + "";
        if (model.startsWith(manufacturer)) {
            return model;
        }
        return manufacturer + " " + model + " (API "+ api +")";
    }

    private static boolean anyStrictlyPositive(int a, int b, int c, int d) {
        boolean ret = false;
        if ( (a > 0) || (b > 0) || (c > 0) || (d > 0) ) {
            ret = true;
        }
        return ret;
    }

    private static String fifos2SupportString(int a, int b, int c, int d) {
        String str = "does not support it!";
        if (anyStrictlyPositive(a,b,c,d) ) {
            str = "supports it! :D";
        }
        return str;
    }
}
