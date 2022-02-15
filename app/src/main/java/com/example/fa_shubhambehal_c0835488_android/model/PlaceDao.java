package com.example.fa_shubhambehal_c0835488_android.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlaceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPlace(PlaceInfo place);

    @Query("SELECT * FROM places")
    List<PlaceInfo> getAllPlaces();

    @Query("UPDATE places SET isVisited = 1 WHERE placeId = :placeId")
    void updateVisitedStatus(int placeId);

    @Query("DELETE FROM places WHERE placeId = :placeId")
    void deletePlace(int placeId);

    @Query("UPDATE places SET placeName= :placeName, placeLat=:placeLat,placeLong=:placeLong WHERE placeId = :placeId")
    void updatePlace(int placeId, String placeName, double placeLat, double placeLong);
}
