package com.example.kshitija.googlemaps;

/**
 * Created by KSHITIJA on 4/27/2017.
 * Class to store list of locations locally
 */

public  class StoreLocations {
    public Double latitude, longitude;
    public String name;
    public Integer access, item;

    public StoreLocations() {
    }

    public StoreLocations(Double latitude, Double longitude, String name, Integer access, Integer item) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.item = item;
        this.access = access;
    }
}
