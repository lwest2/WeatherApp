package com.example.sal.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Text;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{

    private final View mWindow;
    private Context mContext;

    public CustomInfoWindowAdapter(Context mContext) {
        this.mContext = mContext;
        mWindow = LayoutInflater.from(mContext).inflate(R.layout.custom_info_window, null);
    }

    private void RenderWindowText(Marker marker, View view)
    {
        String title = marker.getTitle();
        TextView textView = (TextView) view.findViewById(R.id.title);

        if(!title.equals(""))
        {
            textView.setText(title);
        }

        String snippet = marker.getSnippet();
        TextView textViewsnippet = (TextView) view.findViewById(R.id.snippet);

        if(!snippet.equals(""))
        {
            textViewsnippet.setText(snippet);
        }

    }

    @Override
    public View getInfoWindow(Marker marker) {
        RenderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        RenderWindowText(marker, mWindow);
        return mWindow;
    }
}
