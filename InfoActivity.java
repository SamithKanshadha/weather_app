package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);


        Bundle b = this.getIntent().getExtras();
        String[] info = b.getStringArray("info");

        String m = info[6];


        TextView date = findViewById(R.id.date);
        TextView city = findViewById(R.id.city);
        ImageView icon = findViewById(R.id.wicon);
        TextView temp = findViewById(R.id.temp_day);
        TextView des = findViewById(R.id.description);
        TextView hum = findViewById(R.id.humidity);

        date.setText(info[0]);
        city.setText(info[1]);
        Picasso.with(InfoActivity.this).load("https://openweathermap.org/img/w/"+info[2]+".png").into(icon);

        switch (m){
            case "metric":temp.setText(info[3] + "°C");break;

            case "imperial":temp.setText(info[3] + "°F");break;

        }

        des.setText(info[4]);
        hum.setText("Humidity: "+info[5]+"%");

    }
}