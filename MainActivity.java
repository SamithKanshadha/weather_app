package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    BufferedReader reader;
    HttpsURLConnection urlConnection;
    String forecastJsonStr,city,muni;
    FusedLocationProviderClient fusedLocationProviderClient;
    SharedPreferences SP;
    ListView l;
    String[][] bundles = new String[7][7];

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED){
            getLocation();
        }
        else {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }


    }


    @SuppressLint("MissingPermission")
    public void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if(location != null){
                      try {
                          Geocoder geocoder = new Geocoder(MainActivity.this,Locale.getDefault());

                          List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                          FetchData f = new FetchData();
                          f.execute(Double.toString(addresses.get(0).getLongitude()),Double.toString(addresses.get(0).getLatitude()),"Celsius",addresses.get(0).getAddressLine(0));
                          TextView current_city = (TextView) findViewById(R.id.current_city);
                          current_city.setText(addresses.get(0).getAddressLine(0));

                   }
                   catch (IOException e){
                       e.printStackTrace();
                   }
                }
            }
        });
    }



    @Override
    public void onRestart() {
        super.onRestart();

        String[] geo = new String[2];

         SP = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);


        city = SP.getString("enter_city","Colombo");

        GeoLocation geoAddr = new GeoLocation();
        geo = geoAddr.getAddress(city,getApplicationContext());


        SP = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String tempUnit = SP.getString("select_tempu","Celsius");

        FetchData f = new FetchData();
        f.execute(geo[0],geo[1],tempUnit,city);


        TextView current_city = (TextView) findViewById(R.id.current_city);
        current_city.setText(city);

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()){
            case R.id.about:
                getLocation();
                return true;
            case R.id.settings:
                Intent intent = new Intent(MainActivity.this,SettingActivity.class);
                startActivity(intent);
                return true;

            case R.id.logout:
                Intent logout = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(logout);
                return true;
            default:super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }




    public class FetchData extends AsyncTask<String,Void,String> {

        String cwi,newCity;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

           TextView current_temp = findViewById(R.id.current_temp);
           TextView current_des = findViewById(R.id.current_des);



            try {
                JSONObject weatherData = new JSONObject(forecastJsonStr);
                JSONObject current = weatherData.getJSONObject("current");
                String ct = current.getString("temp");


                JSONArray currnstat = current.getJSONArray("weather");
                JSONObject cws = currnstat.getJSONObject(0);
                cwi = cws.getString("icon");


                String imageUri = "https://openweathermap.org/img/w/"+cwi+".png";
                ImageView ivBasicImage = (ImageView) findViewById(R.id.iconweather);
                Picasso.with(MainActivity.this).load(imageUri).into(ivBasicImage);

                current_des.setText(cws.getString("description"));


                switch (muni){
                    case "metric":current_temp.setText(ct + "°C");break;

                    case "imperial":current_temp.setText(ct + "°F");break;

                }


                String[] temp = new String[7];
                String[] day = new String[7];
                String[] status = new String[7];
              String[] icon_list = new String[7];


                for (int i = 0; i < 7; i++) {

                    JSONArray daily = weatherData.getJSONArray("daily");
                    JSONObject dayObject = daily.getJSONObject(i);

                    JSONObject daytemp = dayObject.getJSONObject("temp");
                    temp[i] = daytemp.getString("day");

                    JSONArray weatherstat = dayObject.getJSONArray("weather");
                    JSONObject ws = weatherstat.getJSONObject(0);
                    status[i] = ws.getString("description");


                    long d = Integer.parseInt(dayObject.getString("dt"));
                    Date dateFormat = new java.util.Date(d * 1000);
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                    String weekday = sdf.format(dateFormat );
                    day[i] = weekday;

                    icon_list[i] = ws.getString("icon");

                }

                for(int j=0;j<7;j++){

                    JSONArray daily = weatherData.getJSONArray("daily");
                    JSONObject dayObject = daily.getJSONObject(j);

                    long d = Integer.parseInt(dayObject.getString("dt"));
                    Date dateFormat = new java.util.Date(d * 1000);
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                    bundles[j][0] = sdf2.format(dateFormat );

                    bundles[j][1] =newCity;

                    JSONArray weatherstat = dayObject.getJSONArray("weather");
                    JSONObject ws = weatherstat.getJSONObject(0);
                    bundles[j][2] = ws.getString("icon");

                    JSONObject daytemp = dayObject.getJSONObject("temp");
                    bundles[j][3] =daytemp.getString("day");

                    bundles[j][4] =ws.getString("description");

                    bundles[j][5] =dayObject.getString("humidity");;

                }


                DailyWeatherList adapter = new DailyWeatherList(MainActivity.this,day,status,temp,icon_list,muni);
                l = findViewById(R.id.daily_weather);
                l.setAdapter(adapter);

                l.setOnItemClickListener((parent, view, position, id) -> {

                    for(int x=0;x<7;x++){
                        if(position == x){

                            Bundle b = new Bundle();
                            b.putStringArray("info", new String[]{bundles[x][0], bundles[x][1],bundles[x][2],bundles[x][3],bundles[x][4],bundles[x][5],muni});
                            Intent intent = new Intent(MainActivity.this,InfoActivity.class);
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                    }

                });


            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }


        @Override
        protected String doInBackground(String... data) {

            newCity = data[3];

            try {

                switch (data[2]){
                    case "Celsius": muni = "metric";break;
                    case "Fahrenheit": muni = "imperial";break;
                }

                final String api_key = "a90414b708987e236f5ee23d14099791";
                final String Base_URL = "https://api.openweathermap.org/data/2.5/onecall?lat="+data[1]+"&lon="+data[0]+"&units="+muni+"&exclude=minutely,hourly,alerts&appid="+api_key;

                URL url = new URL(Base_URL);

                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line1;

                while ((line1 = reader.readLine()) != null) {
                    buffer.append(line1 + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                forecastJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e("Hi", "Error", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Hi", "Error closing stream", e);
                    }
                }
            }
            return null;

        }
    }

    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Exit");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Do you want to exit from the app?")
                .setCancelable(false)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null)
        {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

    }
}