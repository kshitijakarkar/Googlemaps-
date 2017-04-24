package com.example.kshitija.googlemaps;

import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import static android.R.attr.data;
import static android.R.attr.value;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.media.CamcorderProfile.get;
import static android.os.Build.VERSION_CODES.N;




public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

   public static class StoreLocations{

       public Double latitude,longitude;
       public String name;
       public Integer access, item;

       public StoreLocations(){}

       public StoreLocations(Double latitude,Double longitude, String name,Integer access,Integer item)
       {
           this.name = name;
           this.latitude = latitude;
           this.longitude = longitude;
           this.item = item;
           this.access = access;
       }


   }


    private GoogleMap mMap;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("markers");
    //DatabaseReference mchild = myRef.child("marker2");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //myRef.setValue("Hello, World!");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLngBounds California = new LatLngBounds(new LatLng(34.352,-122.20),new LatLng(35.09,-113.58));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(California.getCenter(),5));

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<StoreLocations> randomstore = new ArrayList<>();

                for (DataSnapshot detailsSnapshot : dataSnapshot.getChildren()) {


                    Double lng = detailsSnapshot.child("lon").getValue(Double.class);
                    Double lat = detailsSnapshot.child("lat").getValue(Double.class);
                    String loc = detailsSnapshot.child("title").getValue(String.class);
                    Integer acc = detailsSnapshot.child("access").getValue(Integer.class);
                    Integer item = detailsSnapshot.child("item").getValue(Integer.class);

                    StoreLocations s = new StoreLocations(lat, lng, loc, acc, item);

                    randomstore.add(s);

                }
                Collections.shuffle(randomstore);

                for(int i =0;i<10;i++){
                    StoreLocations s = randomstore.get(i);
                    System.out.println("item : .............."+s.item);
                    LatLng pos = new LatLng(s.longitude,s.latitude);
                    mMap.addMarker(new MarkerOptions().position(pos).title(s.name));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                System.out.println("The read failed: " + databaseError.getCode());

            }
        });

    }


}
