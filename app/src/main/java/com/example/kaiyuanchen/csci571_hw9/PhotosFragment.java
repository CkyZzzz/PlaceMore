package com.example.kaiyuanchen.csci571_hw9;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotosFragment extends Fragment {

    private static final String TAG = "getPhotos";
    private String place_id;
    private View view;
    private RequestQueue queue;
    private JsonObjectRequest request;
    private ListView listView;
    private List<Map<String, Object>> resource;
    private PhotoAdapter adapter;
    private TextView noPhotos;
    private GeoDataClient mGeoDataClient;
    public PhotosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_photos, container, false);
        place_id = getArguments().getString("place_id").toString();
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        resource = new ArrayList<>();
        listView = view.findViewById(R.id.photos);
        noPhotos = view.findViewById(R.id.noPhotos);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPhotos(place_id);
        adapter = new PhotoAdapter(
                getActivity(),
                resource,
                R.layout.photos,
                new String[]{},
                new int[]{}
        );
        listView.setAdapter(adapter);
    }

    private void getPhotos(String placeId) {
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                try {
                    PlacePhotoMetadataResponse photos = task.getResult();
                    // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                    PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                    if(photoMetadataBuffer.getCount() == 0){
                        listView.setVisibility(View.GONE);
                        noPhotos.setVisibility(View.VISIBLE);
                    }else{
                        listView.setVisibility(View.VISIBLE);
                        noPhotos.setVisibility(View.GONE);
                    }
                    // Get the first photo in the list.
                    Iterator<PlacePhotoMetadata> iterator = photoMetadataBuffer.iterator();
                    while(iterator.hasNext()){
                        PlacePhotoMetadata photoMetadata = iterator.next();
                        // Get the attribution text.
                        CharSequence attribution = photoMetadata.getAttributions();
                        // Get a full-size bitmap for the photo.
                        Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                try {
                                    PlacePhotoResponse photo = task.getResult();
                                    Bitmap bitmap = photo.getBitmap();
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("photo", bitmap);
                                    resource.add(map);
                                    adapter.notifyDataSetChanged();
                                }catch (Exception e){
                                    Log.e(TAG, e.getMessage());
                                }
                            }
                        });
                    }
                }catch (Exception e){
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }
}
