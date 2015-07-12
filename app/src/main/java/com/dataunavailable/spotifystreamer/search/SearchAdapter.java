package com.dataunavailable.spotifystreamer.search;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dataunavailable.spotifystreamer.R;
import com.dataunavailable.spotifystreamer.common.ToastUtil;
import com.dataunavailable.spotifystreamer.search.models.SearchResult;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Panayiotis on 6/14/2015.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchResultsViewHolder> {

    public interface OnResultClickListener {
        void onResultClick(SearchResult result);
    }

    private static final String TAG = SearchAdapter.class.getSimpleName();

    protected Context context;

    protected List<SearchResult> resultsList;

    protected View.OnClickListener rowClickListener;

    /**
     * Abstract implementation to be used by artists search and song search
     *
     * @param context       context for the adapter
     * @param resultsList   list of artists to be displayed by this Adapter
     * @param clickListener action to perform when a row is clicked
     */
    public SearchAdapter(@NonNull final Context context, @NonNull List<SearchResult> resultsList, @Nullable final OnResultClickListener clickListener) {
        this.context = context;
        this.resultsList = resultsList;
        if (clickListener != null) {
            this.rowClickListener = new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    TextView topRow = (TextView) v.findViewById(R.id.result_text_row1);

                    if (topRow.getTag() != null && topRow.getTag() instanceof SearchResult) {
                        clickListener.onResultClick((SearchResult)topRow.getTag());
                    } else {
                        ToastUtil.showToast(context, context.getString(R.string.id_notfound));
                    }
                }
            };
        }
    }

    @Override
    public SearchResultsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View rowView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.results_row, viewGroup, false);

        SearchResultsViewHolder viewHolder = new SearchResultsViewHolder(rowView);
        viewHolder.setIsRecyclable(true);

        return viewHolder;
    }

    @Override
    @CallSuper
    public void onBindViewHolder(SearchResultsViewHolder viewHolder, int position) {
        if (viewHolder == null || position < 0) {
            return;
        }

        viewHolder.setOnClickListener(rowClickListener);
        viewHolder.getDivider().setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);

        SearchResult result = resultsList.get(position);
        Log.d(TAG, "Result: " + result.toString());
        viewHolder.getTopRow().setText(result.getLine1());
        viewHolder.getTopRow().setTag(result);

        if( TextUtils.isEmpty(result.getLine2())) {
            viewHolder.getBottomRow().setVisibility(View.GONE);
        } else {
            viewHolder.getBottomRow().setText(result.getLine2());
            viewHolder.getBottomRow().setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(result.getImageUrl())) {
            viewHolder.getIconImage().setImageResource(R.drawable.search_placeholder);
        } else {
            Picasso.with(context).load(result.getImageUrl()).into(viewHolder.getIconImage());
        }
    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }

}
