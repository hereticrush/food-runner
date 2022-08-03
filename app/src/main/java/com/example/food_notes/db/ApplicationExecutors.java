package com.example.food_notes.db;

import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

//TODO find a way to implement a threads interface
public class ApplicationExecutors {

    private final Executor mDiskIO;

    private final Executor mNetworkIO;

    private final Executor mMainThread;

    private ApplicationExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.mDiskIO = diskIO;
        this.mNetworkIO = networkIO;
        this.mMainThread = mainThread;
    }

    public ApplicationExecutors() {
        this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(4), new MainThreadExecutor());
    }


    public Executor getDiskIO() {
        return mDiskIO;
    }

    public Executor getNetworkIO() {
        return mNetworkIO;
    }

    public Executor getMainThread() {
        return mMainThread;
    }

    private static class MainThreadExecutor implements Executor {

        private Handler mainThreadHandler = new Handler() {
            @Override
            public void publish(LogRecord record) {

            }

            @Override
            public void flush() {
                mainThreadHandler.flush();
            }

            @Override
            public void close() throws SecurityException {
                mainThreadHandler.close();
            }
        };

        @Override
        public void execute(@NonNull Runnable command) {

        }
    }
}