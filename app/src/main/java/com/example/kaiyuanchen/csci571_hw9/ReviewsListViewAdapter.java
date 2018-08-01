package com.example.kaiyuanchen.csci571_hw9;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewsListViewAdapter extends SimpleAdapter {
    /**
     * Constructor
     *
     * @param context  The context where the View associated with this SimpleAdapter is running
     * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
     *                 Maps contain the data for each row, and should include all the entries specified in
     *                 "from"
     * @param resource Resource identifier of a view layout that defines the views for this list
     *                 item. The layout file should include at least those named views defined in "to"
     * @param from     A list of column names that will be added to the Map associated with each
     *                 item.
     * @param to       The views that should display column in the "from" parameter. These should all be
     *                 TextViews. The first N views in this list are given the values of the first N columns
     */
    private Map<String, String> map = new HashMap<>();
    public ReviewsListViewAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        map.put("Jan", "01");
        map.put("Feb", "02");
        map.put("Mar", "03");
        map.put("Apr", "04");
        map.put("May", "05");
        map.put("Jun", "06");
        map.put("Jul", "07");
        map.put("Aug", "08");
        map.put("Sep", "09");
        map.put("Oct", "10");
        map.put("Nov", "11");
        map.put("Dec", "12");
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View view = super.getView(position, convertView, parent);

        ImageView image = view.findViewById(R.id.reviewerIcon);
        String url = (String) ((Map)getItem(position)).get("reviewerIcon");
        if(!url.equals("")) Picasso.with(view.getContext()).load(url).into(image);

        RatingBar ratingBar = view.findViewById(R.id.reviewRating);
        double value = (double) ((Map)getItem(position)).get("reviewRating");
        ratingBar.setRating((float) value);

        TextView reviewTime = view.findViewById(R.id.reviewTime);
        String timeStr = (String) ((Map)getItem(position)).get("reviewTime");
        if(timeStr.indexOf("-") == -1){
            Long timestamp = Long.parseLong(timeStr);
            Date time = new Date(timestamp*1000);
            String[] temp = time.toString().split(" ");
            String year = temp[5];
            String month = map.get(temp[1]);
            String day = temp[2];
            String hourMinSec = temp[3];
            timeStr = year + "-" + month + "-" + day + " " + hourMinSec;
        }
        reviewTime.setText(timeStr);
        return view;
    }
}
