package com.asmaa.m.allmaps;

import android.Manifest;
import android.content.Intent;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapssActivity
        extends AppCompatActivity
        implements OnMapReadyCallback
        , GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MapssActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private final int PER_REQ = 12012;
    private static final float Defult_Zoom=15f;

    //widgets
    private AutoCompleteTextView Search;
    private ImageView gps,info,pick;
    //vars
    GoogleMap Mmap;
    private boolean isGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private PlaceAutocompleteAdapter mplaceAutocompleteAdapter;
    private GeoDataClient geoDataClient;
    private GoogleApiClient mGoogleApiClient;

    private PlaceInfo mpalce;

    private Marker mmarker;
  public static final   int PLACE_PICKER_REQUEST = 1;



    private static final LatLngBounds LAT_LNG_BOUNDS=
            new LatLngBounds(new LatLng(-40,-168) ,new LatLng(71,136));




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapss);
        Search = findViewById(R.id.edt_search);

        gps=findViewById(R.id.img_gps);
        info=findViewById(R.id.img_info);
        pick=findViewById(R.id.place_packer);

        checkPermission();
    }

    private void init(){



        Log.d(TAG, "init: intialization");

// filter en egypt only
        geoDataClient = Places.getGeoDataClient(this,null);
        final AutocompleteFilter filter = new AutocompleteFilter.Builder()
                .setCountry("eg")
                .build();

        mplaceAutocompleteAdapter=new PlaceAutocompleteAdapter(this,geoDataClient,LAT_LNG_BOUNDS,filter);
        Search.setAdapter(mplaceAutocompleteAdapter);

        Search.setOnItemClickListener(Autocompletclicklisiner);

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

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if (mmarker.isInfoWindowShown()){
                        mmarker.hideInfoWindow();

                    }else {
                        mmarker.showInfoWindow();
                    }

                }catch (NullPointerException e){
                    Log.d(TAG, "onClick: excption"+e.getMessage());

                }
            }
        });

        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(MapssActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        hideSoftKeyboard();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this,this)
                .build();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient,place.getId());

                placeResult.setResultCallback(mupdateplacesdeatailescallback);
            }
        }
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

    private void moveCamera(LatLng latLng,float zoom,PlaceInfo placeInfo){
        Mmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        Mmap.clear();

        Mmap.setInfoWindowAdapter(new CustomAdapter(this));

        if (placeInfo!=null){

            try {

                Toast.makeText(this, "moveCamera Place Info", Toast.LENGTH_SHORT).show();

                String snippte="Adress : "+placeInfo.getAddress()+"\n"+
                        "phone : "+placeInfo.getPhonenum()+"\n"+
                        "web : "+placeInfo.getWebsite()+"\n"+
                        "rating : "+placeInfo.getRating()+"\n";

                MarkerOptions options=new MarkerOptions()
                        .title(placeInfo.getName())
                        .position(latLng)
                        .snippet(snippte);

                mmarker=Mmap.addMarker(options);


            }catch (NullPointerException e){
                Log.d(TAG, "moveCamera: nullexception"+e.getMessage());
            }
        }else {
            Mmap.addMarker(new MarkerOptions().position(latLng));
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

    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    inputMethodManager.hideSoftInputFromInputMethod(Search.getWindowToken(),0
    );
}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private AdapterView.OnItemClickListener Autocompletclicklisiner =new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();

            final AutocompletePrediction item=mplaceAutocompleteAdapter.getItem(i);
            final String placeid=item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient,placeid);

            placeResult.setResultCallback(mupdateplacesdeatailescallback);
            hideSoftKeyboard();

        }
    };

    private ResultCallback<PlaceBuffer> mupdateplacesdeatailescallback=new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {

            if (!places.getStatus().isSuccess()){

                Log.d(TAG, "onResult: place qury donot complet succes"+places.getStatus().toString());

                places.release();
                return;
            }

            final Place place=places.get(0);

            try {

                mpalce=new PlaceInfo();
                mpalce.setAddress(place.getAddress().toString());
                Log.d(TAG, "onResult: getAddress"+place.getAddress().toString());
//                mpalce.setAttributes(place.getAttributions().toString());
//                Log.d(TAG, "onResult: getAttributions"+place.getAttributions().toString());

                mpalce.setId(place.getId());
                Log.d(TAG, "onResult: getId"+place.getId());

                mpalce.setLatLng(place.getLatLng());
                Log.d(TAG, "onResult: getLatLng"+place.getLatLng());

                mpalce.setName(place.getName().toString());
                Log.d(TAG, "onResult: name"+place.getName());

                mpalce.setRating(place.getRating());
                Log.d(TAG, "onResult: getRating"+place.getRating());

                mpalce.setPhonenum(place.getPhoneNumber().toString());
                Log.d(TAG, "onResult: getPhoneNumber"+place.getPhoneNumber().toString());

                Log.d(TAG, "onResult: get all place Info"+mpalce.toString());


            }catch (NullPointerException e){
                Log.d(TAG, "onResult: NullPointerException "+e.getMessage());
            }


            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude),Defult_Zoom,mpalce);


            places.release();


        }
    };
}
