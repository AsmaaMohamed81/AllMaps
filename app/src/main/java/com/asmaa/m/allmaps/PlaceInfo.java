package com.asmaa.m.allmaps;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class PlaceInfo {


    private String name;
    private String address;
    private String phonenum;
    private String id;
    private String attributes;
    private float rating;
    private LatLng latLng;
    private Uri website;


    public PlaceInfo(String name, String address, String phonenum, String id, String attributes, float rating, LatLng latLng, Uri website) {
        this.name = name;
        this.address = address;
        this.phonenum = phonenum;
        this.id = id;
        this.attributes = attributes;
        this.rating = rating;
        this.latLng = latLng;
        this.website = website;
    }

    public PlaceInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Uri getWebsite() {
        return website;
    }

    public void setWebsite(Uri website) {
        this.website = website;
    }

    @Override
    public String toString() {
        return "PlaceInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phonenum='" + phonenum + '\'' +
                ", id='" + id + '\'' +
                ", attributes='" + attributes + '\'' +
                ", rating=" + rating +
                ", latLng=" + latLng +
                ", website=" + website +
                '}';
    }
}
