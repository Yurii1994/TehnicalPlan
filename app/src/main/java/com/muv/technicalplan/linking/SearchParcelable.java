package com.muv.technicalplan.linking;


import android.os.Parcel;
import android.os.Parcelable;

public class SearchParcelable  implements Parcelable
{
    public String name;
    public String surname;
    public String surname_father;
    public String login;
    public String enterprise;
    public String image;

    public SearchParcelable(String name, String surname, String surname_father, String login, String enterprise,
                             String image)
    {
        this.name = name;
        this.surname = surname;
        this.surname_father = surname_father;
        this.login = login;
        this.enterprise = enterprise;
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
        dest.writeString(login);
        dest.writeString(enterprise);
        dest.writeString(image);
    }


    public static final Parcelable.Creator<SearchParcelable> CREATOR = new Parcelable.Creator<SearchParcelable>()
    {
        public SearchParcelable createFromParcel(Parcel in) {
            return new SearchParcelable(in);
        }

        public SearchParcelable[] newArray(int size) {
            return new SearchParcelable[size];
        }
    };

    private SearchParcelable(Parcel parcel)
    {
        name = parcel.readString();
        surname = parcel.readString();
        surname_father = parcel.readString();
        login = parcel.readString();
        enterprise = parcel.readString();
        image = parcel.readString();
    }
}