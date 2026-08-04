package com.eshelon.prizma_prev.objects;

import static java.lang.String.valueOf;

public class BtDevData {
    private boolean deviceSelected = false;
    private String name="";
    private String mac="";
    private String devNum="";
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getNum() {
        return devNum;
    }

    public void setNum(int devNum) {
        this.devNum = valueOf(devNum);
    }


    public boolean isDeviceSelected() {
        return deviceSelected;
    }

    public void setDeviceSelected(boolean deviceSelected) {
        this.deviceSelected = deviceSelected;
    }
}
