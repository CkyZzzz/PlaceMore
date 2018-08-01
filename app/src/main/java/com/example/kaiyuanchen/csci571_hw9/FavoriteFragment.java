package com.example.kaiyuanchen.csci571_hw9;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment implements AdapterView.OnItemClickListener  {

    public FavoriteListViewAdapter adapter;
    private static ListView listView;
    private List<Map<String, Object>> resourse;
    private static SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static TextView noFavorites;
    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        pref = getActivity().getSharedPreferences("favorites", Context.MODE_PRIVATE);
        editor = pref.edit();
        resourse = new ArrayList<>();
        listView = view.findViewById(R.id.list_view_favorites);
        noFavorites = view.findViewById(R.id.noFavorites);
        adapter = new FavoriteListViewAdapter(getActivity(),
                formatPrefData(),
                R.layout.favorites,
                new String[]{"placeName", "placeAddress"},
                new int[]{R.id.favoriteName, R.id.favoriteAddress});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        return view;
    }

    public List<Map<String, Object>> formatPrefData() {
        resourse.clear();
        changeContent();
        Iterator iterator = pref.getAll().keySet().iterator();
        while(iterator.hasNext()){
            Map<String, Object> map = new HashMap<>();
            String data = pref.getString(iterator.next().toString(),"");
            data = data.substring(2, data.length() - 2);
            String[] tempArr = data.split("\",\"");
            for(int i = 0 ; i < tempArr.length; i++){
                map.put(tempArr[i].split("\":\"")[0], tempArr[i].split("\":\"")[1]);
            }
            resourse.add(map);
        }
        return resourse;
    }

    public static void changeContent(){
        if(pref.getAll().toString().equals("{}")){
            listView.setVisibility(View.INVISIBLE);
            noFavorites.setVisibility(View.VISIBLE);
        }else{
            listView.setVisibility(View.VISIBLE);
            noFavorites.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Map<String, Object> map = (Map<String, Object>) listView.getItemAtPosition(position);
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra("name", map.get("placeName").toString());
        intent.putExtra("place_id", map.get("place_id").toString());
        intent.putExtra("desLat", map.get("desLat").toString());
        intent.putExtra("desLon", map.get("desLon").toString());
        intent.putExtra("vicinity", map.get("vicinity").toString());
        intent.putExtra("placeIcon", map.get("placeIcon").toString());
        startActivity(intent);
    }
}
