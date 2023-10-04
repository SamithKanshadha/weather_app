package com.example.weatherapp;



import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;


public class DailyWeatherList extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] day;
    private final String[] status;
    private final String[] temp;
    private final String[] icon_list;
    private final String m;

    public DailyWeatherList(Activity context,String[] day,String[] status,String[] temp,String[] icon_list,String muni){
        super(context,R.layout.daily_weather_list,day);

        this.context = context;
        this.day = day;
        this.status = status;
        this.temp = temp;
        this.icon_list = icon_list;
        this.m = muni;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater =context.getLayoutInflater();

        View rowView = inflater.inflate(R.layout.daily_weather_list,null,true);
        TextView txtday = (TextView) rowView.findViewById(R.id.day);
        TextView txtstatus = (TextView) rowView.findViewById(R.id.weather_status);
        TextView txttemp = (TextView) rowView.findViewById(R.id.temp);
        ImageView icon = (ImageView) rowView.findViewById(R.id.weather_icon);

        txtday.setText(day[position]);
        txtstatus.setText(status[position]);


        Picasso.with(context).load("https://openweathermap.org/img/w/"+icon_list[position]+".png").into(icon);

        switch (m){
            case "metric":txttemp.setText(temp[position] + "°C");break;

            case "imperial":txttemp.setText(temp[position] + "°F");break;

        }

        return rowView;

    };


    }
