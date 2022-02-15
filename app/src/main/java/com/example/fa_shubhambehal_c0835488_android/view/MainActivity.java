package com.example.fa_shubhambehal_c0835488_android.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fa_shubhambehal_c0835488_android.R;
import com.example.fa_shubhambehal_c0835488_android.interfaces.FavPlaceClickListener;
import com.example.fa_shubhambehal_c0835488_android.model.PlaceDB;
import com.example.fa_shubhambehal_c0835488_android.model.PlaceInfo;
import com.example.fa_shubhambehal_c0835488_android.model.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FavPlaceClickListener {
    private final SingleLiveEvent<List<PlaceInfo>> placeListLiveData = new SingleLiveEvent<>();
    private PlaceListAdapter adapter;
    private List<PlaceInfo> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView rvPlaces = findViewById(R.id.places);
        Button btnAddNewPlace = findViewById(R.id.btn_add_new_place);
        if (places == null) {
            places = new ArrayList<>();
        }
        adapter = new PlaceListAdapter(places, this);
        rvPlaces.setLayoutManager(new LinearLayoutManager(this));
        rvPlaces.setAdapter(adapter);
        btnAddNewPlace.setOnClickListener(view -> {
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
        });

        placeListLiveData.observe(this, placeInfos -> adapter.setData(placeInfos));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                PlaceDB placeDB = PlaceDB.getInstance(getApplicationContext());
                placeListLiveData.postValue(placeDB.placeDao().getAllPlaces());
            }
        };
        thread.start();
    }

    @Override
    public void onVisitedClick(int placeId) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                PlaceDB placeDB = PlaceDB.getInstance(getApplicationContext());
                placeDB.placeDao().updateVisitedStatus(placeId);
                placeListLiveData.postValue(placeDB.placeDao().getAllPlaces());
            }
        };
        thread.start();
    }

    @Override
    public void onPlaceDelete(int placeId) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                PlaceDB placeDB = PlaceDB.getInstance(getApplicationContext());
                placeDB.placeDao().deletePlace(placeId);
                placeListLiveData.postValue(placeDB.placeDao().getAllPlaces());
            }
        };
        thread.start();
    }

    @Override
    public void onItemClick(PlaceInfo place) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("isViewOnly", true);
        intent.putExtra("place", place);
        startActivity(intent);
    }

    @Override
    public void onPlaceEdit(PlaceInfo place) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("isViewOnly", false);
        intent.putExtra("place", place);
        startActivity(intent);
    }
}