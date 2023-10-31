package com.example.locationpinned;

public class LocationObject {

    private Integer id;
    private double latitude;
    private double longitude;
    private String address;

    public LocationObject(double latitude, double longitude, String address) {
        this(null, latitude, longitude, address);
    }
    public LocationObject(Integer id, double latitude, double longitude, String address) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    // for testing remove at end
    @Override
    public String toString() {
        return "ID: " + id + ", Latitude: " + latitude + ", Longitude: " + longitude + ", Address: " + address;
    }

}
