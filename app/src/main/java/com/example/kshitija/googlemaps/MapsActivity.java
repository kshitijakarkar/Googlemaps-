package com.example.kshitija.googlemaps;
/*
    Google map is loaded with 10 locations. Camera zooms in to the selected location.
    Save button appears on top screen to update number of clicks to that Medical Facility.
    After Update is done Thank ou message pops up with new locations.


 */
import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseException;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import com.example.kshitija.googlemaps.StoreLocations;

import static android.R.attr.button;
import static android.R.attr.data;
import static android.R.attr.value;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.media.CamcorderProfile.get;
import static android.os.Build.VERSION_CODES.N;




public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("markers");
    final ArrayList<StoreLocations> randomstore = new ArrayList<>(); //To get locations randomly
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        button = new Button(this);

        button.setVisibility(button.INVISIBLE);
        addContentView(button, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));


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

        LatLngBounds California = new LatLngBounds(new LatLng(34.352, -122.20), new LatLng(35.09, -113.58));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(California.getCenter(), 5)); //Default to zoom California
        mMap.getUiSettings().setZoomControlsEnabled(true);
        myRef.addValueEventListener(new ValueEventListener() {

           @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {


                for (DataSnapshot detailsSnapshot : dataSnapshot.getChildren()) {

                    Integer acc = detailsSnapshot.child("access").getValue(Integer.class);
                    Integer item = detailsSnapshot.child("item").getValue(Integer.class);
                    Double lng = detailsSnapshot.child("lon").getValue(Double.class);
                    Double lat = detailsSnapshot.child("lat").getValue(Double.class);
                    String loc = detailsSnapshot.child("title").getValue(String.class);

                    StoreLocations s = new StoreLocations(lat, lng, loc, acc, item); //Create objects locally

                    randomstore.add(s);  //Store in list

                }

               Collections.shuffle(randomstore); //To get random locations
               for (int i = 0; i < 10; i++) {    //Print 10.
                   StoreLocations s = randomstore.get(i);

                   LatLng pos = new LatLng(s.longitude, s.latitude);
                   mMap.addMarker(new MarkerOptions().position(pos).title(s.name)); //create and display marker
                   mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
               }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                System.out.println("The read failed: " + databaseError.getCode());

            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {              //Listen to click on marker
                mMap.moveCamera(CameraUpdateFactory.zoomBy(5));
                button.setText("Click to Save " + marker.getTitle());
                button.setVisibility(button.VISIBLE);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {             //Listen to click on save

                        final String m = marker.getTitle();

                        final ProgressDialog Dialog = new ProgressDialog(MapsActivity.this);
                        Dialog.setMessage("Saving");
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {  //Listen to change in data of
                                                                                        // single object
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                String i;
                                String set;
                                Integer number;
                                String r = dataSnapshot.child("title").getValue(String.class);

                                for (DataSnapshot detailsSnapshot : dataSnapshot.getChildren()) {

                                    String loc = detailsSnapshot.child("title").getValue(String.class);

                                    if(m.equals(loc)){ //check for object with same title on firebase

                                        i=detailsSnapshot.child("item").getValue(Integer.class).toString();
                                        set = "marker"+i;

                                        number = detailsSnapshot.child("access").getValue(Integer.class); //get its access count
                                        Toast.makeText(MapsActivity.this, "This Facility is accessed "+number+" times "
                                                , Toast.LENGTH_SHORT).show();
                                        number = number+1;
                                        Dialog.show();


                                        myRef.child(set).child("access").setValue(number); //increment the count
                                        Dialog.dismiss();
                                        Toast.makeText(MapsActivity.this, "Thank you for using FastER", Toast.LENGTH_SHORT).show();
                                        onRestart();
                                    }
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                System.out.println("The read failed: " + databaseError.getCode());
                            }
                        });



                    }
                });
                return false;
            }
        });



   }

   protected void onRestart(){
       super.onRestart();
       Intent i = new Intent(getApplicationContext(),MapsActivity.class);
       startActivity(i);
       finish();
   }

}



