package com.example.earthquake;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.earthquake.databinding.ActivityEarthQuakeMapBinding;

import java.util.ArrayList;

public class earthQuakeMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityEarthQuakeMapBinding binding;

    ArrayList<Double> latitudes = new ArrayList<>();
    ArrayList<Double> longitudes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEarthQuakeMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        latitudes = (ArrayList<Double>) getIntent().getSerializableExtra("latitudes");
        longitudes = (ArrayList<Double>) getIntent().getSerializableExtra("longitudes");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
            for (int i = 0; i < latitudes.size(); i++) {
                LatLng latLng = new LatLng(latitudes.get(i), longitudes.get(i));
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        // Add a marker in Sydney and move the camera
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        String serviceString = Context.LOCATION_SERVICE;
        LocationManager locationManager;
        locationManager = (LocationManager) getSystemService(serviceString);
        String provider = LocationManager.GPS_PROVIDER;
        Location l = locationManager.getLastKnownLocation(provider);

        if(l != null) {
           mMap.addMarker(new MarkerOptions()
                   .position(new LatLng(l.getLatitude(), l.getLongitude()))
                   .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
           LatLng latLng = new LatLng(l.getLatitude(), l.getLongitude());
              mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 2));
        }


    }
}