package com.menny.android.anysoftkeyboard.BiAffect.Database.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.menny.android.anysoftkeyboard.BiAffect.TouchDataPOJO;

import io.reactivex.annotations.NonNull;

//@Entity annotates that this class is a table schema and each data member declared in this class corresponds to a table column
@Entity
public class TouchTypeData {
    //Id is an autogenerated primary key. It keeps track of number of rows in the table
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;

    /* eventDownTime is the time when the motion event - ACTION_DOWN is fired for a touch.
    Each key press will have multiple actions associated with it. We store data for each action fired during the course of each key press.
    To group the records to the corresponding key press we store the eventdowntime for each data record as eventDownTime is unique for each key press.
    */
    @NonNull
    public long eventDownTime;

    // stores the time when this motion event action was fires for the key press.
    @NonNull
    public long eventActionTime;

    // stores the code for type of motion event action that was fired at that key press. 0 for ACTION_DOWN, 2 for ACTION_MOVE, 1 for ACTION_UP
    @NonNull
    public int eventAction;

    // stores the pressure of key press recorded at the moment the event action in question was fired.
    @NonNull
    public float force;

    // stores the point of touch - X coordinate in pixel recorded at the moment the event action in question was fired.
    @NonNull
    public float touch_xcord;

    // stores the point of touch - Y coordinate in pixel recorded at the moment the event action in question was fired.
    @NonNull
    public float touch_ycord;

    // stores The length of the major axis of an ellipse that describes the touch area at the point of contact
    //  recorded at the moment the event action in question was fired.
    // the length is reported in pixels
    @NonNull
    public float touch_majorAxis;


    // stores The length of the minor axis of an ellipse that describes the touch area at the point of contact
    //  recorded at the moment the event action in question was fired.
    // the length is reported in pixels
    @NonNull
    public float touch_minorAxis;

    public TouchTypeData(){
        //Default constructor
        // as all data members are public no need for getter and setter methods.
        // each data member defined in an @Entity class must be either public or have a getter method
    }

}
