package com.menny.android.anysoftkeyboard.BiAffectDB;


import com.menny.android.anysoftkeyboard.BiAffectDB.BiAffectDB_roomModel.AccelerometerData;
import com.menny.android.anysoftkeyboard.BiAffectDB.BiAffectDB_roomModel.DeviceData;
import com.menny.android.anysoftkeyboard.BiAffectDB.BiAffectDB_roomModel.KeyData;
import com.menny.android.anysoftkeyboard.BiAffectDB.BiAffectDB_roomModel.SessionData;
import com.menny.android.anysoftkeyboard.BiAffectDB.BiAffectDB_roomModel.TouchData;

public interface BiAffectDBInterface {
    interface TouchDataInterface {
        //All the calls to DAO for Touch entity will be processed.

        void insertTouchTypeEntry(TouchData single_entry);
        void insertTouchTypeEntryBatch(TouchData[] multi_entry);
        TouchData[] fetchTouchDataRows(long keyId,int motioneventtype);

    }

    interface SessionDataInterface {

        void insertSessionStartTime (SessionData single_entry);
        void updateSessionEndTime(SessionData data);
    }

    interface KeyTypeDataInterface {
        void insertOnlySingleKeyMetrics (KeyData single_entry);

        void insertMultipleKeyMetrics (KeyData[] keyDataList);

        //for radius one way of adding to DB will be update at end of session. Also time of key up. Creating the stub as placeholder for now
        //not required but kept return type int so that Roomreturns number of rows edited. This will help in debugging

        int updateRadiusofTouch(KeyData data);

    }

    interface DeviceDataInterface {
        void insertDeviceData (DeviceData single_entry);

        int fetchDeviceId();

    }

    interface AccelerometerDataInterface {
        void insertOnlySingleAccelerometerEntry (AccelerometerData single_entry);

        void insertMultipleAccelerometerEntry (AccelerometerData[] DataList);
    }

}
