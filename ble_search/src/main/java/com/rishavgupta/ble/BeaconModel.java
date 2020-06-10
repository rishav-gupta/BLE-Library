package com.rishavgupta.ble;

/**
 * Created by Rishav Gupta
 */
public class BeaconModel {
    private String name;

    private String mac;

    private float rssi;

    private float distance;

    private boolean miss;


    /**
     * Constructor to initialise the values
     *
     * @param n  name of the ble
     * @param s  mac of the ble
     * @param c  rssi of the ble
     * @param st distance as calculated from the rssi
     * @param m  miss : true if ble was missed and rssi was carried forward from the previous value
     */
    BeaconModel(String n, String s, int c, float st, boolean m) {
        name = n;
        mac = s;
        rssi = c;
        distance = st;
        miss = m;
    }

    /**
     * Function to get all the details of the beacon found
     *
     * @return the details of the beacon found
     */
    public String toString() {
        return "\n Name: " + name + "\n " + "MAC: " + mac + "\n " + "RSSI: " + rssi + "\n " + "Distance: " + distance + "\n " + "Miss: " + miss + "\n";
    }

    /**
     * To get the rssi of the beacon
     *
     * @return rssi
     */
    public float getRssi() {
        return rssi;
    }


    /**
     * To get the name of the beacon
     *
     * @return name
     */
    public String getName() {
        return name;
    }


    /**
     * To get the mac of the beacon
     *
     * @return mac
     */
    public String getMac() {
        return mac;
    }


    /**
     * To get the distance of the beacon from the user
     *
     * @return distance
     */
    public float getDistance() {
        return distance;
    }


    /**
     * To check whether there was a miss or a hit in finding the beacon
     *
     * @return miss
     */
    public boolean getMiss() {
        return miss;
    }


    /**
     * To set the rssi of the beacon
     * @param rssi
     */
    public void setRssi(float rssi) {
        this.rssi = rssi;
    }


    /**
     * To set the name of the beacon
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * To set the mac of the beacon
     * @param mac
     */
    public void setMac(String mac) {
        this.mac = mac;
    }


    /**
     * To set the distance of the beacon
     * @param dis
     */
    public void setDistance(float dis) {
        this.distance = dis;
    }

    /**
     * To set the miss flag of the beacon
     * @param b
     */
    public void setMiss(boolean b) {
        this.miss = b;
    }

}
