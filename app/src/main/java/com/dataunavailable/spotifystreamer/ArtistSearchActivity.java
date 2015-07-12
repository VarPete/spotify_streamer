package com.dataunavailable.spotifystreamer;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.dataunavailable.spotifystreamer.common.BundleKeys;
import com.dataunavailable.spotifystreamer.common.ToastUtil;
import com.dataunavailable.spotifystreamer.search.SearchActivity;
import com.dataunavailable.spotifystreamer.search.SearchAdapter;
import com.dataunavailable.spotifystreamer.search.models.SearchResult;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.client.Response;

/**
 * Created by Panayiotis on 6/11/2015.
 */
public class ArtistSearchActivity extends SearchActivity implements Callback<ArtistsPager> {

    private static final String TAG = ArtistSearchActivity.class.getSimpleName();

    private long lastSearch;

    private String lastQuery = "";

    private MenuItem artist_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            findViewById(R.id.clean_launch_text).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.clean_launch_text).setVisibility(View.GONE);
            lastQuery = savedInstanceState.getString(BundleKeys.QUERY);
            if (getSupportActionBar() != null && !TextUtils.isEmpty(lastQuery)) {
                getSupportActionBar().setSubtitle(lastQuery);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BundleKeys.QUERY, lastQuery);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        artist_search = menu.findItem(R.id.artist_search);
        MenuItemCompat.setOnActionExpandListener(artist_search, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) artist_search.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));


        return true;
    }

    protected void performSearch(String query) {
        long currentTime = System.currentTimeMillis();
        // Perform one search a second, regardless of query (Seeing 2 results on emulator)
        if (currentTime - lastSearch < 1000 || lastQuery.equals(query)) {
            Log.d(TAG, "Skipping search");
        }
        Log.d(TAG, "Query: " + query);
        lastQuery = query;
        lastSearch = currentTime;
        SpotifyApi spotifyApi = new SpotifyApi();
        SpotifyService spotifyService = spotifyApi.getService();
        spotifyService.searchArtists(query, this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(query);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Since we're using singleTop - Handle search here
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            performSearch(intent.getStringExtra(SearchManager.QUERY));
            searchList.requestFocus();
        }
    }

    @Override
    public void success(final ArtistsPager artistsPager, Response response) {
        postIfNeeded(new Runnable() {
            @Override
            public void run() {
                resultsList.clear();
                // If there are no results, inform the user through toast
                if (artistsPager == null || artistsPager.artists == null || artistsPager.artists.total == 0) {
                    ToastUtil.showToast(ArtistSearchActivity.this, getString(R.string.no_artists_found));
                    resultsAdapter.notifyDataSetChanged();
                    findViewById(R.id.clean_launch_text).setVisibility(View.VISIBLE);
                    return;
                }

                findViewById(R.id.clean_launch_text).setVisibility(View.GONE);
                // Move to the top
                searchList.scrollToPosition(0);
                resultsList.addAll(buildResultsList(artistsPager.artists.items));
                // remove the old artists
                resultsAdapter.notifyDataSetChanged();
            }

        });

    }

    protected ArrayList<SearchResult> buildResultsList(List items) {
        ArrayList<SearchResult> results = new ArrayList<>();
        if (items == null || items.size() == 0) {
            ToastUtil.showToast(this, getString(R.string.artist_list_error));
            return results;
        }

        for (Object oArtist : items) {
            if (oArtist == null || !(oArtist instanceof Artist)) {
                continue;
            }

            Artist artist = (Artist)oArtist;

            if (artist.images == null || artist.images.size() == 0) {
                results.add(new SearchResult(artist.id, artist.name, null));
            } else {
                results.add(new SearchResult(artist.id, artist.name, artist.images.get(0).url));
            }
        }

        return results;
    }

    @Override
    public void onResultClick(SearchResult result) {
        Log.d(TAG, "Result: " + result.toString());
        if (result == null || TextUtils.isEmpty(result.getId())) {
            ToastUtil.showToast(this, getString(R.string.artist_id_error));
            return;
        }
        Intent artistSearch = new Intent(this, TopSongsActivity.class);
        artistSearch.putExtra(BundleKeys.QUERY, result);
        startActivity(artistSearch);
    }

    /**
     * Don't exit app if user clicked the serach box, simply exit 'search view'
     */
    @Override
    public void onBackPressed() {
        if (MenuItemCompat.isActionViewExpanded(artist_search)) {
            while (MenuItemCompat.isActionViewExpanded(artist_search)) {
                MenuItemCompat.collapseActionView(artist_search);
            }
        } else {
            super.onBackPressed();
        }
    }

}
