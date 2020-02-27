package com.menny.android.anysoftkeyboard.BiAffect.Database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.menny.android.anysoftkeyboard.BiAffect.Database.Models.DeviceData;

// DAO includes methods that offer abstract access to your app's database.
@Dao
public interface Device_DAO {

    // When you create a DAO method and annotate it with @Insert,
    // Room generates an implementation that inserts all parameters into the database in a single transaction.


    // When an applicable constraint violation occurs, the IGNORE resolution algorithm skips the one row that contains the constraint violation
    // and continues processing subsequent rows of the SQL statement as if nothing went wrong.
    // Other rows before and after the row that contained the constraint violation are inserted or updated normally.
    // No error is returned when the IGNORE conflict resolution algorithm is used.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertDeviceData(DeviceData data);

    // @Query is the main annotation used in DAO classes.
    // It allows you to perform read/write operations on a database.
    /*Each @Query method is verified at compile time, so if there is a problem with the query,
     a compilation error occurs instead of a runtime failure.
      */
    //Room also verifies the return value of the query
    // If the name of the field in the returned object doesn't match the corresponding column names in the query response,
    // Room alerts you in one of the following two ways:
    //It gives a warning if only some field names match.
    //It gives an error if no field names match.
    @Query("SELECT id FROM DeviceData")
    int[] getAllIds();

    @Query( "SELECT * FROM DeviceData LIMIT 1" )
    DeviceData getDeviceData();
}
