package com.muv.technicalplan.enterprise;

import android.os.Parcel;
import android.os.Parcelable;

public class DeleteMapParcelable implements Parcelable
{
    public String enterprise;
    public String position;
    public String path;
    public String TAG;
    public String code;
    public String name_table;

    public DeleteMapParcelable(String enterprise, String position, String path, String tag, String code,
                            String name_table)
    {
        this.enterprise = enterprise;
        this.position = position;
        this.path = path;
        this.TAG = tag;
        this.code = code;
        this.name_table = name_table;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(enterprise);
        dest.writeString(position);
        dest.writeString(path);
        dest.writeString(TAG);
        dest.writeString(code);
        dest.writeString(name_table);
    }


    public static final Parcelable.Creator<DeleteMapParcelable> CREATOR = new Parcelable.Creator<DeleteMapParcelable>()
    {
        public DeleteMapParcelable createFromParcel(Parcel in) {
            return new DeleteMapParcelable(in);
        }

        public DeleteMapParcelable[] newArray(int size) {
            return new DeleteMapParcelable[size];
        }
    };

    private DeleteMapParcelable(Parcel parcel)
    {
        enterprise = parcel.readString();
        position = parcel.readString();
        path = parcel.readString();
        TAG = parcel.readString();
        code = parcel.readString();
        name_table = parcel.readString();
    }
}