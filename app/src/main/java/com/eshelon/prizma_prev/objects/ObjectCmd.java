package com.eshelon.prizma_prev.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ObjectCmd {
    @SerializedName("cmd")
    @Expose
    private int cmd = 0;

    @SerializedName("mod_code")
    @Expose
    private int modCode = 0;

    @SerializedName("mask")
    @Expose
    private int mask = 0;

    @SerializedName("rw")
    @Expose
    private int rw = 0;

    @SerializedName("val")
    @Expose
    private int val = 0;

    @SerializedName("settings")
    @Expose
    private String settings = "";

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public int getModCode() {
        return modCode;
    }

    public void setModCode(int modCode) {
        this.modCode = modCode;
    }

    public int getMask() {
        return mask;
    }

    public void setMask(int mask) {
        this.mask = mask;
    }

    public int getRw() {
        return rw;
    }

    public void setRw(int rw) {
        this.rw = rw;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }



}
