package com.dataunavailable.spotifystreamer.search;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dataunavailable.spotifystreamer.R;
import com.dataunavailable.spotifystreamer.common.BundleKeys;
import com.dataunavailable.spotifystreamer.common.ToastUtil;
import com.dataunavailable.spotifystreamer.search.models.SearchResult;

import java.util.ArrayList;

import retrofit.RetrofitError;

/**
 * Created by Panayiotis on 6/11/2015.
 */
public abstract class SearchActivity extends AppCompatActivity implements SearchAdapter.OnResultClickListener {

    private static final String TAG = SearchActivity.class.getSimpleName();

    private static final Handler uiHandler = new Handler(Looper.getMainLooper());

    protected ArrayList<SearchResult> resultsList = new ArrayList<>();

    protected SearchAdapter resultsAdapter;

    protected RecyclerView searchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentLayoutId());

        if (savedInstanceState != null) {
            resultsList = savedInstanceState.getParcelableArrayList(BundleKeys.LAST_RESULTS);
        }

        searchList = (RecyclerView) findViewById(R.id.search_list);
        resultsAdapter = new SearchAdapter(this, resultsList, this);

        searchList.setClickable(true);
        searchList.setLayoutManager(new LinearLayoutManager(this));

        searchList.setAdapter(resultsAdapter);
    }

    private int getContentLayoutId() {
        return R.layout.activity_spotify_search;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BundleKeys.LAST_RESULTS, resultsList);
    }

    /**
     * Post action to the UI thread if we're not already there
     *
     * @param action action to perform
     */
    protected void postIfNeeded(final Runnable action) {
        if (Thread.currentThread() == getMainLooper().getThread()) {
            action.run();
        } else {
            uiHandler.post(action);
        }
    }

    public void failure(RetrofitError error) {
        ToastUtil.showToast(this, getString(R.string.spotify_error_format, error.getMessage()));
    }

    protected abstract void performSearch(String query);
}
