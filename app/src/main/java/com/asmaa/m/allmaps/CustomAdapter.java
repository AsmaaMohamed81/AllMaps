package com.asmaa.m.allmaps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context mContext;

    public CustomAdapter(Context mContext) {
        this.mContext = mContext;
        mWindow= LayoutInflater.from(mContext).inflate(R.layout.custom_info,null);
    }


    private void rendowWindow(Marker marker, View view){

        String title=marker.getTitle();
        TextView TEXtitle=view.findViewById(R.id.title);

        if(!title.equals("")){
            TEXtitle.setText(title);
        }

        String snipte=marker.getSnippet();
        TextView TEXsnipte=view.findViewById(R.id.snippte);

        if(!snipte.equals("")){
            TEXsnipte.setText(snipte);
        }
    }
    @Override
    public View getInfoWindow(Marker marker) {
        rendowWindow(marker,mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        rendowWindow(marker,mWindow);
        return mWindow;
    }
}
