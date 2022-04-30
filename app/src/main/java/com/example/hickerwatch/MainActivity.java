package com.example.hickerwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener locationListener;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if(grantResults.length>0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                }

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationManager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

            }
        };
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                updateLocation(location);
                            }
                        }
                    });

        }
    }
    public void updateLocation(Location location){
        TextView latitude=(TextView)findViewById(R.id.latitude);
        TextView longitude=(TextView)findViewById(R.id.longitude);
        TextView accuracy=(TextView)findViewById(R.id.accuracy);
        TextView altitude=(TextView)findViewById(R.id.altitude);

        latitude.setText(String.format("Latitude : %s", location.getLatitude()));
        longitude.setText(String.format("Longitude : %s", location.getLongitude()));
        accuracy.setText(String.format("Accuracy : %s", Double.toString(location.getAccuracy())));
        altitude.setText(String.format("Altitude : %s", location.getAltitude()));
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String Address ="could not find Address" ;
            if (address != null && address.size() > 0) {
                Address = "Address : \n" ;
                if (address.get(0).getThoroughfare() != null) {
                    Address += address.get(0).getThoroughfare() + " \n";
                }
                if (address.get(0).getAdminArea() != null) {
                    Address += address.get(0).getAdminArea() + " \n";
                }
                if (address.get(0).getLocality() != null) {
                    Address += address.get(0).getLocality();
                }
            }
            TextView house = (TextView) findViewById(R.id.house);
            house.setText( Address);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}