package com.muv.technicalplan.linking;


import android.os.Parcel;
import android.os.Parcelable;

public class UsersParcelable implements Parcelable
{
    public String name;
    public String surname;
    public String surname_father;
    public String enterprise;
    public String position;
    public String login;
    public String email;
    public String image;

    public UsersParcelable(String name, String surname, String surname_father, String enterprise, String position, String login,
                         String email, String image)
    {
        this.name = name;
        this.surname = surname;
        this.surname_father = surname_father;
        this.enterprise = enterprise;
        this.position = position;
        this.login = login;
        this.email = email;
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name);
        dest.writeString(surname);
        dest.writeString(surname_father);
        dest.writeString(enterprise);
        dest.writeString(position);
        dest.writeString(login);
        dest.writeString(email);
        dest.writeString(image);
    }


    public static final Parcelable.Creator<UsersParcelable> CREATOR = new Parcelable.Creator<UsersParcelable>()
    {
        public UsersParcelable createFromParcel(Parcel in) {
            return new UsersParcelable(in);
        }

        public UsersParcelable[] newArray(int size) {
            return new UsersParcelable[size];
        }
    };

    private UsersParcelable(Parcel parcel)
    {
        name = parcel.readString();
        surname = parcel.readString();
        surname_father = parcel.readString();
        enterprise = parcel.readString();
        position = parcel.readString();
        login = parcel.readString();
        email = parcel.readString();
        image = parcel.readString();
    }
}