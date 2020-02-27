package com.menny.android.anysoftkeyboard.BiAffect;

import android.util.Log;

public class TouchDataPOJO{
    boolean used;
    long eventDownTime;
    long eventTime;
    String eventAction;
    float pressure;
    float x_cord;
    float y_cord;
    float major_axis;
    float minor_axis;
    float accelerometer_x;
    float accelerometer_y;
    float accelerometer_z;
    int touches;


    public TouchDataPOJO(){
        //I really dont need to initialise anything cos everything is primitive data type
    }

    public boolean markUnused(){
        this.used = false;
        this.eventDownTime=0;
        this.eventTime=0;
        this.eventAction=null;
        this.pressure = 0.0f;
        this.x_cord = 0.0f;
        this.y_cord = 0.0f;
        this.major_axis=0.0f;
        this.minor_axis=0.0f;
        this.accelerometer_x = 0.0f;
        this.accelerometer_y = 0.0f;
        this.accelerometer_z = 0.0f;
        this.touches=-1;

        return true;
    }

    public boolean validatePOJO(){
        return (this.used && this.eventAction!=null && this.touches!=-1);
    }

    public void printYourself(){

        Log.i("CS_BiAffect", "eventDownTime->"+this.eventDownTime);
        Log.i("CS_BiAffect", "eventTime->"+this.eventTime);
        Log.i("CS_BiAffect", "eventAction->"+this.eventAction);
        Log.i("CS_BiAffect", "pressure->"+this.pressure);
        Log.i("CS_BiAffect", "x_cord->"+this.x_cord);
        Log.i("CS_BiAffect", "y_cord->"+this.y_cord);
        Log.i("CS_BiAffect", "major_axis->"+this.major_axis);
        Log.i("CS_BiAffect", "minor_axis->"+this.minor_axis);
        Log.i("CS_BiAffect", "touches->"+this.touches);
        Log.i("CS_BiAffect", "Acc_X->"+this.accelerometer_x);
        Log.i("CS_BiAffect", "Acc_Y->"+this.accelerometer_y);
        Log.i("CS_BiAffect", "Acc_Z->"+this.accelerometer_z);
    }

    //I think i will used event action and touches to check the validity of the pojo class
}