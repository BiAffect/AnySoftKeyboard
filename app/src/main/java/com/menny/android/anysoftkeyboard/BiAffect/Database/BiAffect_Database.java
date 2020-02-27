package com.menny.android.anysoftkeyboard.BiAffect.Database;

import android.arch.persistence.room.*;

import com.menny.android.anysoftkeyboard.BiAffect.Database.DAO.Accelerometer_DAO;
import com.menny.android.anysoftkeyboard.BiAffect.Database.DAO.Device_DAO;
import com.menny.android.anysoftkeyboard.BiAffect.Database.DAO.Key_DAO;
import com.menny.android.anysoftkeyboard.BiAffect.Database.DAO.Session_DAO;
import com.menny.android.anysoftkeyboard.BiAffect.Database.DAO.Touch_DAO;
import com.menny.android.anysoftkeyboard.BiAffect.Database.Models.AccelerometerData;
import com.menny.android.anysoftkeyboard.BiAffect.Database.Models.DeviceData;
import com.menny.android.anysoftkeyboard.BiAffect.Database.Models.KeyTypeData;
import com.menny.android.anysoftkeyboard.BiAffect.Database.Models.SessionData;
import com.menny.android.anysoftkeyboard.BiAffect.Database.Models.TouchTypeData;


/*
Room class must be abstract and extend RoomDatabase. This class is the boiler plate for the database to be created
Usually, you only need one instance of the Room database for the whole app.
*/

//Annotate the class to be a Room database, declare the entities that belong in the database and set the version number.
// Listing the entities will create tables in the database.
@Database(entities = {SessionData.class, TouchTypeData.class, KeyTypeData.class,
        AccelerometerData.class, DeviceData.class}, version = 1, exportSchema = false)
public abstract class BiAffect_Database extends RoomDatabase {

    // Defining the DAOs that work with the database.
    // Since gave all of the DAOs public access, no need to explicity provide abstract "getter" method for each @Dao.
    // Definition required in this class so that Room knows which transactions should be handled for each table.
    public abstract Session_DAO mSession_dao();
    public abstract Touch_DAO mTouch_dao();
    public abstract Key_DAO mKey_dao();
    public abstract Accelerometer_DAO mAccelerometer_dao();
    public abstract Device_DAO mDevice_dao();
}
