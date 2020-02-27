package com.menny.android.anysoftkeyboard.BiAffect;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;

import com.menny.android.anysoftkeyboard.BiAffect.Database.BiADatabaseManager;
import com.menny.android.anysoftkeyboard.BiAffect.Database.BiAffect_Database;
import com.menny.android.anysoftkeyboard.BiAffect.Database.Models.SessionData;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

public class BiAManager implements BiADataProcessorInterface.TouchDataProcessorInterface,
        BiADataProcessorInterface, SensorEventListener,
        BiADataProcessorInterface.KeyDataProcessorInterface,
        BiADataProcessorInterface.SessionDataProcessorInterface,
        BiADataProcessorInterface.DeviceDataProcessorInterface
{

    //Context specific holders
    Context mContext;
    //Two data structures to hold the master records,
    static final int TOUCH_BUFFER_SIZE = 20;
    TouchDataPOJO[] t1;
    TouchDataPOJO[] t2;
    boolean bucket1;
    int currentIndex;
    Semaphore t1_Sempahore = new Semaphore(1);
    Semaphore t2_Sempahore = new Semaphore(1);
    //Sensor specific data holders
    private SensorManager mSensorManager;
    float current_accelerometer_x;
    float current_accelerometer_y;
    float current_accelerometer_z;
    //Key Data specific data holders
    static final int KEY_BUFFER_SIZE = 20;
    KeyDataPOJO[] k1;
    KeyDataPOJO[] k2;
    boolean bucketk1;
    int currentIndexKey;
    Semaphore k1_Semaphore = new Semaphore(1);
    Semaphore k2_Semaphore = new Semaphore(1);

    //Session specific data
    long currentRunningSession;
    boolean sessionRunning;

    //Database
    BiADatabaseManager mBiADatabaseManager;

    //TIME OFFSETS
    long birthTime;
    long upTimeAtBirth;
    long offset;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            current_accelerometer_x = event.values[0];
            current_accelerometer_y = event.values[1];
            current_accelerometer_z = event.values[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private static BiAManager shared_instance = null;

    private BiAManager(Context context){
        this.birthTime = System.currentTimeMillis();
        this.upTimeAtBirth = SystemClock.uptimeMillis();
        this.offset = birthTime - upTimeAtBirth;

        this.mContext = context;
        //This will initialise the dbManager when the constructor of BiAManager is called...
        //This wont contain anything as such

        //Initialising all the buffers
        this.t1 = new TouchDataPOJO[TOUCH_BUFFER_SIZE];
        this.t2 = new TouchDataPOJO[TOUCH_BUFFER_SIZE];
        this.bucket1=true;
        for(int i=0; i<TOUCH_BUFFER_SIZE; i++) {
            this.t1[i] = new TouchDataPOJO();
            this.t2[i] = new TouchDataPOJO();
        }

        //Initialising keyDataBuffers
        this.k1 = new KeyDataPOJO[KEY_BUFFER_SIZE];
        this.k2 = new KeyDataPOJO[KEY_BUFFER_SIZE];
        this.bucketk1=true;
        for (int i=0; i<KEY_BUFFER_SIZE; i++){
            this.k1[i] = new KeyDataPOJO();
            this.k2[i] = new KeyDataPOJO();
        }

        //Alright i am able to recevice the context, now what ? should i use a service which will basically do sampling ?
        //For now lets stick to the logic and may be later we can insert it into a dedicated service which will sample the sensor data
        this.mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);

        //This should be done when the session starts and undone when the session ends
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 10);

        //DB
        mBiADatabaseManager = BiADatabaseManager.getInstance(mContext);

        //call to send the device data to db
        sendDeviceData();
    }

    @Override
    public void sendDeviceData(){
        //Log.i("DEVICEDATA", "CALLING");
        Thread temp = new Thread(new Runnable() {
            @Override
            public void run() {
                mBiADatabaseManager.insertDeviceData(mContext);
            }
        });
        temp.start();
    }

    public static synchronized BiAManager getInstance(Context mContext)
    {
        if(mContext!=null){
            if (shared_instance == null)
                shared_instance = new BiAManager(mContext);
            //This will give in a provision to change the context may be at a later stage
            shared_instance.mContext = mContext;
        }
        return shared_instance;
    }

    //Session specific calls
    @Override
    public boolean startSession(){
        //check once.. I dont think the event time is stored as milli sec
        Log.i("CS_BiAffect_Sess","-----------Start SESSION START-------------");
        Log.i("CS_BiAffect_Sess",Log.getStackTraceString(new Exception()));

        this.sessionRunning = true;
        this.currentRunningSession = System.currentTimeMillis();
        Log.i("CS_BiAffect_Sess","startTime -> "+currentRunningSession);

        Thread accelerometerDataCollector = new Thread(new AccelerometerDataWorker(mBiADatabaseManager));
        accelerometerDataCollector.start();

        Thread temp = new Thread(new Runnable() {
            @Override
            public void run() {
                mBiADatabaseManager.insertSessionData(currentRunningSession);
            }
        });
        temp.start();
        Log.i("CS_BiAffect_Sess","-----------Start SESSION End-------------");
        return true;
    }

    @Override
    public boolean endSession(){
        if(!this.sessionRunning) return false;
        Log.i("CS_BiAffect_Sess","-----------END SESSION START-------------");
        Log.i("CS_BiAffect_Sess",Log.getStackTraceString(new Exception()));
        this.sessionRunning = false;
        long sessionEndTime = System.currentTimeMillis();
        Thread temp = new Thread(new Finaliser(this.currentRunningSession, sessionEndTime, mBiADatabaseManager));
        temp.start();
        Log.i("CS_BiAffect_Sess","-----------END SESSION END-------------");
        return true;
    }

    //Touch Data calls
    @Override
    public boolean addMasterEntry(long eventDownTime, long eventTime, int eventAction, float pressure, float x_cord, float y_cord, float major_axis, float minor_axis, int touches){
        TouchDataPOJO[] temp;
        Semaphore temp_Semaphore;
        //assigning correct buffer;
        if(this.bucket1){
            //t1 is supposed to be used
            temp = this.t1;
            temp_Semaphore = t1_Sempahore;
        }else{
            temp = this.t2;
            temp_Semaphore = t2_Sempahore;
        }

        String event = "";
        if(eventAction == 0){
            event = "Action_Down";
        }
        else if(eventAction == 1){
            event = "Action_Up";
        }
        else{
            event = "Action_Move";
        }

        //lock the buffer, if lock fails, there is something wrong with the code
        try {
            temp_Semaphore.acquire();
            temp[currentIndex].eventDownTime = eventDownTime + offset;
            temp[currentIndex].eventTime = eventTime + offset;
            temp[currentIndex].eventAction = event;
            temp[currentIndex].pressure = pressure;
            temp[currentIndex].x_cord = x_cord;
            temp[currentIndex].y_cord = y_cord;
            temp[currentIndex].major_axis = major_axis;
            temp[currentIndex].minor_axis = minor_axis;
            temp[currentIndex].touches = touches;
            temp[currentIndex].accelerometer_x = current_accelerometer_x;
            temp[currentIndex].accelerometer_y = current_accelerometer_y;
            temp[currentIndex].accelerometer_z = current_accelerometer_z;
            temp[currentIndex].used = true;
            Log.i("CS_BiAffect","---------------------------------");
            Log.i("CS_BiAffect","Index->"+currentIndex);
            temp[currentIndex].printYourself();
            Log.i("CS_BiAffect","---------------------------------");
            //Log.i("CS_BiAffect_App_context",AnyApplication.getAppContext().toString());

        }catch(InterruptedException e){
            Log.i("CS_BiAffect", "failed to acquire lock on semaphore");
        }finally {
            temp_Semaphore.release();
            if(temp[currentIndex].used){
                if(currentIndex == temp.length-1){
                    Log.i("CS_BiAffect","-----------BUFFER CHANGE-------------"+this.bucket1);
                    //We can kickoff a worker thread from here to take all the pojos and insert it into the database
                    //We will pass the number of the last buffer being used and then expect the thread to infer from that which one
                    //needs to be emptied
                    Thread t = new Thread(new TouchDataWorker(this.bucket1, mBiADatabaseManager));
                    t.start();
                    //Time to change the buffer and put all the things in the second from next
                    this.currentIndex = 0;
                    if(this.bucket1){
                        bucket1=false;
                    }else{
                        bucket1=true;
                    }
                }else{
                    currentIndex++;
                }
            }
        }
        return true;
    }

    public boolean addlog(long eventDownTime, long eventTime, int eventAction, float pressure, float x_cord, float y_cord, float major_axis, float minor_axis, int touches){
        Log.d("Autosuggestion", "eventDownTime:" + eventDownTime);
        Log.d("Autosuggestion", "eventAction:" + eventAction);
        Log.d("Autosuggestion", "pressure:" + pressure);
        Log.d("Autosuggestion", "x_cord" + x_cord);
        Log.d("Autosuggestion", "y_cord:" + y_cord);
        Log.d("Autosuggestion", "major_axis:" + major_axis);
        Log.d("Autosuggestion", "minor_axis" + minor_axis);

       return true;
    }

    //Key data specific calls
    @Override
    public boolean addKeyDataOnlyDownTime(long eventDownTime, int keyCode, float keyCentre_X, float keyCentre_Y, float keyWidth, float keyHeight) {
        KeyDataPOJO[] temp;
        Semaphore temp_Semaphore;
        String keyType = null;

        // Inorder to protect the users' privacy, instead of saving the keyType code, we save the category of the keyType
        // Here are the categories of the key codes
        // Alpha-Numeric
        // Space
        // Backspace
        // switchToNumber
        // switchToLetter
        // Punctuations (.?!,:;-[]{}()'")
        // Symbols (emojis etc)

//        Log.d("code", keyCode + "");
        // Alpha-Numeric
        if ((keyCode >= 48 && keyCode <= 57) || (keyCode >= 97 && keyCode <= 122)) {
            keyType = new String("Alpha-Numeric");
        }
        // Space
        else if (keyCode == 32) {
            keyType = new String("Space");
        // Backspace
        } else if (keyCode == -5) {
            keyType = new String("Backspace");
        // switch to Numbers
        } else if (keyCode == -2) {
            keyType = new String("switchToNumber");
        // switch to Letters
        } else if (keyCode == - 99) {
            keyType = new String("switchToLetter");
        // Punctuations (.?!,:;-[]{}()'")
        } else if ((keyCode >= 33 && keyCode <= 34) || (keyCode >= 39 && keyCode <= 41) || (keyCode >= 44 && keyCode <= 46) || (keyCode >= 58 && keyCode <= 59)
                || keyCode == 63 || keyCode == 91 || keyCode == 93 || keyCode == 123 || keyCode == 125) {
            keyType = new String("Punctuations");
        }
        // Symbols
        else {
            keyType = new String("Symbols");
        }

        //assigning correct buffer;
        if(this.bucketk1){
            //t1 is supposed to be used
            temp = this.k1;
            temp_Semaphore = k1_Semaphore;
        }else{
            temp = this.k2;
            temp_Semaphore = k2_Semaphore;
        }
        //Lock the semaphore
        try {
            temp_Semaphore.acquire();
            temp[currentIndexKey].eventDownTime = eventDownTime + offset;
            temp[currentIndexKey].eventUpTime = 0 + offset;
            temp[currentIndexKey].keyType = keyType;
            temp[currentIndexKey].keyCentre_X = keyCentre_X;
            temp[currentIndexKey].keyCentre_Y = keyCentre_Y;
            temp[currentIndexKey].keyWidth = keyWidth;
            temp[currentIndexKey].keyHeight = keyHeight;
            temp[currentIndexKey].used = true;
            Log.i("CS_BiAffect_K","---------------------------------");
            Log.i("CS_BiAffect_K","Index->"+currentIndexKey);
            temp[currentIndexKey].printYourself();
            Log.i("CS_BiAffect_K","---------------------------------");

        }catch (InterruptedException e){
            Log.i("CS_BiAffect_K", "failed to acquire lock on semaphore");
        }finally {
            temp_Semaphore.release();
            if(temp[currentIndexKey].used){
                if(currentIndexKey == KEY_BUFFER_SIZE-1){
                    //Time to switch the key buffer
                    Log.i("CS_BiAffect_K","-----------KEY BUFFER CHANGE-------------"+this.bucketk1);
                    //We can kickoff a worker thread from here to take all the pojos and insert it into the database
                    //We will pass the number of the last buffer being used and then expect the thread to infer from that which one
                    //needs to be emptied
                    Thread t = new Thread(new KeyDataWorker(this.bucketk1, mBiADatabaseManager));
                    t.start();
                    //Time to change the buffer and put all the things in the second from next
                    this.currentIndexKey = 0;
                    if(this.bucketk1){
                        bucketk1=false;
                    }else{
                        bucketk1=true;
                    }
                }else{
                    currentIndexKey++;
                }
            }

        }
        return false;
    }

    public boolean addKeyDataOnlyAuto(long eventDownTime, float keyCentre_X, float keyCentre_Y, float keyWidth, float keyHeight) {
        KeyDataPOJO[] temp;
        Semaphore temp_Semaphore;
        String keyType = "AutoSuggestion";




        //assigning correct buffer;
        if(this.bucketk1){
            //t1 is supposed to be used
            temp = this.k1;
            temp_Semaphore = k1_Semaphore;
        }else{
            temp = this.k2;
            temp_Semaphore = k2_Semaphore;
        }
        //Lock the semaphore
        try {
            temp_Semaphore.acquire();
            temp[currentIndexKey].eventDownTime = eventDownTime + offset;
            temp[currentIndexKey].eventUpTime = 0 + offset;
            temp[currentIndexKey].keyType = keyType;
            temp[currentIndexKey].keyCentre_X = keyCentre_X;
            temp[currentIndexKey].keyCentre_Y = keyCentre_Y;
            temp[currentIndexKey].keyWidth = keyWidth;
            temp[currentIndexKey].keyHeight = keyHeight;
            temp[currentIndexKey].used = true;
            Log.i("CS_BiAffect_K","---------------------------------");
            Log.i("CS_BiAffect_K","Index->"+currentIndexKey);
            temp[currentIndexKey].printYourself();


        }catch (InterruptedException e){
            Log.i("CS_BiAffect_K", "failed to acquire lock on semaphore");
        }finally {
            temp_Semaphore.release();
            if(temp[currentIndexKey].used){
                if(currentIndexKey == KEY_BUFFER_SIZE-1){
                    //Time to switch the key buffer
                    Log.i("CS_BiAffect_K","-----------KEY BUFFER CHANGE-------------"+this.bucketk1);
                    //We can kickoff a worker thread from here to take all the pojos and insert it into the database
                    //We will pass the number of the last buffer being used and then expect the thread to infer from that which one
                    //needs to be emptied
                    Thread t = new Thread(new KeyDataWorker(this.bucketk1, mBiADatabaseManager));
                    t.start();
                    //Time to change the buffer and put all the things in the second from next
                    this.currentIndexKey = 0;
                    if(this.bucketk1){
                        bucketk1=false;
                    }else{
                        bucketk1=true;
                    }
                }else{
                    currentIndexKey++;
                }
            }

        }
        return false;
    }


}