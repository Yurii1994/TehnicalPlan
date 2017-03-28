package com.muv.technicalplan.main;


import android.os.Parcel;
import android.os.Parcelable;

public class MapParcelable  implements Parcelable
{
    public int id;
    public String code;
    public String general;
    public String relative;
    public String description;
    public String normal;
    public String lightweight;
    public String light;
    public String date;
    public String comment_manager;
    public String comment_performer;
    public String position;
    public String name_table;
    public String state_performance;
    public String stitched;

    public MapParcelable(int id, String code, String general, String relative, String description, String normal, String lightweight,
                         String light, String date, String comment_manager, String comment_performer, String position,
                         String name_table, String state_performance, String stitched)
    {
        this.id = id;
        this.code = code;
        this.general = general;
        this.relative = relative;
        this.description = description;
        this.normal = normal;
        this.lightweight = lightweight;
        this.light = light;
        this.date = date;
        this.comment_manager = comment_manager;
        this.comment_performer = comment_performer;
        this.position = position;
        this.name_table = name_table;
        this.state_performance = state_performance;
        this.stitched = stitched;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeString(code);
        dest.writeString(general);
        dest.writeString(relative);
        dest.writeString(description);
        dest.writeString(normal);
        dest.writeString(lightweight);
        dest.writeString(light);
        dest.writeString(date);
        dest.writeString(comment_manager);
        dest.writeString(comment_performer);
        dest.writeString(position);
        dest.writeString(name_table);
        dest.writeString(state_performance);
        dest.writeString(stitched);
    }


    public static final Parcelable.Creator<MapParcelable> CREATOR = new Parcelable.Creator<MapParcelable>()
    {
        public MapParcelable createFromParcel(Parcel in) {
            return new MapParcelable(in);
        }

        public MapParcelable[] newArray(int size) {
            return new MapParcelable[size];
        }
    };

    private MapParcelable(Parcel parcel)
    {
        id = parcel.readInt();
        code = parcel.readString();
        general = parcel.readString();
        relative = parcel.readString();
        description = parcel.readString();
        normal = parcel.readString();
        lightweight = parcel.readString();
        light = parcel.readString();
        date = parcel.readString();
        comment_manager = parcel.readString();
        comment_performer = parcel.readString();
        position = parcel.readString();
        name_table = parcel.readString();
        state_performance = parcel.readString();
        stitched = parcel.readString();
    }
}