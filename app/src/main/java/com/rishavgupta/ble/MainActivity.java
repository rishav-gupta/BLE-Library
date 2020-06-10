package com.rishavgupta.ble;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SearchBeacons.onBeaconDiscoveryListener {
    TextView t;
    int i = 0;

    SearchBeacons searchBeacons = new SearchBeacons(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t = (TextView) findViewById(R.id.textView);

        if (searchBeacons.checkBLEcompatability()) {
            Toast.makeText(getApplicationContext(), "BLE supported", Toast.LENGTH_SHORT).show();
            searchBeacons.BluetoothEnabled(MainActivity.this);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (i < 100) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                searchBeacons.scanLeDevice(true);
                            }
                        });
                        i++;
                        try {
                            Thread.sleep(1230);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        }
    }


    @Override
    public void onBeaconDiscovery(BeaconModel[] beacons) {
        beacons = searchBeacons.getProcessedBeaconData(beacons);
        for (BeaconModel beacon : beacons) {
            t.append(beacon.toString() + "\n");
        }
    }

}