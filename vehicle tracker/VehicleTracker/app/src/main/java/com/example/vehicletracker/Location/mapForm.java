package com.example.vehicletracker.Location;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.vehicletracker.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class mapForm extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap map;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference= database.getReference();
    long datNumber=0; // keeps number of inserts





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.map_layout);
        //initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapForm.this);

    }





    //  *map opening*
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        Query downloadData = reference;

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {     datNumber=snapshot.getChildrenCount();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        map=googleMap;
        downloadData.addListenerForSingleValueEvent(new ValueEventListener() {

            //RUN THROUGH ALL DATA IN Real Time DB
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userId, event, prevSpeed, speed, timestamp, locationX,locationY;

                if (snapshot.exists()) { //for all data
                    for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {

                        //get user's id
                        userId = childDataSnapshot.child("userId").getValue().toString();

                        //get event
                        event = childDataSnapshot.child("event").getValue().toString();

                        //get previous speed before accelaration or braking
                        prevSpeed = childDataSnapshot.child("prevSpeed").getValue().toString();

                        //get speed
                        speed = childDataSnapshot.child("speed").getValue().toString();

                        //get timestamp
                        timestamp = childDataSnapshot.child("timestamp").getValue().toString();

                        //get location's coordinates
                        locationX = childDataSnapshot.child("locationX").getValue().toString();
                        locationY = childDataSnapshot.child("locationY").getValue().toString();
                        double locx = Double.parseDouble(locationX);
                        double locy = Double.parseDouble(locationY);


                        //location
                        LatLng name;
                        name = new LatLng(locx, locy);

                        //ACCELERATION EVENT
                        if(event.equals("acceleration")){
                            map.addMarker(new MarkerOptions()
                                    .position(name)
                                    .title(event)
                                    .snippet("Speed: "+speed +" previous speed: "+prevSpeed + "  timestamp: "+timestamp+ " user: "+userId )
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(name,15f));

                        }else{  //BRAKING EVENT
                            map.addMarker(new MarkerOptions()
                                    .position(name)
                                    .title(event)
                                    .snippet("Speed: "+speed +" previous speed: "+prevSpeed + "  timestamp: "+timestamp+ " user: "+userId )
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(name,15f));
                        }
                    }
                }
            }

            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }

        });


    }
}