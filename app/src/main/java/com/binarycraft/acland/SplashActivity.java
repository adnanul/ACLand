package com.binarycraft.acland;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.binarycraft.acland.datautil.DBHelper;
import com.binarycraft.acland.datautil.Data;
import com.binarycraft.acland.entity.Mouza;
import com.binarycraft.acland.entity.Union;
import com.binarycraft.acland.entity.UnionMouzaResponse;
import com.binarycraft.acland.interfaces.APIUnionMouzaInterface;
import com.binarycraft.acland.util.ApplicationUtility;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class SplashActivity extends Activity {

    private static final int SPLASH_DISPLAY_LENGTH = 1000;
    public static final String KEY_REFRESH = "REFRESH";
    APIUnionMouzaInterface service;
    DBHelper dbHelper;

    Context context;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        context = this;

        Bundle extras = getIntent().getExtras();
        if(extras!= null){
            String value = extras.getString(KEY_REFRESH);
            if(value.equals("KEY_REFRESH")){
                initWebSevice();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        dbHelper = new DBHelper(context);
        if(dbHelper.checkDB()){
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                    endSplash();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }else {
            initWebSevice();
        }
    }

    private void initWebSevice(){
        if(ApplicationUtility.checkInternet(context)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Data.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = retrofit.create(APIUnionMouzaInterface.class);
            Call<UnionMouzaResponse> call = service.getAllUnionMouza();
            call.enqueue(unionMouzaResponseCallback);
        }else{
            ApplicationUtility.openNetworkDialog(this);
        }
    }

    Callback<UnionMouzaResponse> unionMouzaResponseCallback = new Callback<UnionMouzaResponse>() {
        @Override
        public void onResponse(Response<UnionMouzaResponse> response, Retrofit retrofit) {
            saveAllUnionAndMouzzas(response.body());
        }

        @Override
        public void onFailure(Throwable t) {
            Log.e("Request","Failed");
            Toast.makeText(context,Data.ERROR_TOAST,Toast.LENGTH_SHORT).show();
        }
    };

    private void saveAllUnionAndMouzzas(UnionMouzaResponse unionMouzaResponse){
        Vector<Mouza> mouzas = unionMouzaResponse.getMouzas();
        Vector<Union> unions = unionMouzaResponse.getUnions();
        for (Mouza mouza :
                mouzas) {
            dbHelper.createMouza(mouza);
        }
        for (Union union :
                unions) {
            dbHelper.createUnion(union);
        }
        endSplash();
    }

    private void endSplash(){
        startActivity(new Intent(context, MainActivity.class));
        finish();
    }
}
