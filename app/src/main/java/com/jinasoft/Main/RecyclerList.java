package com.jinasoft.Main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerList extends RecyclerView.Adapter<RecyclerList.ViewHolder> {
    ArrayList<String> arrayList;

    RecyclerList(ArrayList<String> list) {
        this.arrayList = list;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = (TextView)itemView.findViewById(R.id.text1);
        }
    }

    @NonNull
    @Override
    public RecyclerList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item, parent, false);
        RecyclerList.ViewHolder vh = new RecyclerList.ViewHolder(view);
        return vh;
    } //레이아웃 생성  -> 뷰홀더를 생성

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = arrayList.get(position);
        holder.tv.setText(text);
    } //재활용 됐을때 실행되는 메서드

    @Override
    public int getItemCount() {
        return arrayList.size();
    } // 아이템 개수를 조회


}
