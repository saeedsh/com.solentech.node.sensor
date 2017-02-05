package com.solentech.node.sensor;

public class Tag {

    private short rn16;
    private  short pc;
    private  byte[] epc;
    private byte   epclen;
    private short   handle;
    private byte   rssi;
    private int   count;


    @Override
    public String toString()
    {
        String result = "EPC => ";
        for (int j = 0; j < getEpclen(); j++) {
            result += String.format("%02x", 0xff & getEpc()[j]);
        }
        result += " PC=" + String.format("%02x", getPc());
        result += " rssi=" + (0xff & getRssi());
        result += " count=" + getCount();
        result += " handle=" + getHandle();
        return result;
    }


    public short getRn16() {
        return rn16;
    }

    public void setRn16(short rn16) {
        this.rn16 = rn16;
    }

    public short getPc() {
        return pc;
    }

    public void setPc(short pc) {
        this.pc = pc;
    }

    public byte[] getEpc() {
        return epc;
    }

    public void setEpc(byte[] epc) {
        this.epc = epc;
    }

    public byte getEpclen() {
        return epclen;
    }

    public void setEpclen(byte epclen) {
        this.epclen = epclen;
    }

    public short getHandle() {
        return handle;
    }

    public void setHandle(short handle) {
        this.handle = handle;
    }

    public byte getRssi() {
        return rssi;
    }

    public void setRssi(byte rssi) {
        this.rssi = rssi;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
