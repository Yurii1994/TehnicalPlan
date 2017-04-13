package com.muv.technicalplan.linking;


import android.os.Parcel;
import android.os.Parcelable;

public class LinkingParcelable implements Parcelable
{
    public String where_user;
    public String from_user;
    public String enterprise;
    public String position;
    public String code;
    public String state;
    public String name_table;

    public LinkingParcelable(String where_user, String from_user, String enterprise, String position, String code,
                           String state, String name_table)
    {
        this.where_user = where_user;
        this.from_user = from_user;
        this.enterprise = enterprise;
        this.position = position;
        this.code = code;
        this.state = state;
        this.name_table = name_table;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(where_user);
        dest.writeString(from_user);
        dest.writeString(enterprise);
        dest.writeString(position);
        dest.writeString(code);
        dest.writeString(state);
        dest.writeString(name_table);
    }


    public static final Parcelable.Creator<LinkingParcelable> CREATOR = new Parcelable.Creator<LinkingParcelable>()
    {
        public LinkingParcelable createFromParcel(Parcel in) {
            return new LinkingParcelable(in);
        }

        public LinkingParcelable[] newArray(int size) {
            return new LinkingParcelable[size];
        }
    };

    private LinkingParcelable(Parcel parcel)
    {
        where_user = parcel.readString();
        from_user = parcel.readString();
        enterprise = parcel.readString();
        position = parcel.readString();
        code = parcel.readString();
        state = parcel.readString();
        name_table = parcel.readString();
    }
}