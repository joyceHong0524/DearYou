package com.junga.dearyou;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    MyDiaryAdapter adapter;
    Activity mActivity;


    public MyDiaryAdapter(Activity mActivity,Context context, ArrayList<DiaryItem> diaryList) {
        this.context = context;
        this.diaryList = diaryList;
        adapter = this;
        this.mActivity = mActivity;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_diary,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            final DiaryItem diary = diaryList.get(position);
            holder.title.setText(diary.title);
            holder.date.setText("3 days before");
            holder.content.setText(diary.content);

            holder.myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    //pass current diaryItem
                startEditActivity(position,diary.getTitle(),diary.getContent());
                }
            });


    }

    @Override
    public int getItemCount() {
        return (diaryList != null ? diaryList.size() : 0);
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView date;
        TextView content;
        View myView;

        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
            title = (TextView)  itemView.findViewById(R.id.textView_title);
            date = (TextView) itemView.findViewById(R.id.textView_date);
            content = (TextView) itemView.findViewById(R.id.textView_content);
        }
    }

    //custom methods.

    public void insertItem (DiaryItem diaryItem, int insertIndex){
        diaryList.add(insertIndex,diaryItem);
        adapter.notifyItemInserted(insertIndex);
    }

    public void startEditActivity(int position,String title, String content){

        Intent intent = new Intent(context, EditDiary.class);
        intent.putExtra("position",position);
        intent.putExtra("title",title);
        intent.putExtra("content",content);
        mActivity.startActivity(intent);
    }

}
