package com.example.jack.myapplication.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 智能家居有关的实体
 */
public class Jiaju implements Parcelable {
    private String name;
    private String description;
    private String url;
    private String image;

    public Jiaju() {
    }

    public Jiaju(String name, String description, String url, String image) {
        this.name = name;
        this.description = description;
        this.url = url;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.url);
        dest.writeString(this.image);
    }

    protected Jiaju(Parcel in) {
        this.name = in.readString();
        this.description = in.readString();
        this.url = in.readString();
        this.image = in.readString();
    }

    public static final Parcelable.Creator<Jiaju> CREATOR = new Parcelable.Creator<Jiaju>() {
        @Override
        public Jiaju createFromParcel(Parcel source) {
            return new Jiaju(source);
        }

        @Override
        public Jiaju[] newArray(int size) {
            return new Jiaju[size];
        }
    };
}
