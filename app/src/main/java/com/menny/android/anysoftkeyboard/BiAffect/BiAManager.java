package com.menny.android.anysoftkeyboard.BiAffect;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.menny.android.anysoftkeyboard.AnyApplication;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

public class BiAManager implements BiADataProcessorInterface.TouchDataProcessorInterface, BiADataProcessorInterface, SensorEventListener {

    private BiAWorker1 myCurrentWorker1;

    //Two data structures to hold the master records,
    static final int TOUCH_BUFFER_SIZE = 20;
    TouchDataPOJO[] t1;
    TouchDataPOJO[] t2;
    boolean bucket1;
    int currentIndex;
    Semaphore t1_Sempahore = new Semaphore(1);
    Semaphore t2_Sempahore = new Semaphore(1);

    //Sensor specific data holders
    float current_accelerometer_x;
    float current_accelerometer_y;
    float current_accelerometer_z;

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

    //Instead of inner static class we can also use enum
    static class FeatureLookupStruct{
        final static String pressure = "PRESSURE";
        //final static String action_key_up = "Up";
        //final static String action_key_down = "Down";
    }

    private static BiAManager shared_instance = null;
    ArrayBlockingQueue<BiAFeature> myTupleQueue;
    LinkedHashMap<Long, BiAPOJO> processingMap;
    LinkedHashMap<Long, BiAPOJO> finalPOJOMap;


    private BiAManager(){
        //This wont contain anything as such
        this.myTupleQueue = new ArrayBlockingQueue<>(10000);
        this.processingMap = new LinkedHashMap<>();
        this.finalPOJOMap = new LinkedHashMap<>();

        //Initialising all the buffers
        this.t1 = new TouchDataPOJO[TOUCH_BUFFER_SIZE];
        this.t2 = new TouchDataPOJO[TOUCH_BUFFER_SIZE];
        this.bucket1=true;
        for(int i=0; i<TOUCH_BUFFER_SIZE; i++){
            this.t1[i] = new TouchDataPOJO();
            this.t2[i] = new TouchDataPOJO();
        }
    }

    public static synchronized BiAManager getInstance()
    {
        if (shared_instance == null)
            shared_instance = new BiAManager();

        return shared_instance;
    }

    @Override
    public boolean recordKeyPressForce(long eventDownTime, double pressure, int action) throws NullPointerException, InterruptedException{
        System.out.println("BiAffect recordKeyPressForce received for "+eventDownTime+ " For action "+ action);
        Pressure myPressure = new Pressure(FeatureLookupStruct.pressure, eventDownTime, pressure, action);
        shared_instance.myTupleQueue.put(myPressure);
        return true;
    }

    //Session specific calls
    public boolean startSession(){
        myCurrentWorker1 = new BiAWorker1();
        //not sure if i can start the worker thread here
        myCurrentWorker1.start();
        return true;
    }

    public boolean endSession(){

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

        //lock the buffer, if lock fails, there is something wrong with the code
        try {
            temp_Semaphore.acquire();
            temp[currentIndex].eventDownTime = eventDownTime;
            temp[currentIndex].eventTime = eventTime;
            temp[currentIndex].eventAction = eventAction;
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
                    Thread t = new Thread(new TouchDataWorker(this.bucket1));
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
            Log.i("CS_BiAffect_App_context",AnyApplication.getAppContext().toString());
        }

        return false;
    }
}