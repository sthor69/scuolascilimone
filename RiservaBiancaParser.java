package com.storassa.android.scuolasci;

import java.io.BufferedReader;

public class RiservaBiancaParser {
    
    public enum Meteo {
        SUN,
        CLOUD,
        RAIN,
        SNOW,
        SUN_CLOUD_MIX,
        LIGHTNING,
        RAIN_SNOW_MIX
    }
    BufferedReader htmlString;
    
    public RiservaBiancaParser(BufferedReader s) {
        htmlString = s;
    }
    
    public int getMinTemp() {
        return 0;
    }
    
    public int getMaxTemp() {
        return 0;
    }
    
    public int getMinSnow() {
        return 0;
    }
    
    public int getMaxSnow() {
        return 0;
    }
    
    public int getLastSnow() {
        return 0;
    }
    
    public String getSnowQuality() {
        return "Good";
    }
    
    public int getAvalDanger() {
        return 0;
    }
    
    public String getNote() {
        return "Goodby";
    }
    
    public int getMeteoResource() {
        return (R.drawable.rain_icon);
    }
    
    
}
