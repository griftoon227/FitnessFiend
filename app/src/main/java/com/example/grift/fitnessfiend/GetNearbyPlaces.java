package com.example.grift.fitnessfiend;

import android.os.AsyncTask;


// used for Google Map API implementation
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class GetNearbyPlaces extends AsyncTask<Object, String, String>
{
    private String googleplaceData;
    private GoogleMap mMap;



    @Override
    protected String doInBackground(Object... objects)
    {
        mMap = (GoogleMap) objects[0];

        String url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();

        try
        {
            googleplaceData = downloadUrl.ReadTheURL(url);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return googleplaceData;
    }

    @Override
    protected void onPostExecute(String s)
    {
        List<HashMap<String, String>> nearbyPlacesList;
        DataParser dataParser = new DataParser();
        nearbyPlacesList = dataParser.parse(s);

        DisplayNearbyPlaces(nearbyPlacesList);
    }

    private void DisplayNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList)
    {
        for (int i=0; i < nearbyPlacesList.size(); i++)
        {
            MarkerOptions markerOptions = new MarkerOptions();

            HashMap<String, String> googleNearbyPlace = nearbyPlacesList.get(i);

            String nameOfPlace = googleNearbyPlace.get("place_name");

            String vicinity = googleNearbyPlace.get("vicinity");

            double lat = Double.parseDouble(Objects.requireNonNull(googleNearbyPlace.get("lat")));
            double lng = Double.parseDouble(Objects.requireNonNull(googleNearbyPlace.get("lng")));

            // extras from before
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(nameOfPlace + " : " + vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));


        }
    }
}

