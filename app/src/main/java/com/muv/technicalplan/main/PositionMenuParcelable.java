package com.muv.technicalplan.main;


import android.os.Parcel;
import android.os.Parcelable;

public class PositionMenuParcelable implements Parcelable
{
    public int key;
    public String position;

    public PositionMenuParcelable(int key, String position)
    {
        this.key = key;
        this.position = position;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(key);
        dest.writeString(position);
    }


    public static final Parcelable.Creator<PositionMenuParcelable> CREATOR = new Parcelable.Creator<PositionMenuParcelable>()
    {
        public PositionMenuParcelable createFromParcel(Parcel in) {
            return new PositionMenuParcelable(in);
        }

        public PositionMenuParcelable[] newArray(int size) {
            return new PositionMenuParcelable[size];
        }
    };

    private PositionMenuParcelable(Parcel parcel) {
        key = parcel.readInt();
        position = parcel.readString();
    }
}
