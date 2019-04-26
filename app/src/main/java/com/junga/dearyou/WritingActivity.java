package com.junga.dearyou;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.junga.dearyou.lib.FabLib;
import com.junga.dearyou.lib.FontLib;


import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.text.DateFormat.getDateTimeInstance;

public class WritingActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    int mode; // if mode = 1이면 update, 0이면 initial save.

    EditText title;
    EditText description;
    TextView save;
    TextView textCount;

    ImageView locker;

    FabLib fab;
    //variable for update
    int position;
    String diaryId;

    //set locker status
    boolean isLocked = true; //default is true.

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FontLib fontLib = new FontLib();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        title = (EditText) findViewById(R.id.editText_title);
        description = (EditText) findViewById(R.id.editText_description);
        save = (TextView) findViewById(R.id.TextView_save);
        locker = (ImageView) findViewById(R.id.icon_locker);
        textCount = (TextView) findViewById(R.id.text_count);

        save.setOnClickListener(this);
        locker.setOnClickListener(this);


        fontLib.setFont(this,"oleo_script_bold",title);
        fontLib.setFont(this,"inconsolata",description);
        fontLib.setFont(this,"inconsolata",textCount);

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                int letters = description.getText().toString().length();
                String showLetters = String.valueOf(letters) + " letters..";
                textCount.setText(showLetters);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        mode = getIntent().getIntExtra("mode", 100);
        if (mode == 1) {//edit mode
            String setTitle = getIntent().getStringExtra("title");
            String setContent = getIntent().getStringExtra("content");
            title.setText(setTitle);
            description.setText(setContent);

            position = getIntent().getIntExtra("position", -100);
            if (isLocked) {
                Glide.with(this).load(R.drawable.icon_lock).into(locker);
            } else {
                Glide.with(this).load(R.drawable.icon_open).into(locker);
            }

        } else { //initial save mode
            Glide.with(this).load(R.drawable.icon_lock).into(locker);
        }

        fab = new FabLib(WritingActivity.this);
        fab.setFabMenu();

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.TextView_save) {
            if (mode == 1) {
                updateDiary();
            } else {
                saveDiary();
            }
        } else if (v.getId() == R.id.icon_locker) {
            lockerChanged(); //Handler로 관리한다.
        }
    }

    //save at database.

    private void saveDiary() {

        String editTitle = title.getText().toString();
        String editDescription = description.getText().toString();
        String authorId = ((MyApp) getApplication()).getUser().getUserId();

        if (!isTextChecked()) {
            return;
        }
        ;

        if (auth.getCurrentUser() != null) {
            //set time
            Long time = System.currentTimeMillis();

            final DiaryItem data = new DiaryItem("", authorId, editTitle, editDescription, isLocked, time); //Since we don't know the diaryId yet, just leave it blank!
            db.collection("Diary")
                    .add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DiaryItem written with ID: " + documentReference.getId());
                            // pass the new diary.
                            diaryId = documentReference.getId();
                            saveDiaryId(diaryId);
                            data.setDiaryId(diaryId);
                            userDiarySave(data);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Something went wrong" + e.toString());
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
                        Log.d(TAG, "Successfully Updated diaryId");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void userDiarySave(final DiaryItem diaryItem) {

        final String userId = MyApp.getApp().getUser().userId;

        //1. userid(authorId)를 가지고 데이터 doc에 접근.
        DocumentReference userRef = db.collection("User").document(userId); //might be null. so need to do something with this.

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        UserItem user = document.toObject(UserItem.class);
                        ArrayList<DiaryItem> diaryList = user.getDiaries();
                        diaryList.add(diaryItem);
                        DocumentReference userRef = db.collection("User").document(userId); //might be null. so need to do something with this.

                        userRef.update("diaries", diaryList)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating documents.");
                                    }
                                });

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

        if (!isTextChecked()) {
            return;
        }
        ;

        if (auth.getCurrentUser() != null) {
            Long time = diary.get(position).getTime(); //기존의 시간 그대로이다.
            final DiaryItem data = new DiaryItem(diary.get(position).getDiaryId(), authorId, editTitle, editDescription, isLocked, time);
            db.collection("Diary").document(diary.get(position).getDiaryId())
                    .set(data)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "Success to update diary collection");
                            userDiaryUpdate(data);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "fail");
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
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating documents.");
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

    private void lockerChanged() {
        if (isLocked) { //When it was locked, unlock it.
            isLocked = false;
            Glide.with(this).load(R.drawable.icon_open).into(locker);
            Toast.makeText(this, "This diary will be shared", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "diary shared");
        } else { //When it was unlocked, lock it.
            isLocked = true;
            Glide.with(this).load(R.drawable.icon_lock).into(locker);
            Toast.makeText(this, "We will keep this diary private", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "diary locked");
        }
    }

    private boolean isTextChecked() {
        if (title.getText().toString().equals("") || description.getText().toString().equals("")) {
            Toast.makeText(this, "You've missed something!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (description.getText().toString().length() < 140) {
            Toast.makeText(this, "Diary should be longer than 140 letters..", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.closeFABMenu();
    }
}

