package com.dataunavailable.spotifystreamer;

import android.os.Bundle;
import android.util.Log;

import com.dataunavailable.spotifystreamer.common.BundleKeys;
import com.dataunavailable.spotifystreamer.common.SpotifyMaps;
import com.dataunavailable.spotifystreamer.common.ToastUtil;
import com.dataunavailable.spotifystreamer.search.SearchActivity;
import com.dataunavailable.spotifystreamer.search.models.SearchResult;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by Panayiotis on 7/11/2015.
 */
public class TopSongsActivity extends SearchActivity implements Callback<Tracks> {

    private static final String TAG = TopSongsActivity.class.getSimpleName();
    private static final Integer SMALL_MAX_SIZE = 200;
    private static final int LARGE_MAX_SIZE = 640;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Find out what artist we are supposed to be getting top tracks for
        SearchResult artistResult = getIntent().getParcelableExtra(BundleKeys.QUERY);

        // Show a toast and totally return to where we came from
        if (artistResult == null) {
            ToastUtil.showToast(this, getString(R.string.id_notfound));
            finish();
            return;
        }

        // Show the user the artist name that was selected as subtitle (if actionbar is there)
        if (getSupportActionBar() != null && artistResult.getLine1() != null) {
            getSupportActionBar().setSubtitle(artistResult.getLine1());
        }

        // We only need to search if there is no saved instance
        if (savedInstanceState == null) {
            performSearch(artistResult.getId());
        }
    }

    protected void performSearch(String artistId) {
        SpotifyApi spotifyApi = new SpotifyApi();
        spotifyApi.getService().getArtistTopTrack(artistId, SpotifyMaps.COUNTRY_MAP, this);
    }

    @Override
    public void success(final Tracks tracksPager, Response response) {
        Log.d(TAG, "Got results");
        postIfNeeded(new Runnable() {
            @Override
            public void run() {
                resultsList.clear();
                if (tracksPager == null || tracksPager.tracks == null || tracksPager.tracks.size() == 0) {
                    ToastUtil.showToast(TopSongsActivity.this, getString(R.string.no_top_tracks));
                    resultsAdapter.notifyDataSetChanged();
                    return;
                }
                searchList.scrollToPosition(0);
                resultsList.addAll(buildTracksList(tracksPager.tracks));
                resultsAdapter.notifyDataSetChanged();
            }
        });
    }

    private Image compareImages(Image existing, Image newImage, int bound) {
        if (existing == null) {
            return newImage;
        }

        if (existing.width < newImage.width && existing.height < newImage.height) {
            // Passed test 1
            if (newImage.width < bound && newImage.height < bound) {
                return newImage;
            }
        }

        return existing;
    }

    private ArrayList<SearchResult> buildTracksList(List<Track> tracks) {
        ArrayList<SearchResult> tracksList = new ArrayList<>();
        if (tracks == null) {
            return tracksList;
        }

        for (Track track : tracks) {
            if (track == null) {
                continue;
            }

            List<Image> trackIcons = track.album.images;

            String smallUrl = null;
            String largeUrl = null;

            // Find the large/small icons
            if (trackIcons.size() > 0) {
                Image smallImage = null;
                Image largeImage = null;
                // If there are only 2 icons, just pick and arrange
                if (trackIcons.size() == 2) {
                    smallImage = trackIcons.get(0);
                    largeImage = trackIcons.get(1);
                    // Swap small and large if required
                    smallImage = compareImages(smallImage, largeImage, SMALL_MAX_SIZE);

                } else {
                    for (Image image : trackIcons) {
                        if (image == null) {
                            continue;
                        }

                        smallImage = compareImages(smallImage, image, SMALL_MAX_SIZE);
                        largeImage = compareImages(largeImage, image, LARGE_MAX_SIZE);
                    }
                }

                smallUrl = smallImage.url;
                largeUrl = largeImage.url;
            }

            SearchResult trackResult = new SearchResult(track.id, track.name, track.album.name, smallUrl);
            trackResult.putString("previewUrl", track.preview_url);
            trackResult.putString("largeUrl", largeUrl);
            tracksList.add(trackResult);

        }

        return tracksList;
    }

    @Override
    public void failure(RetrofitError error) {
        ToastUtil.showToast(this, getString(R.string.error, error.getResponse().getReason()));
    }

    @Override
    public void onResultClick(SearchResult result) {
        ToastUtil.showToast(this, getString(R.string.debug_result_clicked, result.getId(), result.getLine1()));
    }
}
