package com.Prithviraj8.Sleefax.Services;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetDirectionsData extends AsyncTask<Object,String,String> {

    GoogleMap googleMap;
    String url;
    LatLng origin,destination;

    HttpURLConnection httpURLConnection = null;
    String data = "";
    InputStream inputStream = null;
    Context c;

    public GetDirectionsData(Context c) {
        this.c = c;
    }

    @Override
    protected String doInBackground(Object... objects) {
        googleMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        origin = (LatLng) objects[2];
        destination = (LatLng) objects[3];

        try {
            URL myUrl = new URL(url);
            httpURLConnection = (HttpURLConnection) myUrl.openConnection();
            httpURLConnection.connect();
            inputStream = httpURLConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine())!= null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;

    }

    @Override
    protected void onPostExecute(String s) {

        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");

            int count = jsonArray.length();
            String[] polyLineArray = new String[count];

            JSONObject jsonObject1;

            for(int i =0;i<count;i++){
                jsonObject1 = jsonArray.getJSONObject(i);
                String polylgone = jsonObject1.getJSONObject("polyline").getString("points");
                polyLineArray[i] = polylgone;
            }

            int count2 = polyLineArray.length;
            for(int i = 0;i<count2;i++){
                PolylineOptions options = new PolylineOptions();
                options.color(Color.BLUE);
                options.width(10);
                options.addAll(PolyUtil.decode(polyLineArray[i]));
                googleMap.addPolyline(options);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        super.onPostExecute(s);
    }
}


