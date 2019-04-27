package com.menny.android.anysoftkeyboard.BiAffectDB;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.menny.android.anysoftkeyboard.AnyApplication;
import com.menny.android.anysoftkeyboard.BiAffectDB.BiAffectDB_roomDAO.Accelerometer_DAO;
import com.menny.android.anysoftkeyboard.BiAffectDB.BiAffectDB_roomDAO.Device_DAO;
import com.menny.android.anysoftkeyboard.BiAffectDB.BiAffectDB_roomDAO.KeyData_DAO;
import com.menny.android.anysoftkeyboard.BiAffectDB.BiAffectDB_roomDAO.Session_DAO;
import com.menny.android.anysoftkeyboard.BiAffectDB.BiAffectDB_roomDAO.Touch_DAO;
import com.menny.android.anysoftkeyboard.BiAffectDB.BiAffectDB_roomModel.AccelerometerData;
import com.menny.android.anysoftkeyboard.BiAffectDB.BiAffectDB_roomModel.DeviceData;
import com.menny.android.anysoftkeyboard.BiAffectDB.BiAffectDB_roomModel.KeyData;
import com.menny.android.anysoftkeyboard.BiAffectDB.BiAffectDB_roomModel.SessionData;
import com.menny.android.anysoftkeyboard.BiAffectDB.BiAffectDB_roomModel.TouchData;

@Database(entities = {TouchData.class, KeyData.class, SessionData.class, AccelerometerData.class, DeviceData.class}, version = 2,exportSchema = false)
public abstract class BiAffectDB extends RoomDatabase {

    public abstract Touch_DAO TouchDataDao();
    public abstract Session_DAO SessionDataDao();
    public abstract KeyData_DAO KeyDataDAO();
    public abstract Accelerometer_DAO AccelDataDAO();
    public abstract Device_DAO DeviceDataDAO();

    private static volatile BiAffectDB INSTANCE;

    public static BiAffectDB getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BiAffectDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(AnyApplication.getAppContext(),
                            BiAffectDB.class, "BiAffect_database.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
