package com.menny.android.anysoftkeyboard.BiAffect.bridge;

import org.sagebionetworks.bridge.android.manager.BridgeManagerProvider;
import org.sagebionetworks.bridge.rest.model.ConsentStatus;
import org.sagebionetworks.bridge.rest.model.UserSessionInfo;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import rx.Single;

/**
 * Utility class to help manage the connection between BiAffect data and the Sage Bridge SDK
 *
 * @author wang@arbormoon.com
 */
public class BiAffectBridge {

    private static class Holder {
        public static BiAffectBridge instance = new BiAffectBridge();
    }

    private BiAffectBridge() {}

    public static BiAffectBridge getInstance() {
        return Holder.instance;
    }

    /**
     * Checks to see if user is considered logged in or not.
     * Sets up Session uploads if the user is logged in.
     */
    public boolean isUserLoggedIn() {
        boolean loggedIn = (null != BridgeManagerProvider.getInstance().getAuthenticationManager().getUserSessionInfo());
        if( loggedIn ) {
            setUpWorkManager();
        }
        return loggedIn;
    }

    /**
     * Sets up WorkManager so queued uploads will be processed:
     * once a day
     * when the device is on an unmetered connection
     * when the device is charging
     * when the device is idle
     */
    private void setUpWorkManager() {
        WorkManager.getInstance().cancelAllWork();
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType( NetworkType.UNMETERED )
                                                           .setRequiresCharging( true )
                                                           .setRequiresDeviceIdle( true )
                                                           .build();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder( BridgeWorker.class,
                                                                       1,
                                                                       TimeUnit.DAYS )
                                          .setConstraints( constraints )
                                          .build();

        WorkManager.getInstance().enqueue( request );
    }

    /**
     * Logs the user in to the Bridge Study Manager.
     * Successful login will also set up Session uploads.
     *
     * @param email    Email that the user signed up with
     * @param password Password that the user signed up with
     * @return A Single that can be subscribed to for updates.
     */
    public Single<UserSessionInfo> logIn( String email, String password ) {
        return BridgeManagerProvider.getInstance()
                                    .getAuthenticationManager()
                                    .signIn( email, password )
                                    .doOnSuccess( __ -> setUpWorkManager() );
    }

    /**
     * Attempts to bypass the consent step to help facilitate onboarding.
     *
     * @param userSessionInfo UserSessionInfo to get the required consents from
     * @return A Single whose success represents a successful bypass
     */
    public Single<UserSessionInfo> bypassConsent( UserSessionInfo userSessionInfo ) {
        Map<String, ConsentStatus> consents = userSessionInfo.getConsentStatuses();
        for( String guid : consents.keySet() ) {
            ConsentStatus consent = consents.get( guid );
            if( null == consent ) {
                continue;
            }
            if( consent.isRequired() && !consent.isConsented() ) {
                return BridgeManagerProvider.getInstance()
                                            .getAuthenticationManager()
                                            .giveConsent( guid,
                                                          userSessionInfo.getFirstName() + " " + userSessionInfo.getLastName(),
                                                          null,
                                                          null,
                                                          null,
                                                          userSessionInfo.getSharingScope() );
            }
        }
        return null;
    }

    public void singleUpload() {
        WorkManager.getInstance().cancelAllWork();
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder( BridgeWorker.class ).build();
        WorkManager.getInstance().enqueue( request );
    }

}
