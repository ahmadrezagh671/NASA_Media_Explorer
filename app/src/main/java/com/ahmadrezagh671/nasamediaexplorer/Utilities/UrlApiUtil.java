package com.ahmadrezagh671.nasamediaexplorer.Utilities;

public class UrlApiUtil {

    public static String getApiUrl(String query,boolean[] filterStates) {
        String url;
        if (query == null || query.isEmpty()){
            url = "https://images-api.nasa.gov/search?year_start=2025";
        }else {
            url = "https://images-api.nasa.gov/search?q=" + query;
        }

        //filterStates : isImageOn, isVideoOn, isAudioOn
        if (filterStates[0])
            url += "&media_type=image";
        else if (filterStates[1])
            url += "&media_type=video";
        else if (filterStates[2])
            url += "&media_type=audio";

        return url;
    }
}
