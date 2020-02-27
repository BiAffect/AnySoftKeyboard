package com.menny.android.anysoftkeyboard.BiAffect.bridge;

import android.content.Context;
import android.support.annotation.NonNull;

import org.sagebionetworks.bridge.android.manager.BridgeManagerProvider;

import androidx.work.RxWorker;
import androidx.work.WorkerParameters;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Single;

/**
 * WorkManager work that takes care of processing the queued upload files
 */
public class BridgeWorker extends RxWorker {

    public BridgeWorker( @NonNull Context appContext, @NonNull WorkerParameters workerParams ) {
        super( appContext, workerParams );
    }

    @Override
    public Single<Result> createWork() {
        return RxJavaInterop.toV2Single( BridgeManagerProvider.getInstance()
                                                              .getUploadManager()
                                                              .processUploadFiles()
                                                              .toSingle( Result::success ) );
    }
}
