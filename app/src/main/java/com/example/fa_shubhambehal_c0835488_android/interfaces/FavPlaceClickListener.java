package com.example.fa_shubhambehal_c0835488_android.interfaces;

import com.example.fa_shubhambehal_c0835488_android.model.PlaceInfo;

public interface FavPlaceClickListener {
    void onVisitedClick(int placeId);

    void onPlaceDelete(int placeId);

    void onItemClick(PlaceInfo place);

    void onPlaceEdit(PlaceInfo place);
}
