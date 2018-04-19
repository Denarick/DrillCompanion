package com.activerecall.drillcompanion;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Steven on 3/7/2018.
 */

public class SpokenName implements Parcelable {
    public String name;
    public String pronunciation;

    //<editor-fold desc="Parcelable Implementation">
    public SpokenName(String name, String pronunciation){
        this.name = name;
        this.pronunciation = pronunciation;
    }

    protected SpokenName(Parcel in) {
        name = in.readString();
        pronunciation = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(pronunciation);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SpokenName> CREATOR = new Creator<SpokenName>() {
        @Override
        public SpokenName createFromParcel(Parcel in) {
            return new SpokenName(in);
        }

        @Override
        public SpokenName[] newArray(int size) {
            return new SpokenName[size];
        }
    };
    //</editor-fold>
}
