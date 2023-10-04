package com.example.weatherapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;

public class GeoLocation {

    public String[] getAddress(String city, Context context){
        String[] coordinates = new String[2];

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = null;
        try {
            List addressList = geocoder.getFromLocationName(city,1);
            if(addressList != null && addressList.size() >0){
                Address address = (Address) addressList.get(0);
                coordinates[0] = Double.toString(address.getLongitude());
                coordinates[1] = Double.toString(address.getLatitude());
                Log.d("ADebugTag", "Cordi: " + coordinates[0] + ","+coordinates[1]);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return coordinates;

    }

    }



