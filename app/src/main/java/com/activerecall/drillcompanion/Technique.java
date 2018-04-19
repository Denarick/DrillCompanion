package com.activerecall.drillcompanion;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Steven on 3/7/2018.
 */

class Technique implements Parcelable {
    private int order;
    private SpokenName techniqueName;
    private SpokenName techSetName;
    public boolean includeSetName = true;

    public Technique(int order, SpokenName techniqueName, SpokenName techSetName){
        this.order = order;
        this.techniqueName = techniqueName;
        this.techSetName = techSetName;
    }

    public Technique() {}

    public String getWrittenName(){

        String name;

        // if the technique has a name, use it. Otherwise, use it's order
        if (techniqueName.name == null) {
            name = String.valueOf(order);
        } else {
            name = techniqueName.name;
        }

        // Include techSetName in result if flag is set
        if(includeSetName){
            return techSetName.name + " " + name;
        }
        return name;
    }

    public String getPronunciation(){
        String name;

        // if the technique has a name, use it. Otherwise, use it's order
        if (techniqueName.pronunciation == null) {
            name = String.valueOf(order);
        } else {
            name = techniqueName.pronunciation;
        }

        // Include techSetName in result if flag is set
        if(includeSetName){
            if(techSetName.pronunciation == null){
                name = techSetName.name + " " + name;
            } else {
                name = techSetName.pronunciation + " " +name;
            }

        }
        return name;
    }

    //<editor-fold desc="Parcelable Implementation">
    protected Technique(Parcel in) {
        order = in.readInt();
        techniqueName = in.readParcelable(SpokenName.class.getClassLoader());
        techSetName = in.readParcelable(SpokenName.class.getClassLoader());
        includeSetName = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(order);
        dest.writeParcelable(techniqueName, flags);
        dest.writeParcelable(techSetName, flags);
        dest.writeByte((byte) (includeSetName ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Technique> CREATOR = new Creator<Technique>() {
        @Override
        public Technique createFromParcel(Parcel in) {
            return new Technique(in);
        }

        @Override
        public Technique[] newArray(int size) {
            return new Technique[size];
        }
    };
    //</editor-fold>

}
