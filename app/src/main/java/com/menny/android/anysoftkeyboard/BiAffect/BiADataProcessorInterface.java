package com.menny.android.anysoftkeyboard.BiAffect;

public interface BiADataProcessorInterface {
    interface TouchDataProcessorInterface {
        //All the calls to process the touch data will be provided over here
        //EventDownTime
        //EventTime
        //EventAction
        //Pressure of the event
        //x_cordinate
        //y_cordinate
        //major axis
        //minor axis
        //Number of touches..this is being done for the purpose of testing if we are overlapping any calls or something

        //One thing i need to make sure is that that data type of the touch cordinates remain the same for the view and the touches.

        boolean addMasterEntry(long eventDownTime, long eventTime, int eventAction, float pressure, float x_cord, float y_cord, float major_axis, float minor_axis, int touches);
    }

    interface KeyDataProcessorInterface {
        //All the calls to process the key data will be provided over here
        //1->Key press down time..this is not the onpress call time as the onpress call follows after this probe, but we are really not interested in,
        //when the listener handle the call, rather the time when the touch to this call was initiated
        //2->Key press release time, this is again not the time of onRelease call to the listener but the time when the user lifted his/her finger,
        //motionevent action_up was recorded, again sticking with the requirements
        //3->the type of the key pressed
        //This call might have some issue when it comes to handling the calls from the mini keyboard, the only thing which i can be sure about that right now,
        //is that there will not be an action_up in the database for the corresponding longpress key, which i have decided for now that i will leave that,
        //on the discretion of the person performing analysis of the data on how to treat that entry

        //One api will be to add a call to record the keyDownTime and the associated key code along with all values

        boolean addKeyDataOnlyDownTime(long eventDownTime, int key_id, float keyCentre_X, float keyCentre_Y, float keyWidth, float keyHeight);

    }

    interface DeviceDataProcessorInterface{
        //This interface is going to put the device specific data in the table, it will only happen once
        //we need to figureout how can i achieve that
        //We will be putting in 4 values in here for now, which are android version, pixel density, screen size and phone model
        void sendDeviceData();

    }

    interface SessionDataProcessorInterface{
        //This will contain the methods which are specific to record the session of the keyboard,
        //This will not be processed by any worker thread, instead it will be pushed in to db directly and the id of the current session will be
        //maintained in the BiAManager.java
        boolean startSession();
        boolean endSession();
    }

}
