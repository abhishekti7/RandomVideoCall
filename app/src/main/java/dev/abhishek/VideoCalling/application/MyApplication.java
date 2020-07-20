package dev.abhishek.VideoCalling.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import dev.abhishek.VideoCalling.managers.SharedPrefManager;
import dev.abhishek.VideoCalling.ui.MainActivity;
import dev.abhishek.VideoCalling.utils.Constants;


public class MyApplication extends Application {

    private static MyApplication mInstance;
    int activity_code;
    SharedPrefManager sharedPrefManager;
    Activity mActivity;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    public static MyApplication getInstance() {
        return mInstance;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        activity_code = 0;
        sharedPrefManager = new SharedPrefManager(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        mInstance = this;
        setLifeCyclerTracker(firebaseAuth, sharedPrefManager);
    }

    public void setLifeCyclerTracker(FirebaseAuth firebaseAuth, SharedPrefManager sharedPrefManager) {
        registerActivityLifecycleCallbacks(new AppLifeCycleTracker(firestore, firebaseAuth, sharedPrefManager));
    }

    public void setActivity_code(int code){
        this.activity_code = code;
    }

    public int getActivity_code(){
        return this.activity_code;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public FirebaseFirestore getFirestore() {
        return firestore;
    }

    public SharedPrefManager getSharedPrefManager() {
        return sharedPrefManager;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
    }

    /**
     * Call when application is close
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        if (mInstance != null) {
            mInstance = null;
        }
    }
}


class AppLifeCycleTracker implements Application.ActivityLifecycleCallbacks {

    private  int numStarted = 0;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    SharedPrefManager sharedPrefManager;

    public AppLifeCycleTracker(FirebaseFirestore firebaseFirestore, FirebaseAuth firebaseAuth, SharedPrefManager sharedPrefManager) {
        this.firebaseAuth = firebaseAuth;
        this.sharedPrefManager = sharedPrefManager;
        this.firestore = firebaseFirestore;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        Log.e("LifeCycle", "onActivityCreated" + activity.getLocalClassName());
        MyApplication.getInstance().setActivity(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        Log.e("LifeCycle", "onActivityStarted" + activity.getLocalClassName());
        numStarted++;

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        Log.e("LifeCycle", "onActivityResumed" + activity.getLocalClassName());
        MyApplication.getInstance().setActivity(activity);

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        Log.e("LifeCycle", "onActivityPaused" + activity.getLocalClassName());
        numStarted--;
        if(MainActivity.dialog!=null){
            MainActivity.dialog.dismiss();
        }
        if (firebaseAuth.getUid()!=null && numStarted == 0) {
            // app went to background
            Log.d("LIFE CYCLE TRACKER", "status changes");
            firestore.collection(Constants.COLLECTION_SEARCHING).document(firebaseAuth.getCurrentUser().getUid())
                    .delete();

        }

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        Log.e("LifeCycle", "onActivityStopped" + activity.getLocalClassName());
        numStarted--;
        if (firebaseAuth.getUid()!=null && numStarted == 0) {
            // app went to background
            Log.d("LIFE CYCLE TRACKER", "status changes");
            firestore.collection(Constants.COLLECTION_SEARCHING).document(firebaseAuth.getCurrentUser().getUid())
                    .delete();

        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
        Log.e("LifeCycle", "onActivitySaveInstanceState" + activity.getLocalClassName());

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        Log.e("LifeCycle", "onActivityDestroyed" + activity.getLocalClassName());
    }
}
