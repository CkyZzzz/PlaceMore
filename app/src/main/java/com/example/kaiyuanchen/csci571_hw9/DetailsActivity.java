package com.example.kaiyuanchen.csci571_hw9;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity  implements InfoFragment.latlonListener{

    private String name;
    private String place_id;
    private Double desLat;
    private Double desLon;
    private String vicinity;
    private String placeIcon;
    private List<Fragment> fragmentList;
    private List<String> titleList;
    private ViewPager pager;
    private TabLayout tabs;
    private InfoFragment infoFrag;
    private PhotosFragment photosFrag;
    private MapFragment mapFrag;
    private ReviewsFragment reviewsFrag;
    private ImageButton favoriteInDetails;
    private ImageButton twitter;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar detailsToolbar = findViewById(R.id.details_toolbar);
        twitter = findViewById(R.id.twitter);
        favoriteInDetails = findViewById(R.id.favoriteInDetails);
        pref = getSharedPreferences("favorites", MODE_PRIVATE);
        editor = pref.edit();
        final Resources res = getResources();
        setSupportActionBar(detailsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        detailsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        place_id = intent.getStringExtra("place_id");
        desLat = Double.parseDouble(intent.getStringExtra("desLat"));
        desLon = Double.parseDouble(intent.getStringExtra("desLon"));
        vicinity = intent.getStringExtra("vicinity");
        placeIcon = intent.getStringExtra("placeIcon");

        if(!pref.getString(place_id, "").equals("")){
            Drawable heart = res.getDrawable(R.drawable.heart_fill_white);
            favoriteInDetails.setImageDrawable(heart);
        }else{
            Drawable heart = res.getDrawable(R.drawable.heart_outline_white);
            favoriteInDetails.setImageDrawable(heart);
        }
        getSupportActionBar().setTitle(name);

        titleList = new ArrayList<>();
        titleList.add("INFO");
        titleList.add("PHOTOS");
        titleList.add("MAP");
        titleList.add("REVIEWS");

        infoFrag = new InfoFragment();
        photosFrag = new PhotosFragment();
        mapFrag = new MapFragment();
        reviewsFrag = new ReviewsFragment();

        Bundle bundle = new Bundle();
        bundle.putString("place_id", place_id);
        infoFrag.setArguments(bundle);
        photosFrag.setArguments(bundle);
        reviewsFrag.setArguments(bundle);

        Bundle bundleMap = new Bundle();
        bundle.putDouble("latitude", desLat);
        bundle.putDouble("longitude", desLon);
        mapFrag.setArguments(bundleMap);

        fragmentList = new ArrayList<>();
        fragmentList.add(infoFrag);
        fragmentList.add(photosFrag);
        fragmentList.add(mapFrag);
        fragmentList.add(reviewsFrag);

        pager = findViewById(R.id.pagerDetails);
        tabs = findViewById(R.id.tabsDetails);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList, titleList);
        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);

        View v1 = LayoutInflater.from(this).inflate(R.layout.tabs, null);
        TextView textView1 = v1.findViewById(R.id.tabText);
        textView1.setText("INFO");
        ImageView imageView1 = v1.findViewById(R.id.tabIcon);
        imageView1.setImageDrawable(res.getDrawable(R.drawable.info_outline));
        tabs.getTabAt(0).setCustomView(v1);

        LinearLayout linearLayout1 = (LinearLayout) tabs.getChildAt(0);
        linearLayout1.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout1.setDividerDrawable(res.getDrawable(R.drawable.border));

        View v2 = LayoutInflater.from(this).inflate(R.layout.tabs, null);
        TextView textView2 = v2.findViewById(R.id.tabText);
        textView2.setText("PHOTOS");
        ImageView imageView2 = v2.findViewById(R.id.tabIcon);
        imageView2.setImageDrawable(res.getDrawable(R.drawable.photos));
        tabs.getTabAt(1).setCustomView(v2);

        View v3 = LayoutInflater.from(this).inflate(R.layout.tabs, null);
        TextView textView3 = v3.findViewById(R.id.tabText);
        textView3.setText("MAP");
        ImageView imageView3 = v3.findViewById(R.id.tabIcon);
        imageView3.setImageDrawable(res.getDrawable(R.drawable.maps));
        tabs.getTabAt(2).setCustomView(v3);

        View v4 = LayoutInflater.from(this).inflate(R.layout.tabs, null);
        TextView textView4 = v4.findViewById(R.id.tabText);
        textView4.setText("REVIEWS");
        ImageView imageView4 = v4.findViewById(R.id.tabIcon);
        imageView4.setImageDrawable(res.getDrawable(R.drawable.review));
        tabs.getTabAt(3).setCustomView(v4);

        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    @Override
    public void sendLatlon(final double desLat, final double desLon, final String url, final String website) {
        Bundle bundle = new Bundle();
        bundle.putDouble("desLat", desLat);
        bundle.putDouble("desLon", desLon);
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                String text = "Check out " + name + " located at " + vicinity + ". Website: ";
                String link = website.equals("") ? url : website;
                String hashtags = "TravelAndEntertainmentSearch";
                String tempUrl = "https://twitter.com/intent/tweet?text=" + text + "&url=" + link + "&hashtags=" + hashtags;
                intent.setData(Uri.parse(tempUrl));
                startActivity(intent);
            }
        });
        favoriteInDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pref.getString(place_id, "").equals("")){
                    Drawable heart = getResources().getDrawable(R.drawable.heart_fill_white);
                    favoriteInDetails.setImageDrawable(heart);
                    String content = "{\"placeName\":\"" + name +
                                     "\",\"placeIcon\":\"" + placeIcon +
                                     "\",\"placeAddress\":\"" + vicinity +
                                     "\",\"place_id\":\"" + place_id +
                                     "\",\"desLat\":\"" + desLat +
                                     "\",\"desLon\":\"" + desLon +
                                     "\",\"vicinity\":\"" + vicinity + "\"}";
                    System.out.println("content" + content);
                    editor.putString(place_id, content);
                    editor.commit();
                    Toast.makeText(DetailsActivity.this, name + " was added to favorites", Toast.LENGTH_SHORT).show();
                }else{
                    Drawable heart = getResources().getDrawable(R.drawable.heart_outline_white);
                    favoriteInDetails.setImageDrawable(heart);
                    editor.remove(place_id);
                    editor.apply();
                    Toast.makeText(DetailsActivity.this, name + " was removed from favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mapFrag.setArguments(bundle);
    }
}
