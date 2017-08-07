package com.example.ywx.mylibrary;

/**
 * Created by ywx on 2017/8/4.
 * 点击座位监听器
 */

public interface OnSelectListener {
    void onSelect(SeatInfo seatInfo);
    void onRemove(SeatInfo seatInfo);
}
