package com.example.kaiyuanchen.csci571_hw9;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int PERMISSION_REQUEST_LATLON = 100;
    private List<Fragment> fragmentList;
    private List<String>titleList;
    private ViewPager pager;
    private TabLayout tabs;
    private SearchFragment searchFrag;
    private FavoriteFragment favoriteFrag;
    private boolean isFirst;
    private View mlayout;
    private RequestQueue queue;
    private JsonObjectRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);
        mlayout = this.findViewById(R.id.content);
        titleList = new ArrayList<>();
        titleList.add("SEARCH");
        titleList.add("FAVORITES");

        fragmentList = new ArrayList<>();
        searchFrag = new SearchFragment();
        favoriteFrag = new FavoriteFragment();
        fragmentList.add(searchFrag);
        fragmentList.add(favoriteFrag);

        pager = findViewById(R.id.pagerMain);
        tabs = findViewById(R.id.tabsMain);
        isFirst = true;

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList, titleList);
		pager.setAdapter(adapter);
		tabs.setupWithViewPager(pager);
        Resources res = getResources();

        View v1 = LayoutInflater.from(this).inflate(R.layout.tabs, null);
        TextView textView1 = v1.findViewById(R.id.tabText);
        textView1.setText("SEARCH");
        ImageView imageView1 = v1.findViewById(R.id.tabIcon);
        imageView1.setImageDrawable(res.getDrawable(R.drawable.search));
        tabs.getTabAt(0).setCustomView(v1);

        LinearLayout linearLayout = (LinearLayout) tabs.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(res.getDrawable(R.drawable.border));

        View v2 = LayoutInflater.from(this).inflate(R.layout.tabs, null);
        TextView textView2 = v2.findViewById(R.id.tabText);
        textView2.setText("FAVORITES");
        ImageView imageView2 = v2.findViewById(R.id.tabIcon);
        imageView2.setImageDrawable(res.getDrawable(R.drawable.heart_fill_white));
        tabs.getTabAt(1).setCustomView(v2);
        showLatLon();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isFirst) {
            favoriteFrag.formatPrefData();
            favoriteFrag.adapter.notifyDataSetChanged();
        }
        isFirst = false;
    }

    private void showLatLon() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            String url = "http://ip-api.com/json";
            queue = Volley.newRequestQueue(this);
            request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                double latitude = response.getDouble("lat");
                                double longitude = response.getDouble("lon");
                                Bundle bundle = new Bundle();
                                bundle.putDouble("latitude", latitude);
                                bundle.putDouble("longitude", longitude);
                                searchFrag.setArguments(bundle);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error", "Fail to get latitude and longitude\n" + error.toString());
                }
            });
            queue.add(request);
        } else {
            requestLatLonPermission();
        }
    }

    private void requestLatLonPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Snackbar.make(mlayout, R.string.latlon_access_required, Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(mlayout, R.string.latlon_permission_not_available, Snackbar.LENGTH_SHORT).show();
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LATLON);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_LATLON) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String url = "http://ip-api.com/json";
                queue = Volley.newRequestQueue(this);
                request = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>(){
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    double latitude = response.getDouble("lat");
                                    double longitude = response.getDouble("lon");
                                    Bundle bundle = new Bundle();
                                    bundle.putDouble("latitude", latitude);
                                    bundle.putDouble("longitude", longitude);
                                    searchFrag.setArguments(bundle);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", "Fail to get latitude and longitude\n" + error.toString());
                    }
                });
                queue.add(request);
            } else {
                Snackbar.make(mlayout, R.string.latlon_permission_denied,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
