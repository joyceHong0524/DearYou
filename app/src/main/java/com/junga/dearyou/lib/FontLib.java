package com.junga.dearyou.lib;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

public class FontLib {

    Activity mAcitivy;

    public FontLib(){
    }

    public void setFont(Activity mAcitivy, String fontName, TextView view){
        String fontPath="fonts/"+fontName+".ttf";
        Typeface custom_font = Typeface.createFromAsset(mAcitivy.getAssets(),fontPath);
        view.setTypeface(custom_font);
    }

}
