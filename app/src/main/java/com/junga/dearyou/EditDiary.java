package com.junga.dearyou;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class EditDiary extends AppCompatActivity implements View.OnClickListener {
    private final int NO_POSITION = -1;
    private final int UPDATE = 1;
    private String title;
    private String content;
    private TextView textView_title;
    private TextView textView_content;
    private TextView textView_edit;
    private TextView textView_delete;
    private int position; //diary의 위치를 알려줌.

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //For delete diary
    private String diaryId; //For method delete diary , 2.Diarydata delete

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_diary);

        textView_title = findViewById(R.id.textView_title);
        textView_content = findViewById(R.id.textView_content);
        textView_edit = findViewById(R.id.textView_edit);
        textView_delete = findViewById(R.id.textView_delete);

        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        position = getIntent().getIntExtra("position", NO_POSITION);
        textView_title.setText(title);
        textView_content.setText(content);

        textView_edit.setOnClickListener(this);
        textView_delete.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.textView_edit) {
            if (position != NO_POSITION) {
                Intent intent = new Intent(EditDiary.this, WritingActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("content", content);
                intent.putExtra("position", position);
                intent.putExtra("mode", UPDATE); //update mode라는 것!
                startActivity(intent);
            }
        }
        if (v.getId() == R.id.textView_delete) {
            if (position != NO_POSITION) {
                deleteDiary();
            }
        }
    }

    private void deleteDiary() {


        //1.Userdata delete
        //유저를 찾는다. diaries 필드를 받아와서 업데이트한다.
        //Diarydata를 위해서 diaries 필드중 id를 받아와서 저장한다.

        db.collection("User").document(MyApp.getApp().getUser().getUserId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                UserItem user = document.toObject(UserItem.class);
                ArrayList<DiaryItem> diaries = user.getDiaries();
                diaryId = diaries.get(position).getDiaryId();
                diaries.remove(position);

                //update editted diary arraylist.
                db.collection("User").document(MyApp.getApp().getUser().getUserId())
                        .update("diaries", diaries)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("", "DocumentSnapshot successfully updated!");
                                hanlder.sendEmptyMessage(1);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("w", "Error updating documents.");

                    }
                });

            }
        });

        //Using handler

    }

    @SuppressLint("HandlerLeak")
    private
    Handler hanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {

                //2.Diarydata  delete
                //diaryId가 필요하다.
                //찾아서 document를 삭제한다.
                db.collection("Diary").document(diaryId)
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("", "Diary successfully deleted!");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("", "Failed to delete");
                    }
                });

                //3.MyApp data delete
                //diaries필드를 찾아서 업데이트 한다.
                //어떻게 찾아줄것인가?

                ArrayList<DiaryItem> diaries = MyApp.getApp().getUser().getDiaries();
                diaries.remove(position);
                MyApp.getApp().getUser().setDiaries(diaries);

                finish();
            }

        }
    };
}


