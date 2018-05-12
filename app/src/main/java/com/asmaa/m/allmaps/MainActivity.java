package com.asmaa.m.allmaps;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.ConditionVariable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.asmaa.m.allmaps.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements Event{


    private ActivityMainBinding binding;
    private static final String TAG = "MainActivity";
    private static final int Error_Dailog_Requst =123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main);
        binding.setEvent(this);

    }




    public boolean isServiceOk(){
        int available= GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available== ConnectionResult.SUCCESS){

            return true;

        }else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){

            Dialog dailog=GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,available,Error_Dailog_Requst);
            dailog.show();

        }else {

            Toast.makeText(this, "You Cant Make Map", Toast.LENGTH_SHORT).show();


        }

        return false;

    }

    @Override
    public void onclick(View view) {
        int id=view.getId();
        switch (id){

            case R.id.btn_map:
                if (isServiceOk()){
                    Intent intent=new Intent(MainActivity.this,MapssActivity.class);
                    startActivity(intent);

                }
                break;
        }


    }
}
