package com.example.vehicletracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vehicletracker.Location.UserInfo;
import com.example.vehicletracker.Location.locationFunctions;
import com.example.vehicletracker.Location.mapForm;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Formatter;
import java.util.Locale;

public class tracker extends AppCompatActivity implements LocationListener {

    float prevspeed ; //keep previous speed
    long dataNumber=0; // keeps number of inserts
    TextView speedmeter; //speed
    TextView email;

    Button seeOnMap;
    Button logout;

    //constructor class
    UserInfo info;

    //firebase declarations
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String usersId = user.getUid(); //get user's id
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference reference= db.getReference();
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tracker);

        mAuth = FirebaseAuth.getInstance();
        info =new UserInfo();
        prevspeed = 0; //set a default speed

        //get buttons and texts
        logout = findViewById(R.id.logout);


        //show user's email
        email=findViewById(R.id.emailTracker);
        email.setText("Email: "+user.getEmail());

        //show speed
        speedmeter = findViewById(R.id.speedmeter);
        speedmeter.setText(0.00 + " km/h");



        //counting inserts in realtime db to reach them later
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {     dataNumber=snapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //Logout button pressed
        logout.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(tracker.this, login.class));

        });



        //Open map
        seeOnMap= findViewById(R.id.map);
        seeOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity((new Intent(tracker.this, mapForm.class)));
            }
        });




        seeOnMap.setOnClickListener(view -> {

            startActivity(new Intent(tracker.this, mapForm.class));

        });






        //check for GPS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {
            // if the permission is granted
            getPerms();
        }

        this.updateSpeed(null);

    }






    // ***Calling function everytime when the location is changed***
    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            locationFunctions myLocation = new locationFunctions(location);
            this.updateSpeed(myLocation);
        }
    }





    public void getPerms() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                //request the missing permissions, and then overriding
                return;
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        }
        Toast.makeText(this, "waiting for GPS connection!", Toast.LENGTH_SHORT).show();
    }








    //ACCELERATION AND BREAKING FORMULA
    private void updateSpeed(locationFunctions location) {
        //get time stamp
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        //OLD SPEED
        float currentSpeed = prevspeed;

        // **LOCATION CHANGED ?**
        if (location != null) {

            //get new speed
            currentSpeed = location.getSpeed();
        }


        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", currentSpeed);

        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(" ", "0");

        //show speed on speedmeter
        speedmeter.setText(strCurrentSpeed + " km/h");


        DecimalFormat df = new DecimalFormat("#.0000");
        DecimalFormat df2 = new DecimalFormat("#.00");

        //SETTING VALUES IN UserInfo Class
        //accelerating
        if (currentSpeed - prevspeed >25) {
            info.setLocationX(String.valueOf(df.format(location.getLatitude())));
            info.setLocationY(String.valueOf(df.format(location.getLongitude())));
            info.setPrevSpeed(df2.format(prevspeed));
            info.setSpeed(String.valueOf( df2.format(currentSpeed - prevspeed)));
            info.setTimestamp(timestamp.toString());
            info.setEvent("acceleration");
            info.setUserId(usersId);

            reference.child(String.valueOf(dataNumber+1)).setValue(info);//new child

            //message
            Toast.makeText(tracker.this,"Accelerating!! HOLD TIGHT!!", Toast.LENGTH_SHORT).show();

        //braking
        } else if (currentSpeed - prevspeed < -25) {

            info.setLocationX(String.valueOf(df.format(location.getLatitude())));
            info.setLocationY(String.valueOf(df.format(location.getLongitude())));
            info.setPrevSpeed(df2.format(prevspeed));
            info.setSpeed(String.valueOf( df2.format(currentSpeed - prevspeed)));
            info.setTimestamp(timestamp.toString());
            info.setEvent("braking");
            info.setUserId(usersId);

            reference.child(String.valueOf(dataNumber+1)).setValue(info); //new child

            //message
            Toast.makeText(tracker.this,"Braking!! HOLD TIGHT AGAIN!!", Toast.LENGTH_SHORT).show();
        }else{
        }
        //update last speed
        prevspeed=currentSpeed;
    }






    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        if(requestCode == 1000){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPerms();


            }else {
                finish();
            }
        }
    }




    //if the user is not logged in
    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            startActivity((new Intent(tracker.this, login.class)));
        }

    }




}