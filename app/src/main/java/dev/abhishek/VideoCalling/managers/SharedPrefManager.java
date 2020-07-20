package dev.abhishek.VideoCalling.managers;

import android.content.Context;
import android.content.SharedPreferences;

import dev.abhishek.VideoCalling.models.User;

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "USER_PREFS";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_UID = "keyuid";
    private static final String KEY_USERNAME = "keyusername";
    private static final String KEY_PROFILEIMG = "keyprofileimg";
    private static final String KEY_CREATED_ON = "keycreatedon";

    private static SharedPrefManager mInstance;
    private static Context mCtx;


    public SharedPrefManager(Context context){
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context){
        if(mInstance==null){
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public void loginUser(User user){
        SharedPreferences sharedPreferences =mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor  editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, user.getUserEmail());
        editor.putString(KEY_UID, user.getUserId());
        editor.putString(KEY_USERNAME, user.getUserName());
        editor.putString(KEY_PROFILEIMG, user.getUserProfileImg());
        editor.putLong(KEY_CREATED_ON, user.getCreated_on());
        editor.apply();
    }

    public User getUser(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString(KEY_UID, null),
                sharedPreferences.getString(KEY_USERNAME, null),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_PROFILEIMG, null),
                sharedPreferences.getLong(KEY_CREATED_ON, 0)
        );
    }
    public void signOut(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}

