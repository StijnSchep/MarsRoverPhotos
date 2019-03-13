package com.personapplication.stijn.marsroverphotos.Config;

import com.personapplication.stijn.marsroverphotos.R;

public class Config {

    /* BEGIN URL VALUES */
    public static final String BASE_URL = "https://api.nasa.gov/mars-photos/api/v1/";

    public static final String ROVERS = "rovers";
    public static final String MANIFEST = "manifests";

    public static final String PHOTOS = "photos";

    public static final String SOL_DATE = "sol";
    public static final String SOL_DATE_VALUE = "1500";

    public static final String CAMERA_MAST = "mast";

    public static final String API_KEY = "API_KEY";
    public static final String API_KEY_VALUE = "U11DDqZgiODJA1aNvWkCdVxJi15I4yWSEILzdF77";
    /* END URL VALUES */


    /* BEGIN JSON VALUES */
    public static final String ID = "id";
    public static final String IMG_URL = "img_src";

    public static final String SOL = "sol";
    public static final String EARTH_DATE = "earth_date";

    public static final String CAMERA = "camera";
    public static final String CAMERA_NAME = "full_name";
    public static final String CAMERA_SHORT = "name";

    public static final String ROVER = "rover";
    public static final String ROVER_NAME = "name";
    public static final String ROVER_TOTAL_PHOTOS = "total_photos";

    /* END JSON VALUES */


    /* BEGIN ASYNC LOADER VALUES  */

    public static final int PHOTO_FETCHING_LOADER = 22;

    public static final String SEARCH_QUERY_URL_EXTRA = "query";

    /* END ASYNC LOADER VALUES */


    /* BEGIN EXTRAS VALUES */

    public static final String JSON_PHOTOS = "json";

    public static final String JSON_PHOTO = "json";

    public static final String ROVER_PREFERENCE = "preference_rover";

    public static final String LANGUAGE_PREFERENCE = "language";

    public static final String LANGUAGE_DEFAULT = "default";
    public static final String LANGUAGE_ENGLISH = "english";
    public static final String LANGUAGE_DUTCH = "dutch";

    /* END EXTRAS VALUES  */


    /* BEGIN ROVER NAMES */

    public static final String CURIOSITY = "curiosity";
    public static final String OPPORTUNITY = "opportunity";
    public static final String SPIRIT = "spirit";

    /* END ROVER NAMES */


    // TODO Fix Detail Screen translation if it's loaded in portrait mode

}
