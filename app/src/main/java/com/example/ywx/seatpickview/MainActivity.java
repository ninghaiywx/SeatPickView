package com.example.ywx.seatpickview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ywx.mylibrary.SeatInfo;
import com.example.ywx.mylibrary.SeatPickView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SeatPickView pick;
    private List<SeatInfo>list=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pick=(SeatPickView)findViewById(R.id.pick);
        for(int i=0;i<10;i++){
               SeatInfo seatInfo=new SeatInfo();
                seatInfo.setRow(0);
                seatInfo.setColums(i);
                seatInfo.setSeatstatue(0);
                seatInfo.setChoose(false);
                list.add(seatInfo);
            }
        for(int i=0;i<8;i++){
            SeatInfo seatInfo=new SeatInfo();
            seatInfo.setRow(1);
            seatInfo.setColums(i);
            seatInfo.setSeatstatue(1);
            seatInfo.setChoose(false);
            list.add(seatInfo);
        }
        for(int i=0;i<15;i++){
            SeatInfo seatInfo=new SeatInfo();
            seatInfo.setRow(2);
            seatInfo.setColums(i);
            seatInfo.setSeatstatue(0);
            seatInfo.setChoose(false);
            list.add(seatInfo);
        }
        for(int i=0;i<15;i++){
            SeatInfo seatInfo=new SeatInfo();
            seatInfo.setRow(3);
            if(i==3){
                seatInfo.setSeatstatue(3);
            }else {
                seatInfo.setSeatstatue(0);
            }
            seatInfo.setColums(i);
            seatInfo.setChoose(false);
            list.add(seatInfo);
        }
        pick.setSeatList(list);
    }
}
