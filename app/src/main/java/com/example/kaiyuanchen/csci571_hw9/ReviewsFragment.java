package com.example.kaiyuanchen.csci571_hw9;


import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewsFragment extends Fragment implements Spinner.OnItemSelectedListener{

    private static final int TIMEOUT = 10000;
    private String place_id;
    private TextView author;
    private RatingBar rating;
    private TextView time;
    private TextView text;
    private RequestQueue queue;
    private JsonObjectRequest request;
    private ReviewsListViewAdapter adapter;
    private ListView listView;
    private List<Map<String, Object>> resource;
    private JSONArray yelpReviews;
    private JSONArray googleReviews;
    private Spinner companyList;
    private Spinner sortList;
    private String company;
    private JSONArray companyReviews;
    private String[] companies;
    private String sortMethod;
    private String[] sortMethods;
    private boolean isGoogleReviews;
    private boolean isInitialization;
    private TextView noReviews;
    public ReviewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_reviews, container, false);
        place_id = getArguments().getString("place_id").toString();
        resource = new ArrayList<>();
        isInitialization = true;
        noReviews = view.findViewById(R.id.noReviews);
        Resources res = getResources();
        companies = res.getStringArray(R.array.companies);
        sortMethods = res.getStringArray(R.array.sortMethods);
        listView = view.findViewById(R.id.list_view_reviews);
        author = view.findViewById(R.id.reviewAuthor);
        rating = view.findViewById(R.id.reviewRating);
        time = view.findViewById(R.id.reviewTime);
        text = view.findViewById(R.id.reviewText);
        companyList = view.findViewById(R.id.companyList);
        companyList.setOnItemSelectedListener(this);
        sortList = view.findViewById(R.id.sortList);
        sortList.setOnItemSelectedListener(this);
        String url = "http://homework8app-env.us-east-2.elasticbeanstalk.com/reviews?place_id=" + place_id;
        System.out.println(url);
        queue = Volley.newRequestQueue(getActivity());
        request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response.toString());
                        try {
                            googleReviews = response.getJSONArray("googleReviews");
                            yelpReviews = response.getJSONArray("yelpReviews");
                            sortMethod = "Default order";
                            companyReviews = new JSONArray(googleReviews.toString());
                            isGoogleReviews = true;
                            formatJSONArray(companyReviews, sortMethod);
                            adapter = new ReviewsListViewAdapter(getActivity(),
                                    resource,
                                    R.layout.reviews,
                                    new String[]{"reviewAuthor", "reviewText"},
                                    new int[]{R.id.reviewAuthor, R.id.reviewText});
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Map<String, Object> map = (Map<String, Object>) listView.getItemAtPosition(position);
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_VIEW);
                                    String tempUrl = (String) map.get("url");
                                    if(!tempUrl.equals("")){
                                        intent.setData(Uri.parse((String) map.get("url")));
                                        startActivity(intent);
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "Fail to get reviews data from server side\n" + error.toString());
            }
        });
        queue.add(request);
        return view;
    }

    private void formatJSONArray(JSONArray JSONArrayObject, String sortMethod) {
        resource.clear();
        if(changeContent()) {
            if (sortMethod.equals("Highest rating")) {
                List<JSONObject> tempList = new ArrayList<>();
                for (int i = 0; i < JSONArrayObject.length(); i++) {
                    try {
                        tempList.add(JSONArrayObject.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Collections.sort(tempList, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject a, JSONObject b) {
                        int ratingA = 0, ratingB = 0;
                        try {
                            ratingA = a.getInt("rating");
                            ratingB = b.getInt("rating");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return ratingB - ratingA;
                    }
                });
                JSONArrayObject = new JSONArray();
                for (int i = 0; i < tempList.size(); i++) {
                    JSONArrayObject.put(tempList.get(i));
                }
            } else if (sortMethod.equals("Lowest rating")) {
                List<JSONObject> tempList = new ArrayList<>();
                for (int i = 0; i < JSONArrayObject.length(); i++) {
                    try {
                        tempList.add(JSONArrayObject.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Collections.sort(tempList, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject a, JSONObject b) {
                        int ratingA = 0, ratingB = 0;
                        try {
                            ratingA = a.getInt("rating");
                            ratingB = b.getInt("rating");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return ratingA - ratingB;
                    }
                });
                JSONArrayObject = new JSONArray();
                for (int i = 0; i < tempList.size(); i++) {
                    JSONArrayObject.put(tempList.get(i));
                }
            } else if (sortMethod.equals("Most recent")) {
                List<JSONObject> tempList = new ArrayList<>();
                for (int i = 0; i < JSONArrayObject.length(); i++) {
                    try {
                        tempList.add(JSONArrayObject.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Collections.sort(tempList, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject a, JSONObject b) {
                        int gA = 0, gB = 0;
                        String yA = "2017-11-20 09:06:22", yB = "2017-11-20 09:06:22";
                        try {
                            if (isGoogleReviews) {
                                gA = a.getInt("time");
                                gB = b.getInt("time");
                            } else {
                                yA = a.getString("time");
                                yB = b.getString("time");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (isGoogleReviews) {
                            return gB - gA;
                        } else {
                            return compareDateForYelp(yB, yA);
                        }
                    }
                });
                JSONArrayObject = new JSONArray();
                for (int i = 0; i < tempList.size(); i++) {
                    JSONArrayObject.put(tempList.get(i));
                }
            } else if (sortMethod.equals("Least recent")) {
                List<JSONObject> tempList = new ArrayList<>();
                for (int i = 0; i < JSONArrayObject.length(); i++) {
                    try {
                        tempList.add(JSONArrayObject.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Collections.sort(tempList, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject a, JSONObject b) {
                        int gA = 0, gB = 0;
                        String yA = "2017-11-20 09:06:22", yB = "2017-11-20 09:06:22";
                        try {
                            if (isGoogleReviews) {
                                gA = a.getInt("time");
                                gB = b.getInt("time");
                            } else {
                                yA = a.getString("time");
                                yB = b.getString("time");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (isGoogleReviews) {
                            return gA - gB;
                        } else {
                            return compareDateForYelp(yA, yB);
                        }
                    }
                });
                JSONArrayObject = new JSONArray();
                for (int i = 0; i < tempList.size(); i++) {
                    JSONArrayObject.put(tempList.get(i));
                }
            }
            for (int i = 0; i < JSONArrayObject.length(); i++) {
                try {
                    Map<String, Object> map = new HashMap<>();
                    JSONObject piece = JSONArrayObject.getJSONObject(i);
                    map.put("reviewAuthor", piece.optString("author"));
                    map.put("url", piece.optString("url", ""));
                    map.put("reviewerIcon", piece.optString("profilePhoto", ""));
                    map.put("reviewRating", piece.optDouble("rating"));
                    map.put("reviewTime", piece.optString("time"));
                    map.put("reviewText", piece.optString("text"));
                    resource.add(map);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()) {
            case R.id.companyList:
                company = companies[position];
                if(company.equals("Google reviews") && !isInitialization){
                    try {
                        companyReviews = googleReviews == null ? new JSONArray("[]") : new JSONArray(googleReviews.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    isGoogleReviews = true;
                    formatJSONArray(companyReviews, sortMethod);
                    adapter.notifyDataSetChanged();
                }
                if(company.equals("Yelp reviews") && !isInitialization){
                    try {
                        companyReviews = yelpReviews == null ? new JSONArray("[]") : new JSONArray(yelpReviews.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    isGoogleReviews = false;
                    formatJSONArray(companyReviews, sortMethod);
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.sortList:
                sortMethod = sortMethods[position];
                if(sortMethod.equals("Default order") && !isInitialization){
                    sortMethod = "Default order";
                    try {
                        if(isGoogleReviews){
                            companyReviews = new JSONArray(googleReviews.toString());
                        }else{
                            companyReviews = new JSONArray(yelpReviews.toString());
                        }
                    } catch (JSONException e) {
                            e.printStackTrace();
                    }
                    formatJSONArray(companyReviews, sortMethod);
                    adapter.notifyDataSetChanged();
                }
                if(sortMethod.equals("Highest rating")  && !isInitialization){
                    sortMethod = "Highest rating";
                    formatJSONArray(companyReviews, sortMethod);
                    adapter.notifyDataSetChanged();
                }
                if(sortMethod.equals("Lowest rating")  && !isInitialization){
                    sortMethod = "Lowest rating";
                    formatJSONArray(companyReviews, sortMethod);
                    adapter.notifyDataSetChanged();
                }
                if(sortMethod.equals("Most recent")  && !isInitialization){
                    sortMethod = "Most recent";
                    formatJSONArray(companyReviews, sortMethod);
                    adapter.notifyDataSetChanged();
                }
                if(sortMethod.equals("Least recent")  && !isInitialization){
                    sortMethod = "Least recent";
                    formatJSONArray(companyReviews, sortMethod);
                    adapter.notifyDataSetChanged();
                }
                isInitialization = false;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        switch(parent.getId()) {
            case R.id.companyList:
                break;
            case R.id.sortList:
                break;
        }
    }

    private int compareDateForYelp(String a, String b){
        String[] dateA = a.split(" ")[0].split("-");
        String[] dateB = b.split(" ")[0].split("-");
        String[] timeA = a.split(" ")[1].split(":");
        String[] timeB = b.split(" ")[1].split(":");
        if(Integer.parseInt(dateA[0]) != Integer.parseInt(dateB[0])){
            return Integer.parseInt(dateA[0]) - Integer.parseInt(dateB[0]);
        }else if(Integer.parseInt(dateA[1]) != Integer.parseInt(dateB[1])){
            return Integer.parseInt(dateA[1]) - Integer.parseInt(dateB[1]);
        }else if(Integer.parseInt(dateA[2]) != Integer.parseInt(dateB[2])){
            return Integer.parseInt(dateA[2]) - Integer.parseInt(dateB[2]);
        }else if(Integer.parseInt(timeA[0]) != Integer.parseInt(timeB[0])){
            return Integer.parseInt(timeA[0]) - Integer.parseInt(timeB[0]);
        }else if(Integer.parseInt(timeA[1]) != Integer.parseInt(timeB[1])){
            return Integer.parseInt(timeA[1]) - Integer.parseInt(timeB[1]);
        }else{
            return Integer.parseInt(timeA[2]) - Integer.parseInt(timeB[2]);
        }
    }

    public boolean changeContent(){
        if(companyReviews.length() == 0){
            listView.setVisibility(View.INVISIBLE);
            noReviews.setVisibility(View.VISIBLE);
            return false;
        }else{
            listView.setVisibility(View.VISIBLE);
            noReviews.setVisibility(View.INVISIBLE);
            return true;
        }
    }
}
