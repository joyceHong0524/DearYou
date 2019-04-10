package com.junga.dearyou;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyDiaryAdapter extends RecyclerView.Adapter<MyDiaryAdapter.ViewHolder> {

    Context context;
    ArrayList<DiaryItem> diaryList;

    public MyDiaryAdapter(Context context, ArrayList<DiaryItem> diaryList) {
        this.context = context;
        this.diaryList = diaryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_diary,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DiaryItem diary = diaryList.get(position);
            holder.title.setText(diary.title);
            holder.date.setText("3 days before");
            holder.content.setText(diary.content);
    }

    @Override
    public int getItemCount() {
        return (diaryList != null ? diaryList.size() : 0);
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView date;
        TextView content;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView)  itemView.findViewById(R.id.textView_title);
            date = (TextView) itemView.findViewById(R.id.textView_date);
            content = (TextView) itemView.findViewById(R.id.textView_content);
        }
    }
}
