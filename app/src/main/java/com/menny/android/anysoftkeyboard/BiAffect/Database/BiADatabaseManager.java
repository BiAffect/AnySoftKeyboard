package com.menny.android.anysoftkeyboard.BiAffect.Database;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;

import com.menny.android.anysoftkeyboard.BiAffect.Database.Models.AccelerometerData;
import com.menny.android.anysoftkeyboard.BiAffect.Database.Models.DeviceData;
import com.menny.android.anysoftkeyboard.BiAffect.Database.Models.KeyTypeData;
import com.menny.android.anysoftkeyboard.BiAffect.Database.Models.SessionData;
import com.menny.android.anysoftkeyboard.BiAffect.Database.Models.TouchTypeData;

import java.util.List;

// All data processing work handeled here
// calls to room DB and CRUD operations are called from here
// APIs exposed to Data Collection system are defined here.
public class BiADatabaseManager implements BiADBInterface.TouchDataInterface,BiADBInterface.KeyDataInterface,
BiADBInterface.SessionDataInterface,BiADBInterface.AccelerometerData,BiADBInterface.DeviceData {


    private static BiADatabaseManager sharedInstance;
    private static BiAffect_Database mDatabaseInstance;

    // made BiADatabaseManager Singleton. Only one Instance is available throughout execution and shared by everyone.
    public static synchronized BiADatabaseManager getInstance(Context appContext) {
        if(sharedInstance==null){
            sharedInstance = new BiADatabaseManager(appContext);
        }
        return sharedInstance;
    }

    private BiADatabaseManager(Context appContext) {

        // The Room database instance is created inside constructor.
        // As the constructor will be called only once the Database in turn, has only one instance created
        // This is done to prevent having multiple instances of the database opened at the same time.
        mDatabaseInstance = Room.databaseBuilder(appContext, BiAffect_Database.class,
                "BiAffect_Database.db")
                .fallbackToDestructiveMigration()
                .build();

    }

    //exposed API for processing Session data
    @Override
    public void insertSessionData(long sessionStartTime){

        // Creating an object for Session entity class and initialising the data members
        // This object will be used as parameter for Room @Insert method
        // In essence The object encapsulates the values to be inserted into the table
        SessionData currentSessionData = new SessionData();
        currentSessionData.sessionStartTime = sessionStartTime;

        //  Add a wrapper for the insert() method for Session Table.
        // Insert will throw error is datamembers annotated with @nonnull have null values
        // Insert will throw error is datamembers annotated with @primaryKey is a duplicate
        // thus in try catch block
        try{
            mDatabaseInstance.mSession_dao().insertSessionStartTime(currentSessionData);
        }catch (Exception e)
        {
            // do nothing
        }

    }

    @Override
    public void updateSessionData(long sessionStartTime, long sessionEndTime) {

        // Creating an object for Session entity class and initialising the data members
        // This object will be used as parameter for Room @Insert method
        // In essence The object encapsulates the values to be updated into the table,
        // Room takes the object, extracts value of the data member annotated with @PrimaryKey,
        // finds a record in table with that value for the Primary Key column
        // If a record matches, that row is updated
        SessionData currentSessionData = new SessionData();
        currentSessionData.sessionStartTime = sessionStartTime;
        currentSessionData.sessionEndTime = sessionEndTime;

        //  Add a wrapper for the update() method for Session Table.
        try {
            mDatabaseInstance.mSession_dao().updateSessionEndTime(currentSessionData);
        } catch (Exception e) {
            //do nothing
        }
    }

    /**
     * Gets a list of TouchTypeData associated with a particular keyDownTime.
     *
     * @param time epoch representing the keyDownTime "foreign key"
     * @return List of TouchTypeData associated with the given keyDownTime
     */
    public List<TouchTypeData> getTouchTypeData( long time ) {
        return mDatabaseInstance.mTouch_dao().getTouchTypeData( time );
    }

    //exposed Api for touch data
    @Override
    public void insertTouchData(long eventDownTime, long eventTime, String eventAction, float pressure, float x_cord, float y_cord, float majorAxis, float minorAxis){

        // Creating an object for TouchTypeData entity class and initialising the data members
        // This object will be used as parameter for Room @Insert method
        // In essence The object encapsulates the values to be inserted into the table
        TouchTypeData data = new TouchTypeData();
        data.eventDownTime = eventDownTime;
        data.eventActionTime = eventTime;
        data.eventAction = eventAction;
        data.force = pressure;
        data.touch_xcord = x_cord;
        data.touch_ycord = y_cord;
        data.touch_majorAxis = majorAxis;
        data.touch_minorAxis = minorAxis;

        //  Add a wrapper for the insert() method for TouchTypeData Table.
        // Insert will throw error is datamembers annotated with @nonnull have null values
        // Insert will throw error is datamembers annotated with @primaryKey is a duplicate
        // thus in try catch block
        try{
        mDatabaseInstance.mTouch_dao().insertSingleTouchDataEntry(data);
    }catch (Exception e){
            // do nothing
        }
    }

    /**
     * Gets a list of KeyTypeData that occurred between the start and end times.
     *
     * @param start epoch representing the earliest KeyTypeData returned
     * @param end   epoch representing the latest KeyTypeData returned
     * @return List of KeyTypeData between the start and end times
     */
    public List<KeyTypeData> getKeyTypeData( long start, long end ) {
        return mDatabaseInstance.mKey_dao().getKeyTypeData( start, end );
    }

    //exposed api for keydata
    @Override
    public void insertKeyData(long keyDownTime, String keyCode, float centre_X, float centre_Y, float width, float height){

        // Creating an object for KeyTypeData entity class and initialising the data members
        // This object will be used as parameter for Room @Insert method
        // In essence The object encapsulates the values to be inserted into the table
        KeyTypeData data = new KeyTypeData();
        data.keyDownTime_id = keyDownTime;
        data.keyTypeCode = keyCode;
        data.keyCentre_X = centre_X;
        data.keyCentre_Y = centre_Y;
        data.key_Width = width;
        data.key_Height = height;

        //  Add a wrapper for the insert() method for KeyTypeData Table.
        // Insert will throw error is datamembers annotated with @nonnull have null values
        // Insert will throw error is datamembers annotated with @primaryKey is a duplicate
        // thus in try catch block
        try {
            mDatabaseInstance.mKey_dao().insertSingleKeyData(data);
        }catch(Exception e){
            //do nothing
            //Log.i("BiAffect", "Exception caught in insertKeyData at "+keyDownTime+ "\n");
        }
    }

    /**
     * Gets a list of AccelerometerData that occurred between the start and end times.
     *
     * @param start epoch representing the earliest AccelerometerData returned
     * @param end   epoch representing the latest AccelerometerData returned
     * @return List of AccelerometerData between the start and end times
     */
    public List<AccelerometerData> getAccelerometerData( long start, long end ) {
        return mDatabaseInstance.mAccelerometer_dao().getAccelerometerData( start, end );
    }

    //accelerometer exposed apis
    @Override
    public void insertAccelerometerData(long time, float x, float y, float z){

        // Creating an object for Accelerometer entity class and initialising the data members
        // This object will be used as parameter for Room @Insert method
        // In essence The object encapsulates the values to be inserted into the table
        AccelerometerData data = new AccelerometerData();
        data.time = time;
        data.Acc_X = x;
        data.Acc_Y = y;
        data.Acc_Z = z;

        //  Add a wrapper for the insert() method for TouchTypeData Table.
        // Insert will throw error is datamembers annotated with @nonnull have null values
        // Insert will throw error is datamembers annotated with @primaryKey is a duplicate
        // thus in try catch block
        try{
        mDatabaseInstance.mAccelerometer_dao().insertAccelerometerData(data);
    }catch (Exception e){
            // do nothing
        }}

    /**
     * Gets the singleton device data for this device.
     *
     * @return The singleton DeviceData for this device
     */
    public DeviceData getDeviceData() {
        return mDatabaseInstance.mDevice_dao().getDeviceData();
    }

    //api exposed for Device Data
    @Override
    public void insertDeviceData(Context c){

        // Handling device data being inserted only once
        // Instered when the app is first installed and never again.
        // Not inserted every time a session starts
        // avoid dupliaction of data

        int size=-1;
        //  Add a wrapper for the getAllIds() method in Device DAO for DeviceData Table.
        //  This method is a SQL SELECT query
        // It gives a warning if only some field names match.
        //  It gives an error if no field names match.
        try {
            size = mDatabaseInstance.mDevice_dao().getAllIds().length;
            //Log.i("DEVICEDATA", " "+size);
        }catch (Exception e){
           // Log.i("DEVICEDATA", "EXCEPTION"+e.getLocalizedMessage());
        }

        // getAllIds() returns the IDs of all the records stored in DeviceData Table
        // getAllIds().length will return 0 if and only if no record exists in the DeviceData table
        // this is a check done for single insertion
        if(size==0){

            // No probes on Data Collection system for Device Data. Directly collected and Proccessed here.

            // Getting device SDK vesion
            String release = Build.VERSION.RELEASE;
            int sdkVersion = Build.VERSION.SDK_INT;
            String FINALVERSION = "Android SDK: " + sdkVersion + " (" + release +")";

            // Getting device display metrics - pixel density, width, height (in pixels)
            DisplayMetrics metrics = c.getResources().getDisplayMetrics();
            int densityDPI = metrics.densityDpi;
            float densityLogical = metrics.density;
            int widthPixels = metrics.widthPixels;
            int heightPixels = metrics.heightPixels;

            // Getting device manufacturer and model details
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;


            //insert the data into db

            // Creating an object for DeviceData entity class and initialising the data members
            // This object will be used as parameter for Room @Insert method
            // In essence The object encapsulates the values to be inserted into the table
            DeviceData data = new DeviceData();
            data.androidVersion = FINALVERSION;
            data.pixelDensityDpi = densityDPI;
            data.pixelDensityLogical = densityLogical;
            data.deviceWidthPixel = widthPixels;
            data.deviceHeightPixel = heightPixels;
            data.manufacturer = manufacturer;
            data.phoneModel = model;

            //  Add a wrapper for the insert() method for DeviceData Table.
            // Insert will throw error is datamembers annotated with @nonnull have null values
            // Insert will throw error is datamembers annotated with @primaryKey is a duplicate
            // thus in try catch block
            try {
                mDatabaseInstance.mDevice_dao().insertDeviceData(data);
            }catch (Exception e){
                // do nothing
            }

        }
    }
}
