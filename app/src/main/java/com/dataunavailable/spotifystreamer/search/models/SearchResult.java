package com.dataunavailable.spotifystreamer.search.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Panayiotis on 7/11/2015.
 */
public class SearchResult implements Parcelable {

    private JSONObject resultsObject = new JSONObject();

    protected SearchResult(Parcel in) {
        try {
            resultsObject = new JSONObject(in.readString());
        } catch (JSONException e) {
            // Since this is debug information, and we don't have a context here, it's alright that it's not localized...
            throw new IllegalArgumentException("Could not build result", e);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(resultsObject.toString());
    }

    public static final Creator<SearchResult> CREATOR = new Creator<SearchResult>() {
        @Override
        public SearchResult createFromParcel(Parcel in) {
            return new SearchResult(in);
        }

        @Override
        public SearchResult[] newArray(int size) {
            return new SearchResult[size];
        }
    };

    public SearchResult(String id, String line1, String imageUri) {
        buildJSONObject(id, line1, null, imageUri);
    }

    public SearchResult(String id, String line1, String line2, String imageUrl) {
        buildJSONObject(id, line1, line2, imageUrl);
    }

    private void buildJSONObject(String id, String line1, String line2, String imageUrl) {
        try {
            resultsObject.accumulate("id", id);
            resultsObject.accumulate("line1", line1);
            resultsObject.accumulate("line2", line2);
            resultsObject.accumulate("imageUrl", imageUrl);
        } catch (JSONException e) {
            throw new IllegalArgumentException("Could not build result: ", e);
        }
    }

    public void putString(String key, String value) {
        try {
            resultsObject.put(key, value);
        } catch (JSONException e) {
            // Since this is debug information, and we don't have a context here, it's alright that it's not localized...
            throw new IllegalArgumentException(String.format("Unable to set key: %s", key), e);
        }
    }

    public String getString(String key) {
        return resultsObject.optString(key);
    }

    public String getLine1() {
        return resultsObject.optString("line1");
    }

    public String getImageUrl() {
        return resultsObject.optString("imageUrl");
    }

    public String getLine2() {
        return resultsObject.optString("line2");
    }

    public String getId() {
        return resultsObject.optString("id");
    }

    @Override
    public String toString() {
        return resultsObject.toString();
    }
}
