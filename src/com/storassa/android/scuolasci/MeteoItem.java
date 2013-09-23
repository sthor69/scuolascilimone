package com.storassa.android.scuolasci;

import android.os.Parcel;
import android.os.Parcelable;

public class MeteoItem implements Parcelable {
    final int iconResourceId;
    final double minTemp;
    final double maxTemp;
    final double humidity;
    final double precipit;
    final double minSnow;
    final double maxSnow;
    final double lastSnow;

    public MeteoItem(int _iconResourceId, double _minTemp, double _maxTemp,
            double _humidity, double _precipit, double _minSnow,
            double _maxSnow, double _lastSnow) {
        iconResourceId = _iconResourceId;
        minTemp = _minTemp;
        maxTemp = _maxTemp;
        humidity = _humidity;
        precipit = _precipit;
        minSnow = _minSnow;
        maxSnow = _maxSnow;
        lastSnow = _lastSnow;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getPrecipit() {
        return precipit;
    }
    
    public double getMinSnow() {
        return minSnow;
    }
    
    public double getMaxSnow() {
        return maxSnow;
    }
    
    public double getLastSnow() {
        return lastSnow;
    }
    
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(iconResourceId);
        out.writeDouble(minTemp);
        out.writeDouble(maxTemp);
        out.writeDouble(humidity);
        out.writeDouble(precipit);
        out.writeDouble(minSnow);
        out.writeDouble(maxSnow);
        out.writeDouble(lastSnow);
    }

    public static final Parcelable.Creator<MeteoItem> CREATOR = new Parcelable.Creator<MeteoItem>() {
        public MeteoItem createFromParcel(Parcel in) {
            return new MeteoItem(in);
        }

        public MeteoItem[] newArray(int size) {
            return new MeteoItem[size];
        }
    };

    private MeteoItem(Parcel in) {
        iconResourceId = in.readInt();
        minTemp = in.readDouble();
        maxTemp = in.readDouble();
        humidity = in.readDouble();
        precipit = in.readDouble();
        minSnow = in.readDouble();
        maxSnow = in.readDouble();
        lastSnow = in.readDouble();
    }

}
