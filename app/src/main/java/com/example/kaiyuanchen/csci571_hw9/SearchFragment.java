package com.example.kaiyuanchen.csci571_hw9;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements RadioGroup.OnCheckedChangeListener, Spinner.OnItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    private AutoCompleteTextView keywordInput;
    private String keyword;
    private Spinner categoryList;
    private String[] categories;
    private String category;
    private EditText distanceInput;
    private int distance;
    private RadioGroup patterns;
    private AutoCompleteTextView locationInput;
    private String location;
    private int pattern;
    private double latitude;
    private double longitude;
    private Button searchBtn;
    private Button clearBtn;
    private TextView keywordError;
    private TextView locationError;
    private AutocompleteAdapter autocompleteAdapter;
    private GeoDataClient mGeoDataClient;

    public SearchFragment() {
        pattern = 1;
        distance = 10;
        keyword = "";
        location = "";
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View searchPartView = inflater.inflate(R.layout.fragment_search, container, false);
        Resources res = getResources();
        categories = res.getStringArray(R.array.categories);
        keywordInput = searchPartView.findViewById(R.id.keywordInput);
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        LatLngBounds currLatLng = new LatLngBounds(new LatLng(0, 0), new LatLng(0, 0));
        autocompleteAdapter = new AutocompleteAdapter(getActivity(), mGeoDataClient, currLatLng, null);
        keywordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                keywordError = getActivity().findViewById(R.id.keywordError);
                keywordError.setVisibility(View.GONE);
                keyword = keywordInput.getText().toString();
            }
        });

        categoryList = searchPartView.findViewById(R.id.categoryList);
        categoryList.setOnItemSelectedListener(this);
        distanceInput = searchPartView.findViewById(R.id.distanceInput);
        distanceInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                distance = Integer.parseInt(distanceInput.getText().toString().equals("") ? "10" : distanceInput.getText().toString());
            }
        });
        patterns = searchPartView.findViewById(R.id.patterns);
        patterns.setOnCheckedChangeListener(this);
        locationInput = searchPartView.findViewById(R.id.locationInput);
        locationInput.setAdapter(autocompleteAdapter);
        locationInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                locationError = getActivity().findViewById(R.id.locationError);
                locationError.setVisibility(View.GONE);
                location = locationInput.getText().toString();
            }
        });
        locationInput.setEnabled(false);
        searchBtn = searchPartView.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((!keyword.equals("") && pattern == 1) || (!keyword.equals("") && pattern == 2 && !location.equals(""))) &&
                        getArguments() != null){
                    Intent intent = new Intent(getActivity(), ResultsActivity.class);
                    latitude = getArguments().getDouble("latitude");
                    longitude = getArguments().getDouble("longitude");
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("keyword", keyword);
                    intent.putExtra("category", category);
                    intent.putExtra("distance", distance);
                    intent.putExtra("pattern", pattern);
                    if(pattern == 2) intent.putExtra("location", location);
                    startActivity(intent);
                }
                if(getArguments() == null){
                    Toast.makeText(getActivity(), R.string.latlon_access_required, Toast.LENGTH_SHORT).show();
                }

                if(keyword.equals("") || (pattern == 2 && location.equals(""))){
                    Toast.makeText(getActivity(), "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
                }

                if(keyword.equals("")){
                    keywordError = getActivity().findViewById(R.id.keywordError);
                    keywordError.setVisibility(View.VISIBLE);
                }
                if(pattern == 2 && location.equals("")){
                    locationError = getActivity().findViewById(R.id.locationError);
                    locationError.setVisibility(View.VISIBLE);
                }
            }
        });
        clearBtn = searchPartView.findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keywordInput.setText("");
                locationInput.setText("");
                RadioButton radioBtn1 = getActivity().findViewById(R.id.curBtn);
                radioBtn1.setChecked(true);
                pattern = 1;
                distanceInput.setText("");
                distance = 10;
                categoryList.setSelection(0);
                keywordError = getActivity().findViewById(R.id.keywordError);
                locationError = getActivity().findViewById(R.id.locationError);
                keywordError.setVisibility(View.GONE);
                locationError.setVisibility(View.GONE);
            }
        });
        return searchPartView;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.curBtn:
                pattern = 1;
                locationInput.setText("");
                locationInput.setEnabled(false);
                locationError = getActivity().findViewById(R.id.locationError);
                locationError.setVisibility(View.GONE);
                break;
            case R.id.cusBtn:
                pattern = 2;
                locationInput.setEnabled(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        category = categories[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        category = categories[0];
    }
}
