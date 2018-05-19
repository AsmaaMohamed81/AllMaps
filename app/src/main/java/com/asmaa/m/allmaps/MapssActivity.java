package com.asmaa.m.allmaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapssActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapssActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private final int PER_REQ = 12012;
    private static final float Defult_Zoom=15f;

    //widgets
    private AutoCompleteTextView Search;
    private ImageView gps;
    //vars
    GoogleMap Mmap;
    private boolean isGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapss);
        Search = findViewById(R.id.edt_search);
        gps=findViewById(R.id.img_gps);

        checkPermission();
    }

    private void init(){

        Log.d(TAG, "init: intialization");

        Search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionid, KeyEvent keyEvent) {

                if (actionid== EditorInfo.IME_ACTION_SEARCH
                        || actionid==EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction()==KeyEvent.ACTION_DOWN
                        || keyEvent.getAction()==KeyEvent.KEYCODE_ENTER){

                    geolocate();
                }
                return false;
            }


        });

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });

        hideSoftKeyboard();

    }

    private void geolocate() {

        String searchString=Search.getText().toString();
        Geocoder geocoder= new Geocoder(this);
        List<Address> addressList=new ArrayList<>();

        try {

            addressList=geocoder.getFromLocationName(searchString,1);

        }catch (IOException e){

            Log.d(TAG, "geolocate:IOException "+e.getMessage());

        }

        if (addressList.size() > 0){
             Address address=addressList.get(0);
            Log.d(TAG, "geolocate: found location"+address.toString());
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),Defult_Zoom,address.getAddressLine(0));
        }



    }



    private void moveCamera(LatLng latLng,float zoom,String title   ){
        Mmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));


        if (!title.equals("myLocation")){
            MarkerOptions options=new MarkerOptions()
                    .position(latLng)
                    .title(title);

            Mmap.addMarker(options);

        }

        hideSoftKeyboard();


    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Mmap=googleMap;

        Toast.makeText(this, "Map Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG,"onMapReady : Map Ready");

        if (isGranted){
            try {
                getDeviceLocation();

                // get image of gps
                Mmap.setMyLocationEnabled(true);
                // delete image of gps
                Mmap.getUiSettings().setMyLocationButtonEnabled(false);
                init();
            }catch (SecurityException e)
            {
                e.printStackTrace();
            }



        }



    }
//
    private void getDeviceLocation()    {

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


                                moveCamera(new LatLng
                                        (currentlocation1.getLatitude(),currentlocation1.getLongitude()),Defult_Zoom,"myLocation");

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

        //make implement to get method of onMapReady():

        mapFragment.getMapAsync(this);
    }

// 1 step checkPermission && onRequestPermissionsResult
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


private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
}
}
