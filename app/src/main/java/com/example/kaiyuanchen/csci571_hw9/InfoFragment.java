package com.example.kaiyuanchen.csci571_hw9;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {

    private String place_id;
    private View view;
    private RequestQueue queue;
    private JsonObjectRequest request;
    private latlonListener myListener;
    public InfoFragment() {
        // Required empty public constructor
    }

    public interface latlonListener{
        void sendLatlon(double desLat, double desLon, String url, String website);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            myListener = (latlonListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_info, container, false);
        place_id = getArguments().getString("place_id").toString();
        String url = "http://homework8app-env.us-east-2.elasticbeanstalk.com/info?place_id=" + place_id;
        queue = Volley.newRequestQueue(getActivity());
        request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response.toString());
                        String address = response.optString("address", "");
                        String phone_number = response.optString("phone_number", "");
                        int price_level = response.optInt("price_level", -1);
                        double rating = response.optDouble("rating", -1.0);
                        String google_page = response.optString("google_page", "");
                        String website = response.optString("website", "");
                        Log.i("lalalalala", response.optDouble("latitude") + "    " + response.optDouble("longitude"));
                        myListener.sendLatlon(response.optDouble("latitude"), response.optDouble("longitude"), google_page, response.optString("website", ""));
                        if(!address.equals("")){
                            TableRow row0 = view.findViewById(R.id.row0);
                            row0.setVisibility(View.VISIBLE);
                            TextView addressView = view.findViewById(R.id.infoAddress);
                            addressView.setText(address);
                        }
                        if(!phone_number.equals("")){
                            TableRow row1 = view.findViewById(R.id.row1);
                            row1.setVisibility(View.VISIBLE);
                            TextView phoneNumView = view.findViewById(R.id.infoPhoneNum);
                            phoneNumView.setText(phone_number);
                        }
                        if(price_level != -1){
                            TableRow row2 = view.findViewById(R.id.row2);
                            row2.setVisibility(View.VISIBLE);
                            String dollar = "";
                            for(int i = 0 ; i < price_level; i++){
                                dollar += "$";
                            }
                            TextView priceLvlView = view.findViewById(R.id.infoPriceLvl);
                            priceLvlView.setText(dollar);
                        }
                        if(rating != -1.0){
                            TableRow row3 = view.findViewById(R.id.row3);
                            row3.setVisibility(View.VISIBLE);
                            RatingBar ratingView = view.findViewById(R.id.infoRating);
                            ratingView.setRating((float) rating);
                        }
                        if(!google_page.equals("")){
                            TableRow row4 = view.findViewById(R.id.row4);
                            row4.setVisibility(View.VISIBLE);
                            Button googlePageView = view.findViewById(R.id.infoGooglePage);
                            googlePageView.setText(google_page);
                        }
                        if(!website.equals("")){
                            TableRow row5 = view.findViewById(R.id.row5);
                            row5.setVisibility(View.VISIBLE);
                            Button websiteView = view.findViewById(R.id.infoWebsite);
                            websiteView.setText(website);
                        }
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "Fail to get details data from server side\n" + error.toString());
            }
        });
        queue.add(request);
        return view;
    }
}
