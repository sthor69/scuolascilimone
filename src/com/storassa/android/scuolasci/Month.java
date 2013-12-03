package com.storassa.android.scuolasci;

import android.content.Context;
import android.content.res.Resources;

public enum Month {

    january,
    february,
    march,
    april,
    may,
    june,
    july,
    august,
    september,
    october,
    november,
    december;
    
    public String getLabel(Context context) {
        Resources res = context.getResources();
        int resId = res.getIdentifier(this.name(), "string", context.getPackageName());
        if (0 != resId) {
            return (res.getString(resId));
        }
        return (name());
    }

}
