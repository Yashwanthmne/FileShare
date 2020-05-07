package com.hotocorporation.demo.FileShare;

import android.app.Application;

public class MyApplication extends Application {
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!

        // Network service
        //getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));
    }
}
