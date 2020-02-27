package com.menny.android.anysoftkeyboard.BiAffect;

import android.util.Log;

import com.menny.android.anysoftkeyboard.BiAffect.Database.BiADatabaseManager;

import java.util.concurrent.Semaphore;

public class KeyDataWorker implements Runnable {

    boolean bucketk1;
    BiAManager sharedInstance;
    BiADatabaseManager mBiADatabaseManager;
    public KeyDataWorker(boolean bucketk1, BiADatabaseManager databaseManager){
        super();
        this.bucketk1=bucketk1;
        mBiADatabaseManager = databaseManager;
    }

    @Override
    public void run() {
        sharedInstance = BiAManager.getInstance(null);
        KeyDataPOJO[] temp;
        Semaphore temp_Semaphore;
        if(this.bucketk1){
            //Need to lock the first bucket
            temp = sharedInstance.k1;
            temp_Semaphore = sharedInstance.k1_Semaphore;
        }else{
            //lock second bucket
            temp = sharedInstance.k2;
            temp_Semaphore = sharedInstance.k2_Semaphore;
        }

        //The rest will proceed as it is
        try {
            temp_Semaphore.acquire();
            Log.i("CS_BiAffect_K","---------KEY BUFFER EMPTY START-----------"+this.bucketk1);
            for(KeyDataPOJO k:temp){
                if(k.used && k.validatePOJO()){
                    mBiADatabaseManager.insertKeyData(k.eventDownTime, k.keyType, k.keyCentre_X, k.keyCentre_Y, k.keyWidth, k.keyHeight);
                    k.markUnused();
                }
            }
        }catch (InterruptedException e){

        }finally {
            temp_Semaphore.release();
            Log.i("CS_BiAffect_K","---------KEY BUFFER EMPTY END-----------"+this.bucketk1);

        }
    }
}
