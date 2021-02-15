package com.jinasoft.Main;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class SecondFragment extends Fragment{

    private ArrayList<String> list = new ArrayList<>();
    public int page;
    public RecyclerView.LayoutManager layoutManager;
    Context context;

    public static SecondFragment newInstance(int page) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        page = getArguments().getInt("someInt", 0);
        for (int i = 0; i < 60; i++){
            if (i > 30)
            list.add("반복문" + i + "번째");

            Log.d("확인" ,"" + i);
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sub2, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.ChartList);


        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerList adapter = new RecyclerList(list);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
