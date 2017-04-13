package com.muv.technicalplan.enterprise;


import android.os.Parcel;
import android.os.Parcelable;

public class PositionsParcelable  implements Parcelable
{
    public String login;
    public String position;
    public String code;
    public String name_table;

    public PositionsParcelable(String login, String position, String code, String name_table)
    {
        this.login = login;
        this.position = position;
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
        dest.writeString(login);
        dest.writeString(position);
        dest.writeString(code);
        dest.writeString(name_table);
    }


    public static final Parcelable.Creator<PositionsParcelable> CREATOR = new Parcelable.Creator<PositionsParcelable>()
    {
        public PositionsParcelable createFromParcel(Parcel in) {
            return new PositionsParcelable(in);
        }

        public PositionsParcelable[] newArray(int size) {
            return new PositionsParcelable[size];
        }
    };

    private PositionsParcelable(Parcel parcel)
    {
        login = parcel.readString();
        position = parcel.readString();
        code = parcel.readString();
        name_table = parcel.readString();
    }
}