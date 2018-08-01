package com.example.kaiyuanchen.csci571_hw9;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final int TIMEOUT = 10000;
    private double latitude;
    private double longitude;
    private String keyword;
    private String category;
    private int distance;
    private int pattern;
    private String location;
    private RequestQueue queue;
    private JsonArrayRequest request;
    private ResultsListViewAdapter adapter;
    private ListView listView;
    private boolean isFirst;
    private List<Map<String, Object>> resource;
    private Button prev;
    private Button next;
    private int infoCount;
    private int startIndex;
    private int endIndex;
    private JSONArray responseArray;
    private ProgressDialog progressDialog;
    private TextView noResults;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar mainToolbar = findViewById(R.id.main_toolbar);
        listView = findViewById(R.id.list_view);
        noResults = this.findViewById(R.id.noResults);
        resource = new ArrayList<>();
        isFirst = true;
        startIndex = 0;
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Fetching results");
        progressDialog.show();
        prev = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next.setEnabled(true);
                endIndex = startIndex;
                startIndex -= 20;
                if(startIndex == 0) prev.setEnabled(false);
                formatJSONArray(responseArray);
                adapter.notifyDataSetChanged();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prev.setEnabled(true);
                startIndex += 20;
                endIndex = Math.min(startIndex + 20, infoCount);
                if(endIndex == infoCount) next.setEnabled(false);
                formatJSONArray(responseArray);
                adapter.notifyDataSetChanged();
            }
        });
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", -1.0);
        longitude = intent.getDoubleExtra("longitude", -1.0);
        keyword = intent.getStringExtra("keyword");
        category = intent.getStringExtra("category");
        distance = intent.getIntExtra("distance",-1);
        pattern = intent.getIntExtra("pattern",-1);
        if(pattern == 2) location = intent.getStringExtra("location");
        String URL_TARGET_PLACES = "http://homework8app-env.us-east-2.elasticbeanstalk.com/places?keyword=" + keyword + "&category=" + category +
                                    "&distance=" + distance + "&latitude=" + latitude + "&longitude=" + longitude + "&pattern=" + pattern +
                                    "&location=" + location;
        System.out.println(URL_TARGET_PLACES);
        queue = Volley.newRequestQueue(this);
        request = new JsonArrayRequest(Request.Method.GET, URL_TARGET_PLACES, null,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        responseArray = response;
                        infoCount = response.length() - 2;
                        endIndex = Math.min(infoCount, 20);
                        prev.setEnabled(false);
                        if(endIndex == infoCount) next.setEnabled(false);
                        adapter = new ResultsListViewAdapter(ResultsActivity.this,
                                formatJSONArray(response),
                                R.layout.item,
                                new String[]{"placeName", "placeAddress"},
                                new int[]{R.id.placeName, R.id.placeAddress},
                                ResultsActivity.this);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(ResultsActivity.this);
                        isFirst = false;
                        progressDialog.dismiss();
                    }
                },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", "Fail to get json data from server side for around places request\n" + error.toString());
                }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    private List<Map<String, Object>> formatJSONArray(JSONArray JSONArrayObject) {
        resource.clear();
        changeContent(JSONArrayObject);
        for(int i = startIndex ; i < endIndex && JSONArrayObject.length() != 0; i++){
            try {
                Map<String, Object> map = new HashMap<>();
                JSONObject piece = JSONArrayObject.getJSONObject(i);
                map.put("placeIcon", piece.getString("icon"));
                map.put("placeName", piece.getString("name"));
                map.put("placeAddress", piece.getString("vicinity"));
                map.put("place_id", piece.getString("place_id"));
                map.put("desLat", piece.getString("latitude"));
                map.put("desLon", piece.getString("longitude"));
                map.put("vicinity", piece.getString("vicinity"));
                resource.add(map);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return resource;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Map<String, Object> map = (Map<String, Object>) listView.getItemAtPosition(position);
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("name", map.get("placeName").toString());
            intent.putExtra("place_id", map.get("place_id").toString());
            intent.putExtra("desLat", map.get("desLat").toString());
            intent.putExtra("desLon", map.get("desLon").toString());
            intent.putExtra("vicinity", map.get("vicinity").toString());
            intent.putExtra("placeIcon", map.get("placeIcon").toString());
            startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isFirst) adapter.notifyDataSetChanged();
        isFirst = false;
    }

    public void changeContent(JSONArray JSONArrayObject){
        if(JSONArrayObject.length() == 2){
            listView.setVisibility(View.INVISIBLE);
            noResults.setVisibility(View.VISIBLE);
        }else{
            listView.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.INVISIBLE);
        }
    }
}
