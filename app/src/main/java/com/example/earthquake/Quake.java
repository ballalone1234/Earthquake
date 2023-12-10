package com.example.earthquake;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Quake {
    private Date date;
    private String details;
    private Location location;
    private double magnitude;
    private String link;
    private String depth;
    public Date getDate() { return date; }
    public String getDetails() { return details; }
    public Location getLocation() { return location; }
    public double getMagnitude() { return magnitude; }
    public String getLink() { return link; }
    public Quake(Date _d, String _det, Location _loc, double _mag, String _link, String _depth) {
        date = _d;
        details = _det;
        location = _loc;
        magnitude = _mag;
        link = _link;
        depth = _depth;
    }
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH.mm", Locale.US);
        String dateString = sdf.format(date);
        return dateString + ": " + magnitude + " (" + depth + "m) " + details ;
    }
}

