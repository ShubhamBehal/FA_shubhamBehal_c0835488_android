package com.example.fa_shubhambehal_c0835488_android.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "places")
public class PlaceInfo implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int placeId;
    public String placeName;
    public double placeLat;
    public double placeLong;
    public boolean isVisited;

    public PlaceInfo(String placeName, double placeLat, double placeLong, boolean isVisited) {
        this.placeName = placeName;
        this.placeLat = placeLat;
        this.placeLong = placeLong;
        this.isVisited = isVisited;
    }

    protected PlaceInfo(Parcel in) {
        placeId = in.readInt();
        placeName = in.readString();
        placeLat = in.readDouble();
        placeLong = in.readDouble();
        isVisited = in.readByte() != 0;
    }

    public static final Creator<PlaceInfo> CREATOR = new Creator<PlaceInfo>() {
        @Override
        public PlaceInfo createFromParcel(Parcel in) {
            return new PlaceInfo(in);
        }

        @Override
        public PlaceInfo[] newArray(int size) {
            return new PlaceInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(placeId);
        parcel.writeString(placeName);
        parcel.writeDouble(placeLat);
        parcel.writeDouble(placeLong);
        parcel.writeByte((byte) (isVisited ? 1 : 0));
    }
}
