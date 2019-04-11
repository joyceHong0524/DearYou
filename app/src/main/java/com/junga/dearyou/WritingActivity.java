package com.junga.dearyou;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firestore.v1.DocumentTransform;


import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.text.DateFormat.getDateTimeInstance;

public class WritingActivity extends AppCompatActivity implements View.OnClickListener {

    int mode; // if mode = 1이면 update, 0이면 initial save.

    EditText title;
    EditText description;
    TextView save;
    TextView cancel;
    TextView titleAbove;

    //variable for update
    int position;
    String diaryId;


    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        title = (EditText) findViewById(R.id.editText_title);
        description = (EditText) findViewById(R.id.editText_description);
        save = (TextView) findViewById(R.id.TextView_save);
        cancel = (TextView) findViewById(R.id.cancel);
        titleAbove = (TextView) findViewById(R.id.titleAbove);

        save.setOnClickListener(this);
        cancel.setOnClickListener(this);

        mode = getIntent().getIntExtra("mode", 100);

        if (mode == 1) {
            String setTitle = getIntent().getStringExtra("title");
            String setContent = getIntent().getStringExtra("content");
            title.setText(setTitle);
            description.setText(setContent);

            position = getIntent().getIntExtra("position", -100);
            Log.d("position", String.valueOf(position));
        }


    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.TextView_save) {
            if (mode == 1) {
                updateDiary();
            } else {
                saveDiary();
            }
        } else if (v.getId() == R.id.cancel) {

        }

    }

    //save at database.

    private void saveDiary() {

        String editTitle = title.getText().toString();
        String editDescription = description.getText().toString();
        String authorId = ((MyApp) getApplication()).getUser().getUserId();


        if (auth.getCurrentUser() != null) {
            Long time = System.currentTimeMillis();
            final DiaryItem data = new DiaryItem("", authorId, editTitle, editDescription, false,time); //Since we don't know the diaryId yet, just leave it blank!
            db.collection("Diary")
                    .add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d("hi", "DocumentSnapshot written with ID: " + documentReference.getId());
                    // pass the new diary.
                    diaryId = documentReference.getId();
                    saveDiaryId(diaryId);
                    data.setDiaryId(diaryId);
                    userDiarySave(data);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("error", e.toString());
                }
            });
        } else {
            return;
        }
    }


    private void saveDiaryId(String diaryId) {
        db.collection("Diary").document(diaryId).update("diaryId", diaryId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("fd", "updated diaryId");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

//        Map<String,Object> data = new HashMap<>();
//        data.put("title",editTitle);
//        data.put("description",editDescription);


    private void userDiarySave(final DiaryItem diaryItem) {

        final String userId = MyApp.getApp().getUser().userId;

        //1. userid(authorId)를 가지고 데이터 doc에 접근.
        DocumentReference userRef = db.collection("User").document(userId); //might be null. so need to do something with this.

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
//                 Log.d("document log",document.toString());
                    if (document.exists()) {
                        UserItem user = document.toObject(UserItem.class);
                        ArrayList<DiaryItem> diaryList = user.getDiaries();
                        diaryList.add(diaryItem);
                        DocumentReference userRef = db.collection("User").document(userId); //might be null. so need to do something with this.

                        userRef.update("diaries", diaryList)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("", "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("w", "Error updating documents.");
                                    }
                                });

                        //NOT HERE, RELOAD AT THE MAIN ACTIVITY

                        //myapp 클래스에 업데이트하기
                        ArrayList<DiaryItem> diaries = MyApp.getApp().getUser().getDiaries();
                        diaries.add(diaryItem);
                        MyApp.getApp().getUser().setDiaries(diaries);

                        Toast.makeText(WritingActivity.this, "Saved :D", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });


    }

    //2. Update diary


    private void updateDiary() {

        String editTitle = title.getText().toString();
        String editDescription = description.getText().toString();
        String authorId = MyApp.getApp().getUser().getUserId();
        ArrayList<DiaryItem> diary = MyApp.getApp().getUser().getDiaries();
        if (auth.getCurrentUser() != null) {
            Long time = diary.get(position).getTime(); //기존의 시간 그대로이다.
            final DiaryItem data = new DiaryItem(diary.get(position).getDiaryId(), authorId, editTitle, editDescription, false,time);
            db.collection("Diary").document(diary.get(position).getDiaryId())
                    .set(data)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("df", "Success to update diary collection");
                            userDiaryUpdate(data);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("df", "fail");
                }
            });
        } else {
            return;
        }

    }

    private void userDiaryUpdate(final DiaryItem data) {
        final String userId = MyApp.getApp().getUser().userId;

        //1. userid(authorId)를 가지고 데이터 doc에 접근.
        final DocumentReference userRef = db.collection("User").document(userId); //might be null. so need to do something with this.

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
//                 Log.d("document log",document.toString());
                    if (document.exists()) {
                        UserItem user = document.toObject(UserItem.class);
                        ArrayList<DiaryItem> diaryList = user.getDiaries();
                        diaryList.set(position, data);
                        userRef.update("diaries", diaryList)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("", "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("w", "Error updating documents.");
                                    }
                                });

                        //NOT HERE, RELOAD AT THE MAIN ACTIVITY

                        //myapp 클래스에 업데이트하기
                        ArrayList<DiaryItem> diaries = MyApp.getApp().getUser().getDiaries();
                        diaries.set(position, data);
                        MyApp.getApp().getUser().setDiaries(diaries);

                        Toast.makeText(WritingActivity.this, "Saved :D", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });

    }

    private String getDiaryDate(long timestamp) {
        DateFormat dateFormat = getDateTimeInstance();
        Date netDate = (new Date(timestamp));
        return dateFormat.format(netDate);
    }
}

