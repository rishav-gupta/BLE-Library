# BLE-Library
A powerful library to search BLE devices and run complex operations to output processed critical parameters after running smoothening and error correction algorithms
## Installation
Step 1: Add it in your root build.gradle at the end of repositories:
```bash
      allprojects {
        repositories {
          ...
          maven { url 'https://jitpack.io' }
        }
      }
```
Step 2: Add the dependency
```bash
dependencies {
	        implementation 'com.github.rishav-gupta:BLE-Library:v1.0'
	}
  ```
  ## Usage
  
1. Implement the interface
  ```bash
public class MainActivity extends AppCompatActivity implements SearchBeacons.onBeaconDiscoveryListener
  ```
  
2. Following method will be implemented
```bash
@Override
    public void onBeaconDiscovery(BeaconModel[] beacons) {
        beacons = searchBeacons.getProcessedBeaconData(beacons);
        for (BeaconModel beacon : beacons) {
           //all the ble beacons can be found here after the error correction
        }
    }
```

3. To start the scanning, use the following snippet
```bash
 if (searchBeacons.checkBLEcompatability()) {                             //checks BLE compatibility
            Toast.makeText(getApplicationContext(),
            "BLE supported", Toast.LENGTH_SHORT).show();
            searchBeacons.BluetoothEnabled(MainActivity.this);            //to enable the bluetooth
            searchBeacons.scanLeDevice(true);                             //to start the scanning                   
        }
```
  
  ## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.
