package dev.abhishek.VideoCalling.managers;

import android.app.Activity;
import android.content.Intent;

import java.util.HashMap;

import dev.abhishek.VideoCalling.R;


public class ActivitySwitchManager {

    Activity activity;
    Class NewActivity;
    Intent mMenuIntent;

    public ActivitySwitchManager(Activity activity, Class NewActivity){

        this.activity = activity;
        this.NewActivity = NewActivity;
         mMenuIntent = new Intent(activity,
                NewActivity);
    }
    public ActivitySwitchManager(Activity activity, Class NewActivity, HashMap<String, String> params){
        this.activity = activity;
        this.NewActivity = NewActivity;
        mMenuIntent = new Intent(activity, NewActivity);
        for (String key : params.keySet()){
            mMenuIntent.putExtra(key, params.get(key));
        }
    }
    public void openActivity(){


        mMenuIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(mMenuIntent);
        activity.finish();

        activity.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);

    }

    public void openActivityWithoutSlide(){


        mMenuIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(mMenuIntent);
        activity.finish();

    }
    public void openActivityWthoutFinish(){

        activity.startActivity(mMenuIntent);
        activity.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);

    }
}
