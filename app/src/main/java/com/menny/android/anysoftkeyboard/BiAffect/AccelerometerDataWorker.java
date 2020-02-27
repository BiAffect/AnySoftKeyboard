package com.menny.android.anysoftkeyboard.BiAffect;

import android.util.Log;

import com.menny.android.anysoftkeyboard.BiAffect.Database.BiADatabaseManager;

public class AccelerometerDataWorker implements Runnable {
    BiAManager sharedInstance;
    BiADatabaseManager mBiADatabaseManager;
    final int FREQUENCY = 10;
    final int oneSecondMillis = 1000;
    public AccelerometerDataWorker(BiADatabaseManager databaseManager){
        super();
        sharedInstance = BiAManager.getInstance(null);
        mBiADatabaseManager = databaseManager;
    }

    @Override
    public void run() {
        while(sharedInstance.sessionRunning){
            long currentTime = System.currentTimeMillis();
            mBiADatabaseManager.insertAccelerometerData(currentTime, sharedInstance.current_accelerometer_x, sharedInstance.current_accelerometer_y, sharedInstance.current_accelerometer_z);
            //Log.i("ACC", "PUSHED");
            try {
                Thread.sleep(oneSecondMillis/FREQUENCY);
            }catch (InterruptedException e){
                //Do nothing
            }
        }
    }
}
