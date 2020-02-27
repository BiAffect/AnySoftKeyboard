package com.menny.android.anysoftkeyboard.BiAffect.Database.Models;

import android.arch.persistence.room.*;

import io.reactivex.annotations.NonNull;

//@Entity annotates that this class is a table schema and each data member declared in this class corresponds to a table column
@Entity
public class SessionData {

    // A session starts when the keyboard comes into view
    // sessionStartTime is the timestamp in milliseconds when the keybaord comes into view, starting a session
    @PrimaryKey
    @NonNull
    public long sessionStartTime;

    // A session ends when the keyboard goes out of view.
    // sessionEndTime is the timestamp in milliseconds when the keybaord goes out of view, ending a session
    public long sessionEndTime;

    public SessionData(){
        //Default constructor
        // as all data members are public no need for getter and setter methods.
        // each data member defined in an @Entity class must be either public or have a getter method

    }

}
