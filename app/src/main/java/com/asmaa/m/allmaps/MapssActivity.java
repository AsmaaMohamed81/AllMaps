package com.asmaa.m.allmaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class MapssActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapssActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean isGranted = false;
    private final int PER_REQ = 12012;
    private static final float Defult_Zoom=15f;

    GoogleMap Mmap;

    private FusedLocationProviderClient fusedLocationProviderClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapss);

        checkPermission();
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        Mmap=googleMap;

        Toast.makeText(this, "Map Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG,"onMapReady : Map Ready");

        if (isGranted){
            try {
                getDeviceLocation();
                Mmap.setMyLocationEnabled(true);
                Mmap.getUiSettings().setMyLocationButtonEnabled(false);
            }catch (SecurityException e)
            {
                e.printStackTrace();
            }



        }



    }

    private void getDeviceLocation() {

        Log.d(TAG, "getDeviceLocation: get Currnt Location");

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);

        try {
            if (isGranted){
                final Task location=fusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location");

                            Location currentlocation1=(Location) task.getResult();

                            if (currentlocation1!=null)
                            {
                                Log.d(TAG, "onComplete: "+currentlocation1.getLatitude());
                                Log.d(TAG, "onComplete: "+currentlocation1.getLongitude());


                                Mmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng
                                        (currentlocation1.getLatitude(),currentlocation1.getLongitude()),Defult_Zoom));

                            }

                            }else {

                            Log.d(TAG, "onComplete: Not Found");
                            Toast.makeText(MapssActivity.this, "Uenable to get currunt location", Toast.LENGTH_SHORT).show();
                            
                        }
                    }
                });


            }


        }catch (SecurityException e){

            Log.d(TAG, "getDeviceLocation: Scyrety exaption"+e.getMessage());
            
        }catch (NullPointerException e){}


    }

    private void initMap() {

        Log.d(TAG,"initMap : Init Map ");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void checkPermission() {
        Log.d(TAG, "checkPermission: get premation");
        String[] permission = new String[]{FINE_LOCATION, COURSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                isGranted = true;
                initMap();
            } else {

                ActivityCompat.requestPermissions(this, permission, PER_REQ);

            }


        } else {


            ActivityCompat.requestPermissions(this, permission, PER_REQ);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult: called ;");
        isGranted = false;

        if (requestCode == PER_REQ) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {

                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isGranted = false;
                        Log.d(TAG, "onRequestPermissionsResult: Permassion failed");
                        Toast.makeText(this, "NOOOOO", Toast.LENGTH_SHORT).show();
                        return;

                    }
                }

            } else {
                Log.d(TAG, "onRequestPermissionsResult: Praemaion Granted");
                isGranted = true;
                initMap();
            }
        }

    }



}
