package com.example.vehicletracker.Location;

public class UserInfo {
    String userId;
    String prevSpeed;
    String speed;
    String timestamp;
    String event;
    String locationX;
    String locationY;


    public UserInfo() {

    }

    public String getLocationX() {
        return locationX;
    }

    public void setLocationX(String locationX) {
        this.locationX = locationX;
    }



    public String getLocationY() {
        return locationY;
    }

    public void setLocationY(String locationY) {
        this.locationY = locationY;
    }



    public String getPrevSpeed() {
        return prevSpeed;
    }

    public void setPrevSpeed(String lastSpeed) {
        this.prevSpeed = lastSpeed;
    }



    public String getTimestamp() {

        return timestamp;
    }
    public void setTimestamp(String timestamp) {

        this.timestamp = timestamp;
    }



    public String getSpeed() {
        return speed;
    }
    public void setSpeed(String speed) {
        this.speed = speed;
    }



    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String id) {
        this.userId = id;
    }
}