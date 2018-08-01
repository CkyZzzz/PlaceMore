package com.example.kaiyuanchen.csci571_hw9;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class FavoriteListViewAdapter extends SimpleAdapter {
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
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private List<Map<String, Object>> data;
    private Context context;
    public FavoriteListViewAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.pref = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        editor = pref.edit();
        this.data = (List<Map<String, Object>>) data;
        this.context = context;
    }

    public View getView(final int position, View convertView, final ViewGroup parent){
        View view = super.getView(position, convertView, parent);
        final ImageButton favBtn = view.findViewById(R.id.favoriteInFavorite);
        final Resources res = view.getResources();
        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = ((Map)getItem(position)).get("placeName").toString();
                editor.remove((String) ((Map)getItem(position)).get("place_id"));
                editor.apply();
                data.remove(position);
                FavoriteFragment.changeContent();
                notifyDataSetChanged();
                Toast.makeText(context, temp + " was removed from favorites", Toast.LENGTH_SHORT).show();
            }
        });
        if(!pref.getString((String) ((Map)getItem(position)).get("place_id"), "").equals("")){
            Drawable heart = res.getDrawable(R.drawable.heart_fill_red);
            favBtn.setImageDrawable(heart);
            favBtn.setVisibility(View.VISIBLE);
        }else{
            favBtn.setVisibility(View.GONE);
        }
        ImageView image = view.findViewById(R.id.favoriteIcon);
        String url = (String) ((Map)getItem(position)).get("placeIcon");
        Picasso.with(view.getContext()).load(url).into(image);
        return view;
    }
}
