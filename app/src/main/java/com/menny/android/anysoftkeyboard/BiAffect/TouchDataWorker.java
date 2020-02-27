package com.menny.android.anysoftkeyboard.BiAffect;

import android.util.Log;

import com.menny.android.anysoftkeyboard.BiAffect.Database.BiADatabaseManager;

import java.util.concurrent.Semaphore;

public class TouchDataWorker implements Runnable {
    boolean bucket1;
    BiAManager sharedInstance;
    BiADatabaseManager mBiADatabaseManager;
    TouchDataWorker(boolean bucket1, BiADatabaseManager databaseManager){
        super();
        this.bucket1 = bucket1;
        mBiADatabaseManager = databaseManager;
    }

    @Override
    public void run() {
        sharedInstance = BiAManager.getInstance(null);
        TouchDataPOJO[] temp;
        Semaphore temp_Sempaphore;
        if(bucket1){
            temp = sharedInstance.t1;
            temp_Sempaphore = sharedInstance.t1_Sempahore;
        }else{
            temp = sharedInstance.t2;
            temp_Sempaphore = sharedInstance.t2_Sempahore;
        }
        try {
            temp_Sempaphore.acquire();
            Log.i("CS_BiAffect","-----------BUFFER EMPTY START-------------"+this.bucket1);
            for(TouchDataPOJO data:temp) {
                if (data.used && data.validatePOJO()) {
                    //Ready to be used inside the code
                    mBiADatabaseManager.insertTouchData(data.eventDownTime, data.eventTime, data.eventAction, data.pressure, data.x_cord, data.y_cord, data.major_axis, data.minor_axis);
                    data.markUnused();
                }
            }
        }catch (InterruptedException e){

        }finally {
            temp_Sempaphore.release();
            Log.i("CS_BiAffect","-----------BUFFER EMPTY END-------------"+this.bucket1);
        }
    }
}
