package com.example.kaiyuanchen.csci571_hw9;


import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements Spinner.OnItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String API_KEY = "AIzaSyDpD0SMyuaFuWvkBQPRvUyOFw6dJ9yjo2A";
    private SupportMapFragment mapFragment;
    private LatLng end;
    private String mode;
    private String[] modes;
    private Spinner modeList;
    private String from = "";
    private AutoCompleteTextView fromInput;
    private RequestQueue queue;
    private JsonObjectRequest request;
    private GoogleMap map;
    private AutocompleteAdapter autocompleteAdapter;
    private GeoDataClient mGeoDataClient;
    private Polyline polylineFinal;
    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        double lat = getArguments().getDouble("latitude");
        double lon = getArguments().getDouble("longitude");
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        LatLngBounds currLatLng = new LatLngBounds(new LatLng(lat - 0.1, lon - 0.1), new LatLng(lat + 0.1, lon + 0.1));
        autocompleteAdapter = new AutocompleteAdapter(getActivity(), mGeoDataClient, currLatLng, null);
        Resources res = getResources();
        modes = res.getStringArray(R.array.modes);
        modeList = view.findViewById(R.id.modeList);
        modeList.setOnItemSelectedListener(this);
        fromInput = view.findViewById(R.id.fromInput);
        fromInput.setAdapter(autocompleteAdapter);
        fromInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                from = fromInput.getText().toString();
                String url = getDirectionsUrl(from, mode);
                map.clear();
                end = new LatLng(getArguments().getDouble("desLat"), getArguments().getDouble("desLon"));
                map.addMarker(new MarkerOptions().position(end));
                showRoute(url);
            }
        });
//        fromInput.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map = googleMap;
                    end = new LatLng(getArguments().getDouble("desLat"), getArguments().getDouble("desLon"));
                    map.addMarker(new MarkerOptions().position(end));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(end,12.0f));
                }
            });
        }
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mode = modes[position];
        if(!from.equals("")) {
            String url = getDirectionsUrl(from, mode);
            map.clear();
            end = new LatLng(getArguments().getDouble("desLat"), getArguments().getDouble("desLon"));
            map.addMarker(new MarkerOptions().position(end));
            showRoute(url);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        mode = modes[0];
    }

    private String getDirectionsUrl(String start, String mode) {
        String origin = start.replace(" ", "+");
        System.out.println("https://maps.googleapis.com/maps/api/directions/json?origin=" + origin + "&destination=" +
                end.latitude + "," + end.longitude + "&mode=" + mode.toLowerCase() + "&key=" + API_KEY);
        return "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin + "&destination=" +
                end.latitude + "," + end.longitude + "&mode=" + mode.toLowerCase() + "&key=" + API_KEY;
    }

    private void showRoute(String url) {
        queue = Volley.newRequestQueue(getActivity());
        request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            DirectionsJSONParser parser = new DirectionsJSONParser();
                            List<List<HashMap<String, String>>> routes = parser.parse(response);
                            List<LatLng> points = new ArrayList<>();
                            PolylineOptions lineOptions = new PolylineOptions();
                            for (int i = 0; i < routes.size(); i++) {
                                List<HashMap<String, String>> path = routes.get(i);
                                for (int j = 0; j < path.size(); j++) {
                                    HashMap<String, String> point = path.get(j);
                                    double lat = Double.parseDouble(point.get("lat"));
                                    double lng = Double.parseDouble(point.get("lng"));
                                    LatLng position = new LatLng(lat, lng);
                                    if(i == 0 && j == 0){
                                        map.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                        builder.include(end);
                                        builder.include(position);
                                        LatLngBounds bounds = builder.build();
                                        int padding = 30;
                                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                        map.animateCamera(cu);
                                    }
                                    points.add(position);
                                }
                                lineOptions.addAll(points);
                                lineOptions.width(10);
                                lineOptions.color(Color.BLUE);
                            }
                            polylineFinal = map.addPolyline(lineOptions);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", "Fail to get json data from ip-api\n" + error.getMessage());
                    }
                }
        );
        queue.add(request);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
