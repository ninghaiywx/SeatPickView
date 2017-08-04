package com.example.ywx.mylibrary;

/**
 * Created by ywx on 2017/8/4.
 * 座位bean类
 */

public class SeatInfo {
    private int sid;
    private int fid;
    private int leftside;
    private String seatnumber;
    private int row;
    private int colums;
    private int statue;
    private int seatstatue;
    private boolean isChoose;

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public int getLeftside() {
        return leftside;
    }

    public void setLeftside(int leftside) {
        this.leftside = leftside;
    }

    public String getSeatnumber() {
        return seatnumber;
    }

    public void setSeatnumber(String seatnumber) {
        this.seatnumber = seatnumber;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColums() {
        return colums;
    }

    public void setColums(int colums) {
        this.colums = colums;
    }

    public int getStatue() {
        return statue;
    }

    public void setStatue(int statue) {
        this.statue = statue;
    }

    public int getSeatstatue() {
        return seatstatue;
    }

    public void setSeatstatue(int seatstatue) {
        this.seatstatue = seatstatue;
    }
}
