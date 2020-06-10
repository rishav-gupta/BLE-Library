package com.rishavgupta.ble;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class SearchBeacons extends ListActivity {


    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 1200;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private boolean mScanning;
    int rssi;
    public static List<BeaconModel> list = new ArrayList<BeaconModel>();
    public int scanCount = 0;
    int count = 3;
    Boolean b = false;
    Map<String, Float> dic = new HashMap<String, Float>();
    Map<String, Integer> miss = new HashMap<String, Integer>();
    Map<String, String> nameDic = new HashMap<String, String>();
    static Context cnt;


    /**
     * constructor to give this activity the context
     *
     * @param context
     */
    public SearchBeacons(Context context) {
        this.cnt = (Activity) context;
    }


    /**
     * Interface, contains the function which is to be over-ridden by the user to get the beacon data.
     */
    public interface onBeaconDiscoveryListener {
        public void onBeaconDiscovery(BeaconModel[] beacons);
    }


    /**
     * links the array inside the function (which is to be over-ridden) with the object
     *
     * @param objListener
     * @param beaconModels
     */
    private static void callCommand(onBeaconDiscoveryListener objListener, BeaconModel[] beaconModels) {
        objListener.onBeaconDiscovery(beaconModels);
    }


    /**
     * checks if android version is compatible with detecting BLE
     *
     * @return true if compatible
     */
    public boolean checkBLEcompatability() {
        // if not compatible
        if (!cnt.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        //if compatible
        else
            return true;
    }


    /**
     * checks if android version is compatible with bluetooth service
     *
     * @return true if compatible
     */
    public boolean checkBluetoothCompatability() {
        //Access the bluetooth manager
        final BluetoothManager bluetoothManager = (BluetoothManager) cnt.getSystemService(Context.BLUETOOTH_SERVICE);

        //Get the bluetooth adapter
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // if not compatible
        // if compatible
        return mBluetoothAdapter != null;
    }


    /**
     * if bluetooth is not enabled, prompts a message to the user to enable it by clicking a button
     *
     * @param activity
     */
    public void BluetoothEnabled(Activity activity) {
        // Get the bluetooth adapter
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // if not enabled then promt a message
        if (!mBluetoothAdapter.isEnabled()) {
            Intent intentBtEnabled = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // The REQUEST_ENABLE_BT constant passed to startActivityForResult() is a locally defined integer (which must be greater than 0), that the system passes back to you in your onActivityResult()
            // implementation as the requestCode parameter.
            activity.startActivityForResult(intentBtEnabled, REQUEST_ENABLE_BT);
        }
    }


    /**
     * Calls the callback which detects the BLE devices
     *
     * @param enable *
     */
    public void scanLeDevice(final boolean enable) {
        // get the bluetooth adapter
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // create a new handler
        mHandler = new Handler();
        // checks if scanning has to be done
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    // calls to stop the scanning
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    // convert the list containing all the beacons found while scanning, to an array of beacon type
                    BeaconModel[] r = list.toArray(new BeaconModel[0]);
                    // passes that array as a parameter to the function which is to be over-ridden by the user
                    callCommand((onBeaconDiscoveryListener) cnt, r);
                    //clear the array after compiling search results of one search in an array
                    list.removeAll(list);

                }
            }, SCAN_PERIOD);

            // maintains the count of total scans
            scanCount++;
            // to enable scanning
            mScanning = true;
            // call to start the scanning
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
        //if no scanning has to be done
        else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }


    /**
     * @param r returns the processed data to the user with all the devices found
     * @return array of type beacon to the user
     */
    public BeaconModel[] getProcessedBeaconData(BeaconModel[] r) {

       // Log.d("debugDis", "Scan count: " + scanCount + "\nBeacons: " + Arrays.toString(r));

        // checks whether the received array containing all the information about the beacons contains some data
        if (r.length > 0 || dic.size() > 0)
            count++;

        // dictionary which will contain all the new devices
        Map<String, Float> newDevs = new HashMap<String, Float>();


        for (BeaconModel beaconModel : r) {
            //adds the received devices to the nameDic dictionary
            nameDic.put(beaconModel.getMac(), beaconModel.getName());

            // checks if that device is already in the newDevs dictionary
            if (newDevs.containsKey(beaconModel.getMac())) {
                // if it contains, then we have to consider that rssi value of the same beacon which has the least value for more accuracy
                if (newDevs.get(beaconModel.getMac()) < beaconModel.getRssi()) {
                    // the one with the minimum value
                    newDevs.put(beaconModel.getMac(), beaconModel.getRssi());
                }
            }
            // else we have to consider the same rssi value
            else {
                // add the details in the newDevs dictionary
                newDevs.put(beaconModel.getMac(), beaconModel.getRssi());
            }
        }

        // Iterator to iterate in the newDevs dictionary
        Iterator<String> ite = newDevs.keySet().iterator();
        // getting the keyset
        ite = newDevs.keySet().iterator();
        // Run the loop till the last key
        while (ite.hasNext()) {
            // to store the current key
            String key = ite.next();
            // if found, remove the key from the miss dictionary(used to add those devices which are not found during the scanning. but found previosuly)
            miss.remove(key);
            // if the dic dictionary contains the key, apply the rolling average over the rssi
            if (dic.containsKey(key)) {
                // rolling average
                float rolling_avg = ((count * dic.get(key)) + newDevs.get(key)) / (count + 1);
                // update the dictionary
                dic.put(key, rolling_avg);
            }
            // if key not found in the dic dictionary then get the rssi from the newDevs dictionary
            else {
                dic.put(key, newDevs.get(key));
            }

        }


        // Iterator to iterate through the dic dictionary
        // loop till the last key of the dictionary
        for (String key : dic.keySet()) {
            // stores the current key
            // if the device is not found in the current scan
            if (!newDevs.containsKey(key)) {
                // and the device is not in the miss dictionary
                if (miss.containsKey(key)) {
                    // add the device to the miss dictionary, and update the miss value(increments by 1)
                    miss.put(key, miss.get(key) + 1);
                }
                // if no miss
                else {
                    // then the miss value is updated by 1
                    miss.put(key, 1);
                }
            }
        }



        // Iterator to iterate through the miss dictionary
        Iterator<String> missIt = miss.keySet().iterator();
        // loop till the last key of the dictionary
        while (missIt.hasNext()) {
            // stores the current value of the key
            String key = missIt.next();
            // if miss value in the dictionry > 2, that is more than 2 misses in a row
            if (miss.get(key) > 2) {
                // remove that device from the the nameDic dictionary
                nameDic.remove(key);
                // remove from the iterator
                missIt.remove();
                // remove that device from the dic dictionary
                dic.remove(key);
            }
        }


        // stores the processed data in a list of beacon type
        List<BeaconModel> finalList = new ArrayList<BeaconModel>();

        // iterator to iterate through dic dictionary
        // loop through the last key
        for (String mac : dic.keySet()) {
            // get mac from the key
            // calibrated transmission power ro calculate distance
            double txPower = -81.38f;
            // find the distance
            float Distance = (float) Math.pow(10, ((double) dic.get(mac) - txPower) / -(10 * 2));

            finalList.add(new BeaconModel(nameDic.get(mac), mac, Math.round(dic.get(mac)),
                    Distance + 0.10f, miss.containsKey(mac)));

        }

        // array to convert the list with processed list
        BeaconModel[] b = finalList.toArray(new BeaconModel[0]);

        Log.d("finalL", Arrays.toString(b));

        // returns the array to the user
        return b;
    }


    /**
     * callback which is called from scanLeDevice, detects the devices and adds them to the list.
     */
    public BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {


                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    // calibrated power
                    double txPower = -81.38f;
                    SearchBeacons.this.rssi = rssi;
                    // calculate the distance
                    final float Distance = (float) Math.pow(10, ((double) rssi - txPower) / -(10 * 2));
                    // add to the unprocessed list
                    list.add(new BeaconModel(device.getName(), device.getAddress(), SearchBeacons.this.rssi, Distance + 0.10f, false));

                }
            };
}
