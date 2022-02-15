package com.example.fa_shubhambehal_c0835488_android.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.fa_shubhambehal_c0835488_android.R;
import com.example.fa_shubhambehal_c0835488_android.model.PlaceDB;
import com.example.fa_shubhambehal_c0835488_android.model.PlaceInfo;
import com.example.fa_shubhambehal_c0835488_android.model.SingleLiveEvent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    LocationManager lm;
    LocationListener ll;
    private GoogleMap mMap;
    private PlaceDB placeDB;
    private Button btnSave;
    private PlaceInfo placeInfo;
    private final SingleLiveEvent<Boolean> isSaved = new SingleLiveEvent<>();
    private boolean isEdit;
    private int placeId;
    private double homeLat;
    private double homeLong;
    private double favLat;
    private double favLong;
    private TextView tvDistance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        btnSave = findViewById(R.id.btn_save);
        Button btnNormal = findViewById(R.id.btn_normal);
        Button btnHybrid = findViewById(R.id.btn_hybrid);
        Button btnSubmit = findViewById(R.id.btn_submit);
        tvDistance = findViewById(R.id.tv_distance);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        placeDB = PlaceDB.getInstance(getApplicationContext());

        btnSave.setOnClickListener(view -> insertFavToRepo());

        isSaved.observe(this, aBoolean -> {
            if (aBoolean) {
                Toast.makeText(MapActivity.this, "Location added to the Favourite List", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAddressAndAddMarker();
            }
        });


        btnNormal.setOnClickListener(view -> mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL));
        btnHybrid.setOnClickListener(view -> mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID));
    }

    private void validateAddressAndAddMarker() {
        String address = ((EditText) findViewById(R.id.et_address)).getText().toString().trim();
        if (address.equalsIgnoreCase("")) {
            Toast.makeText(this, "The Address cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            markFavOnMap(getLocationFromAddress(this, address));
        }
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p1;

    }

    private void insertFavToRepo() {
        addToRepo(placeInfo);
    }

    public void addMarker(Location location, String title) {
        LatLng location1 = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(location1).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location1));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
                Location lastLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                addMarker(lastLocation, "Your Last Location");
                homeLat = lastLocation.getLatitude();
                homeLong = lastLocation.getLongitude();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        addHomeLocation();
        Intent intent = getIntent();
        PlaceInfo place = intent.getParcelableExtra("place");
        isEdit = place != null;

        if (place != null) {
            placeId = place.placeId;
            Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(place.placeLat);
            placeLocation.setLongitude(place.placeLong);
            addMarker(placeLocation, place.placeName);
            favLat = place.placeLat;
            favLong = place.placeLong;
            drawLine();
        }
        if (!intent.getBooleanExtra("isViewOnly", false)) {
            mMap.setOnMapLongClickListener(this::markFavOnMap);
            findViewById(R.id.group_edit).setVisibility(View.VISIBLE);
        }
    }

    private void addHomeLocation() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ll = location -> {
            addMarker(location, "Your Location");
            homeLat = location.getLatitude();
            homeLong = location.getLongitude();
            drawLine();
        };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
        }
    }

    private void drawLine() {
        if (favLat != 0 && favLong != 0 && homeLat != 0 && homeLong != 0) {
            PolylineOptions line =
                    new PolylineOptions().add(
                            new LatLng(favLat, favLong),
                            new LatLng(homeLat,
                                    homeLong))
                            .width(5).color(Color.RED);
            mMap.addPolyline(line);
            calculateDistance(new LatLng(homeLat, homeLong), new LatLng(favLat, favLong));
        }
    }

    public void calculateDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        tvDistance.setVisibility(View.VISIBLE);
        tvDistance.setText("Total distance to travel is : " + valueResult + "KM");
    }

    private void markFavOnMap(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String location = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                if (addresses.get(0).getAdminArea() != null) {
                    location += addresses.get(0).getAdminArea() + " ";
                }
                if (addresses.get(0).getLocality() != null) {
                    location += addresses.get(0).getLocality() + " ";
                }
                if (addresses.get(0).getSubLocality() != null) {
                    location += addresses.get(0).getSubLocality() + " ";
                }
            }
            if (Objects.equals(location, "")) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
                location += sdf.format(new Date());
            }
            LatLng latLng1 = new LatLng(latLng.latitude, latLng.longitude);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng1).title(location));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
            addHomeLocation();
            placeInfo = new PlaceInfo(location, latLng.latitude, latLng.longitude, false);

            btnSave.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addToRepo(PlaceInfo placeInfo) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                if (isEdit) {
                    placeDB.placeDao().updatePlace(placeId, placeInfo.placeName, placeInfo.placeLat, placeInfo.placeLong);
                } else {
                    placeDB.placeDao().insertPlace(placeInfo);
                }
                isSaved.postValue(true);
            }
        };
        thread.start();
    }
}
