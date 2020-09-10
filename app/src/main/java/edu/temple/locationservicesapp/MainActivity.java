package edu.temple.locationservicesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    LocationManager lm;
    LocationListener ll;
    Location oldLocation;

    TextView lat, lon;

    MapView mapView;
    Marker marker;
    LatLng myLocation;
    GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);
        mapView.getMapAsync(this);

        mapView.onCreate(savedInstanceState);

        lat = findViewById(R.id.lat);
        lon = findViewById(R.id.lon);

        lm = getSystemService(LocationManager.class);

        ll = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat.setText(String.valueOf(location.getLatitude()));
                lon.setText(String.valueOf(location.getLongitude()));

                if (oldLocation != null) {
                    Log.d("Speed", String.valueOf(oldLocation.distanceTo(location) / (location.getTime() - oldLocation.getTime())));
                }
                oldLocation = location;
                if (googleMap != null) {
                    myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.animateCamera(CameraUpdateFactory
                            .newLatLngZoom(myLocation, 17));
                    if (marker == null) {
                        marker = googleMap.addMarker((new MarkerOptions()).position(myLocation));
                    } else {
                        marker.setPosition(myLocation);
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        if (!checkPermission()) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 111);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (checkPermission())
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, ll);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        lm.removeUpdates(ll);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    private boolean checkPermission() {
        return (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission must be granted for this application to function!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (checkPermission()) {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            marker = googleMap.addMarker((new MarkerOptions()).position(latLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }
    }
}