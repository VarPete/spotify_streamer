package com.dataunavailable.spotifystreamer.search;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dataunavailable.spotifystreamer.R;

/**
 * Created by Panayiotis on 7/10/2015.
 */
public class SearchResultsViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = SearchResultsViewHolder.class.getSimpleName();

    private TextView topRow;
    private TextView bottomRow;
    private ImageView iconImage;
    private View divider;

    public SearchResultsViewHolder(View itemView) {
        super(itemView);
        topRow = (TextView) itemView.findViewById(R.id.result_text_row1);
        bottomRow = (TextView) itemView.findViewById(R.id.result_text_row2);
        iconImage = (ImageView) itemView.findViewById(R.id.result_image);
        divider = itemView.findViewById(R.id.divider);
    }

    public TextView getTopRow() {
        return topRow;
    }

    public TextView getBottomRow() {
        return bottomRow;
    }

    public ImageView getIconImage() {
        return iconImage;
    }

    public View getDivider() {
        return divider;
    }

    public void setOnClickListener( View.OnClickListener listener ) {
        if( itemView != null ) {
            Log.d(TAG, "ClickListener set");
            itemView.setOnClickListener(listener);
        }
    }


}