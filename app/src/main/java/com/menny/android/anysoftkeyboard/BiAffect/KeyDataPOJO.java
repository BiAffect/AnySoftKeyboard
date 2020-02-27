package com.menny.android.anysoftkeyboard.BiAffect;

import android.util.Log;

public class KeyDataPOJO {

    boolean used;
    long eventDownTime;
    long eventUpTime;
    String keyType;
    float keyCentre_X;
    float keyCentre_Y;
    float keyWidth;
    float keyHeight;

    public KeyDataPOJO(){

    }

    public boolean markUnused(){
        this.used = false;
        this.eventDownTime=-1;
        this.eventUpTime=-1;
        this.keyType=null;
        this.keyCentre_X=-1;
        this.keyCentre_Y=-1;
        this.keyWidth=-1;
        this.keyHeight=-1;

        return true;
    }

    public boolean validatePOJO(){
        return (this.used && this.eventDownTime!=-1);
    }

    public void printYourself(){
        Log.i("CS_BiAffect_K", "eventDownTime->"+this.eventDownTime);
        Log.i("CS_BiAffect_K", "eventUpTime->"+this.eventUpTime);
        Log.i("CS_BiAffect_K", "keyId->"+this.keyType);
        Log.i("CS_BiAffect_K", "keyCentre_X->"+this.keyCentre_X);
        Log.i("CS_BiAffect_K", "keyCentre_Y->"+this.keyCentre_Y);
        Log.i("CS_BiAffect_K", "keyWidth->"+this.keyWidth);
        Log.i("CS_BiAffect_K", "keyHeight->"+this.keyHeight);
    }

}
