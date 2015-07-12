package com.dataunavailable.spotifystreamer.search;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dataunavailable.spotifystreamer.R;
import com.dataunavailable.spotifystreamer.search.models.SearchResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Panayiotis on 6/11/2015.
 */
public class SearchFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_spotify_search, container, false);
    }

}
