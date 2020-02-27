package com.menny.android.anysoftkeyboard.BiAffect.Database;

import android.content.Context;

public interface BiADBInterface {
    interface TouchDataInterface {

        //All the calls to process the touch data will be provided over here
        // no data processing handled by data collector. raw data sent as parameters.
        //EventDownTime
        //EventTime
        //EventAction
        //Pressure of the event
        //x_cordinate
        //y_cordinate
        //major axis
        //minor axis

        // Currently according to design, single insert works efficiently. thus only this implemented.
        void insertTouchData(long eventDownTime, long eventTime, String eventAction,
         float pressure, float x_cord, float y_cord, float majorAxis, float minorAxis);
    }

    interface KeyDataInterface {

        //All the calls to process the key data will be provided over here
        //no data processing handled by data collector. raw data sent as parameters.
        // eventDownTime which acts as ID
        // keytype code
        // key center X
        // key center Y
        // kry width
        //key height

        //Api will be to add a call to record the keyDownTime and the associated key code along with all values
        void insertKeyData(long keyDownTime, String keyCode, float centre_X, float centre_Y, float width, float height);
    }


    interface SessionDataInterface {

        //This will contain the methods which are specific to record the session of the keyboard,

        void insertSessionData(long sessionStartTime);
        void updateSessionData(long sessionStartTime, long sessionEndTime);
    }

    interface AccelerometerData {
        // insert accelerometer data sampled every 1/1000th of a second through out a session
        void insertAccelerometerData(long time, float x, float y, float z);
    }

    interface DeviceData {
        //This interface is going to put the device specific data in the table, it will only happen once
        // this is handled in the implementation
        void insertDeviceData(Context c);
    }
}

