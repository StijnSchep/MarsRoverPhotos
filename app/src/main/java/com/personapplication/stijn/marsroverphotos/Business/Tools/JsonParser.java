package com.personapplication.stijn.marsroverphotos.Business.Tools;

import android.util.Log;

import com.personapplication.stijn.marsroverphotos.Config.Config;
import com.personapplication.stijn.marsroverphotos.Domain.Photo;
import com.personapplication.stijn.marsroverphotos.Domain.Rover;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonParser {
    private static final String TAG = JsonParser.class.getSimpleName();

    //Given a JSON string, converts it to a list of Photos, only works on JSON from the NASA API
    public static List<Photo> parsePhotos(String source) {
        Log.d(TAG, "Started parsing photos with source: " + source);

        List<Photo> photos = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(source);
            JSONArray JSONphotos = root.getJSONArray("photos");

            for(int i = 0; i < JSONphotos.length(); i++) {
                JSONObject JSONphoto = JSONphotos.getJSONObject(i);

                int ID = JSONphoto.getInt(Config.ID);
                int sol = JSONphoto.getInt(Config.SOL);
                String img_url = JSONphoto.getString(Config.IMG_URL);
                String earth_date = JSONphoto.getString(Config.EARTH_DATE);

                JSONObject camera = JSONphoto.getJSONObject(Config.CAMERA);
                String camera_name = camera.getString(Config.CAMERA_NAME);
                String camera_name_short = camera.getString(Config.CAMERA_SHORT);

                JSONObject JSONrover = JSONphoto.getJSONObject(Config.ROVER);
                String rover_name = JSONrover.getString(Config.ROVER_NAME);
                int rover_photoAmount = JSONrover.getInt(Config.ROVER_TOTAL_PHOTOS);

                Rover rover = new Rover(rover_name, rover_photoAmount);
                Photo photo = new Photo(ID, sol, camera_name, camera_name_short, earth_date, img_url, rover);
                photos.add(photo);
            }

        } catch(JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        Log.d(TAG, "Fetched " + photos.size() + " photos, sending them to caller...");
        return photos;
    }
}
