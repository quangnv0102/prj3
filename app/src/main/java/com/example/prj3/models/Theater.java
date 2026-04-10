package com.example.prj3.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Theater {
    private String id;
    private String name;
    private String address;
    private String city;
    private String phone;
    private int totalScreens;
    private double latitude;
    private double longitude;

    public Theater() {}

    public Theater(String id, String name, String address, String city,
                   String phone, int totalScreens) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.totalScreens = totalScreens;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getTotalScreens() { return totalScreens; }
    public void setTotalScreens(int totalScreens) { this.totalScreens = totalScreens; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}
