package dev.abhishek.VideoCalling.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import dev.abhishek.VideoCalling.R;


public class Utils {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected())
            return true;
        else {
            showDialog(context);
            return false;
        }
    }

    private static void showDialog(final Context context) {

        final Dialog dialog;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_no_internet);


        dialog.findViewById(R.id.sure_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                ((AppCompatActivity) context).startActivityForResult(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS), 0);
                ((AppCompatActivity) context).overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.slide_dialog;
        dialog.show();
    }

    public static Dialog showSearching(Context context){
        final Dialog dialog;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_searching);
        dialog.setCancelable(false);



        dialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.slide_dialog;
        dialog.show();
        return dialog;
    }
}
