package com.menny.android.anysoftkeyboard.BiAffect.Database.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import io.reactivex.annotations.NonNull;

/*
For each key pressed, the data related to the key is constant. But each key press has multiple touch events being fired and we are capturing data for each event.
* To avoid having duplicate key data, we have normalised the data of touch and key
*  */

//@Entity annotates that this class is a table schema and each data member declared in this class corresponds to a table column
@Entity
public class KeyTypeData {
    /*This Column is a foreign key in TouchTypeData Table.
    eventDownTime is the time when the motion event - ACTION_DOWN is fired for a touch.
    Each key press will have multiple actions associated with it. We store data for each action fired during the course of each key press.
    To group the records to the corresponding key press we store the eventdowntime for each data record as eventDownTime is unique for each key press.
    */
      /* To correlate the key pressed at each "key press" we are using eventdowntime as a primary key in this table as keyDownTime_id.
      Therefore all the records in TouchTypeData Table having eventdowntime value equal to keyDownTime_id value are data collected for that key press
      * */
    @PrimaryKey
    @NonNull
    public long keyDownTime_id;

    // We do not store the key pressed. Instead we have grouped the keys into different categories - alphanumeric, backspace, autocorrect, suggestion, other. Codes of these categories are stored.
    @NonNull
    public String keyTypeCode;

    // The centre of the key pressed - X coord
    // In pixels
    @NonNull
    public float keyCentre_X;

    // The centre of the key pressed - Y coord
    // In pixels
    @NonNull
    public float keyCentre_Y;

    // The width of the key pressed
    // In pixels
    @NonNull
    public float key_Width;

    // The width of the key pressed
    // In pixels
    @NonNull
    public float key_Height;
}
