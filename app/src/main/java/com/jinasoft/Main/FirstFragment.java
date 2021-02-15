package com.jinasoft.Main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class FirstFragment extends Fragment{

    private ArrayList<String> list = new ArrayList<>();
    public int page;
    public RecyclerView.LayoutManager layoutManager;
    Context context;

    public static FirstFragment newInstance(int page) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        page = getArguments().getInt("someInt", 0);
        for (int i = 0; i < 30; i++){
            list.add("반복문" + i + "번째");

            Log.d("확인" ,"" + i);
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sub1, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.ChartList);


        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerList adapter = new RecyclerList(list);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
