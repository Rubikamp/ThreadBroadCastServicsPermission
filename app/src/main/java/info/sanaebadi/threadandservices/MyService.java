package info.sanaebadi.threadandservices;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MyService extends Service {
    private ExecutorService executor;
    private Runnable runnable;

    private static final String TAG = "MyService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //called just once
        runnable= () -> {
            try {
                Thread.sleep(5000);
                Log.i(TAG, "onCreate: " + "service runnig");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
           //stop service
            stopSelf();
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(executor==null){
            executor= Executors.newSingleThreadExecutor();
            executor.submit(runnable);
        }
        return START_NOT_STICKY;
    }

}
