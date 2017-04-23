package com.example.kshitija.googlemaps;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("markers");
    DatabaseReference mchild = myRef.child("marker1");


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
       // myRef.setValue("Hello, World!");
        // Add a marker in Sydney and move the camera
       // double lo = 18.52;
        //double la = 73.85;
        //LatLng sydney = new LatLng(lo, la);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Pune"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //final double lng=0;
        //double lat=0;

        mchild.child("lat").setValue(73.85);
        mchild.child("lon").setValue(18.52);
        mchild.child("title").setValue("Pune");
        mchild.child("access").setValue(1);

        mchild.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double lng = dataSnapshot.child("lon").getValue(Double.class);
                Double lat = dataSnapshot.child("lat").getValue(Double.class);
                String loc = dataSnapshot.child("title").getValue(String.class);
                //System.out.println("Lon : .............."+lng);
                //System.out.println("lat : .............."+lat);

                LatLng pune = new LatLng(lng,lat);
                mMap.addMarker(new MarkerOptions().position(pune).title(loc));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(pune));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //LatLng pune = new LatLng(lat,lng);
        //mMap.addMarker(new MarkerOptions().position(pune).title("PUNE"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(pune));
    }
}
