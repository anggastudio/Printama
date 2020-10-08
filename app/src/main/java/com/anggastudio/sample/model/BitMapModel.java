package com.anggastudio.sample.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BitMapModel implements Parcelable {
    String bitmapName;
    String bitmapImageBase64;
    int logoWidth;
    int logoHeight;
    int logoMarginLeft;

    public BitMapModel() {
    }

    protected BitMapModel(Parcel in) {
        bitmapName = in.readString();
        bitmapImageBase64 = in.readString();
        logoWidth = in.readInt();
        logoHeight = in.readInt();
        logoMarginLeft = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bitmapName);
        dest.writeString(bitmapImageBase64);
        dest.writeInt(logoWidth);
        dest.writeInt(logoHeight);
        dest.writeInt(logoMarginLeft);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BitMapModel> CREATOR = new Creator<BitMapModel>() {
        @Override
        public BitMapModel createFromParcel(Parcel in) {
            return new BitMapModel(in);
        }

        @Override
        public BitMapModel[] newArray(int size) {
            return new BitMapModel[size];
        }
    };

    public String getBitmapName() {
        return bitmapName;
    }

    public void setBitmapName(String bitmapName) {
        this.bitmapName = bitmapName;
    }

    public String getBitmapImageBase64() {
        return bitmapImageBase64;
    }

    public void setBitmapImageBase64(String bitmapImageBase64) {
        this.bitmapImageBase64 = bitmapImageBase64;
    }

    public int getLogoWidth() {
        return logoWidth;
    }

    public void setLogoWidth(int logoWidth) {
        this.logoWidth = logoWidth;
    }

    public int getLogoHeight() {
        return logoHeight;
    }

    public void setLogoHeight(int logoHeight) {
        this.logoHeight = logoHeight;
    }

    public int getLogoMarginLeft() {
        return logoMarginLeft;
    }

    public void setLogoMarginLeft(int logoMarginLeft) {
        this.logoMarginLeft = logoMarginLeft;
    }
}
