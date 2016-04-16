package com.binarycraft.acland.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

import com.binarycraft.acland.R;
import com.binarycraft.acland.datautil.Data;

/**
 * Created by User on 12-Apr-16.
 */
public class ApplicationUtility {

    public static void openNetworkDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

// 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(activity.getString(R.string.con_message))
                .setTitle(activity.getString(R.string.con_title)).setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        }).setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        }).setCancelable(false).create();
        builder.show();
    }

    public static boolean checkInternet(Context context) {
        ConnectivityManager check = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = check.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        return isConnected;
    }

    public static String isEnglishNumber(String key) {
        String dag = "";
        char[] charArray = key.toCharArray();
        for (char number : charArray
                ) {
            boolean isNumber = false;
            int index = 0;
            for (int i = 0; i<Data.numbers_eng.length;i++) {
                if(number==Data.numbers_eng[i]){
                    index = i;
                    isNumber = true;
                }
            }
            if (isNumber) {
                dag = dag+Data.numbers_bng[index];
            }else {
                dag = dag + number;
            }
            Log.e("DAG",dag);
        }
        Log.e("DAG",dag);
        return dag;
    }

}
