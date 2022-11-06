package com.example.vehicletracker.Location;

import android.location.Location;

public class locationFunctions extends Location {


    public locationFunctions(Location location){
        super(location);



    }


    @Override
    public float distanceTo(Location dest) {
        float nDistance= super.distanceTo(dest);
        return nDistance;

    }


    @Override
    public double getAltitude() {
        double nAltitude =super.getAltitude();
        return nAltitude;
    }

    @Override
    public float getSpeed() {
        float nSpeed =super.getSpeed() * 3.6f;
        return nSpeed * 2.23693629f;
    }

    @Override
    public float getAccuracy() {
        float nAccurancy =super.getAccuracy();
        return nAccurancy;
    }
}
