package com.menny.android.anysoftkeyboard.BiAffect.bridge;

import com.menny.android.anysoftkeyboard.BiAffect.Database.Models.AccelerometerData;
import com.menny.android.anysoftkeyboard.BiAffect.Database.Models.KeyTypeData;
import com.menny.android.anysoftkeyboard.BiAffect.Database.Models.TouchTypeData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * JSON object that represents the correct data format that Bridge Research Manager expects for BiAffect.
 */
public class Session {
    private double              duration;
    private long                timestamp;
    private List<Keylog>        keylogs = new ArrayList<>();
    private List<Accelerations> accelerations;

    /**
     * Basic constructor for this class that initializes the required time-related parameters
     *
     * @param start epoch representing the start of the Session
     * @param end   epoch representing the end of the Session
     */
    public Session( long start, long end ) {
        duration = end - start;
        timestamp = start;
    }

    /**
     * Adds the correct Accelerations data from the AccelerometerData from the DB
     *
     * @param accelerometerData List of AccelerometerData associated with this Session
     */
    public void addAccelerometerData( List<AccelerometerData> accelerometerData ) {
        accelerations = Observable.fromIterable( accelerometerData )
                                  .map( Accelerations::new )
                                  .toList()
                                  .blockingGet();
    }

    /**
     * Adds the Keylog object to the Session
     */
    public void addKeylog( Keylog keylog ) {
        keylogs.add( keylog );
    }

    /**
     * Class to represent an entire key (KeyTypeData), along with associated touches (TouchTypeData)
     * <p>
     * This maps to Keylogs in the scheme
     */
    public static class Keylog {
        private long        timestamp; //timestamp of first touch event
        private String      keyTypeCode; //from key type data
        private List<Touch> touches = new ArrayList<>();

        //transient to not be serialized in JSON
        //used to calculate distances
        private transient float centerX;
        private transient float centerY;
        private transient float width;
        private transient float height;

        private static transient float previousX = 0;
        private static transient float previousY = 0;

        private static class Touch {
            private long   timestamp;
            private String action;
            private float  force;
            private double distanceFromCenter;
            private double distanceFromPrevious;
            private float  touch_majorAxis;
            private float  touch_minorAxis;

            private Touch( TouchTypeData origin ) {
                timestamp = origin.eventActionTime;
                action = origin.eventAction;
                force = origin.force;
                touch_majorAxis = origin.touch_majorAxis;
                touch_minorAxis = origin.touch_minorAxis;
            }
        }

        /**
         * Creates a new Keylog object from the origin KeyTypeData object
         */
        public Keylog( KeyTypeData origin ) {
            timestamp = origin.keyDownTime_id;
            keyTypeCode = origin.keyTypeCode;
            centerX = origin.keyCentre_X;
            centerY = origin.keyCentre_Y;
            width = origin.key_Width;
            height = origin.key_Height;
        }

        /**
         * Creates a new Touch object from the origin TouchTypeData object
         * <p>
         * Also calculates the distanceFromCenter and distanceFromPrevious fields
         */
        public void addTouch( TouchTypeData origin ) {
            Touch touch = new Touch( origin );

            touch.distanceFromCenter = Math.hypot( origin.touch_xcord - centerX,
                                                   origin.touch_ycord - centerY );

            touch.distanceFromPrevious = Math.hypot( origin.touch_xcord - previousX,
                                                     origin.touch_ycord - previousY );

            touches.add( touch );
            previousX = origin.touch_xcord;
            previousY = origin.touch_ycord;
        }
    }

    /**
     * Class that maps to Accelerations in the scheme
     */
    private static class Accelerations {
        private long   time;
        private double x;
        private double y;
        private double z;

        private Accelerations( AccelerometerData source ) {
            time = source.time;
            x = source.Acc_X;
            y = source.Acc_Y;
            z = source.Acc_Z;
        }
    }

}
