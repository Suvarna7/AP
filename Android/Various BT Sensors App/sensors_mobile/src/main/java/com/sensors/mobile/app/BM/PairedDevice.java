/**
 * Copyright (c) 2015, BodyMedia Inc. All Rights Reserved
 */

package com.sensors.mobile.app.BM;

public class PairedDevice {

    /** Serial number of the device. */
    private String serialNumber;
    /** Pairing key that is used to connect to the device. */
    private byte[] pairingKey;

    public PairedDevice(String serialNumber, final byte[] pairingKey) {
        this.serialNumber = serialNumber;
        this.pairingKey = pairingKey;
    }

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public  byte[] getPairingKey() {
        return this.pairingKey;
    }
}
